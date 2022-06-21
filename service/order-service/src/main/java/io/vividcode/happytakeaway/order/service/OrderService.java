package io.vividcode.happytakeaway.order.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.vividcode.happytakeaway.delivery.api.v1.Address;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryTaskCreatedEvent;
import io.vividcode.happytakeaway.event.EventService;
import io.vividcode.happytakeaway.order.api.v1.ConfirmOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.ConfirmOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersResponse;
import io.vividcode.happytakeaway.order.api.v1.GetOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.GetOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.MarkAsReadyForDeliveryRequest;
import io.vividcode.happytakeaway.order.api.v1.MarkAsReadyForDeliveryResponse;
import io.vividcode.happytakeaway.order.api.v1.Order;
import io.vividcode.happytakeaway.order.api.v1.OrderItem;
import io.vividcode.happytakeaway.order.api.v1.OrdersResult;
import io.vividcode.happytakeaway.order.api.v1.PageRequest;
import io.vividcode.happytakeaway.order.api.v1.event.OrderConfirmedEvent;
import io.vividcode.happytakeaway.order.api.v1.event.OrderCreatedEvent;
import io.vividcode.happytakeaway.order.api.v1.event.OrderDetails;
import io.vividcode.happytakeaway.order.entity.LineItemEntity;
import io.vividcode.happytakeaway.order.entity.OrderEntity;
import io.vividcode.happytakeaway.order.entity.OrderStatus;
import io.vividcode.happytakeaway.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderService {

  @Inject OrderRepository orderRepository;

  @Inject EntityManager entityManager;

  @Inject EventService eventService;

  @Transactional
  @Timed(
      value = "order.created",
      extraTags = {"entity", "order"},
      description = "Created orders",
      histogram = true)
  public CreateOrderResponse createOrder(CreateOrderRequest request) {
    OrderEntity order =
        OrderEntity.builder()
            .userId(request.getUserId())
            .restaurantId(request.getRestaurantId())
            .status(OrderStatus.CREATED)
            .lineItems(this.buildLineItems(request))
            .build();
    this.orderRepository.persist(order);
    this.eventService.publish(order, this.orderCreatedEvent(order));
    return CreateOrderResponse.newBuilder().setOrderId(order.getId()).build();
  }

  @Transactional
  public GetOrderResponse getOrder(GetOrderRequest request) {
    return this.orderRepository
        .findByIdOptional(request.getOrderId())
        .map(
            order ->
                GetOrderResponse.newBuilder()
                    .setOrderId(order.getId())
                    .setRestaurantId(order.getRestaurantId())
                    .setUserId(order.getUserId())
                    .addAllItems(this.decodeOrderItems(order))
                    .build())
        .orElseThrow(() -> ServiceHelper.orderNotFound(request.getOrderId()));
  }

  @Transactional
  @Counted(
      value = "order.confirmed",
      extraTags = {"entity", "order"},
      description = "Confirm order")
  public ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request) {
    String orderId = request.getOrderId();
    boolean result =
        this.orderRepository
            .findByIdOptional(orderId)
            .map(
                order -> {
                  order.setStatus(OrderStatus.CONFIRMED);
                  this.orderRepository.persist(order);
                  this.eventService.publish(order, this.orderConfirmedEvent(orderId));
                  return true;
                })
            .orElse(false);
    return ConfirmOrderResponse.newBuilder().setOrderId(orderId).setResult(result).build();
  }

  @Transactional
  public MarkAsReadyForDeliveryResponse markAsReadyForDelivery(
      MarkAsReadyForDeliveryRequest request) {
    String orderId = request.getOrderId();
    boolean result =
        this.orderRepository
            .findByIdOptional(orderId)
            .map(
                order -> {
                  if (order.getStatus() != OrderStatus.CONFIRMED) {
                    throw new InvalidOrderStatusException(OrderStatus.CONFIRMED, order.getStatus());
                  }
                  order.setStatus(OrderStatus.READY_FOR_DELIVERY);
                  this.orderRepository.persist(order);
                  DeliveryTask deliveryTask =
                      DeliveryTask.builder()
                          .id(UUID.randomUUID().toString())
                          .orderId(order.getId())
                          .restaurantId(order.getRestaurantId())
                          .userId(order.getUserId())
                          .restaurantAddress(
                              Address.builder()
                                  .lng(request.getPickupAddress().getLng())
                                  .lat(request.getPickupAddress().getLat())
                                  .build())
                          .build();
                  this.eventService.publish(
                      deliveryTask, this.deliveryTaskCreatedEvent(deliveryTask));
                  return true;
                })
            .orElse(false);
    return MarkAsReadyForDeliveryResponse.newBuilder()
        .setOrderId(orderId)
        .setResult(result)
        .build();
  }

  private OrderCreatedEvent orderCreatedEvent(OrderEntity order) {
    return new OrderCreatedEvent(
        OrderDetails.builder()
            .orderId(order.getId())
            .userId(order.getUserId())
            .restaurantId(order.getRestaurantId())
            .items(
                order.getLineItems().stream()
                    .map(
                        lineItem ->
                            OrderDetails.OrderItem.builder()
                                .itemId(lineItem.getItemId())
                                .quantity(lineItem.getQuantity())
                                .price(lineItem.getPrice())
                                .build())
                    .collect(Collectors.toList()))
            .build());
  }

  private OrderConfirmedEvent orderConfirmedEvent(String orderId) {
    return new OrderConfirmedEvent(orderId);
  }

  private DeliveryTaskCreatedEvent deliveryTaskCreatedEvent(DeliveryTask deliveryTask) {
    return new DeliveryTaskCreatedEvent(deliveryTask);
  }

  @Transactional
  public FindOrdersResponse findOrders(FindOrdersRequest request) {
    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<OrderEntity> countQueryRoot = countQuery.from(OrderEntity.class);
    countQuery.select(cb.count(countQueryRoot));
    this.updateQuery(countQuery, cb, countQueryRoot, request);
    long count = this.entityManager.createQuery(countQuery).getSingleResult();

    CriteriaQuery<OrderEntity> ordersQuery = cb.createQuery(OrderEntity.class);
    Root<OrderEntity> ordersRoot = ordersQuery.from(OrderEntity.class);
    this.updateQuery(ordersQuery, cb, ordersRoot, request);
    PageRequest pageRequest = request.getPageRequest();
    List<Order> orders =
        this.entityManager
            .createQuery(ordersQuery)
            .setFirstResult(pageRequest.getPage() * pageRequest.getSize())
            .setMaxResults(pageRequest.getSize())
            .getResultList()
            .stream()
            .map(
                order ->
                    Order.newBuilder()
                        .setOrderId(order.getId())
                        .setUserId(order.getUserId())
                        .setRestaurantId(order.getRestaurantId())
                        .setStatus(order.getStatus().name())
                        .addAllItems(this.decodeOrderItems(order))
                        .build())
            .collect(Collectors.toList());
    int pageCount = (int) Math.ceil(count * 1.0 / pageRequest.getSize());
    return FindOrdersResponse.newBuilder()
        .setResult(
            OrdersResult.newBuilder()
                .setCurrentPage(pageRequest.getPage())
                .setTotalPages(pageCount)
                .setTotalItems(count)
                .addAllOrders(orders)
                .build())
        .build();
  }

  private <T> void updateQuery(
      CriteriaQuery<T> query,
      CriteriaBuilder cb,
      Root<OrderEntity> root,
      FindOrdersRequest request) {
    List<Predicate> predicates = new ArrayList<>();
    if (!request.getUserId().isBlank()) {
      predicates.add(cb.equal(root.get("userId"), request.getUserId()));
    }
    if (!request.getRestaurantId().isBlank()) {
      predicates.add((cb.equal(root.get("restaurantId"), request.getRestaurantId())));
    }
    if (!request.getStatus().isBlank()) {
      predicates.add(cb.equal(root.get("status"), OrderStatus.valueOf(request.getStatus())));
    }
    query.where(predicates.toArray(new Predicate[0]));
  }

  private List<LineItemEntity> buildLineItems(CreateOrderRequest request) {
    return request.getItemsList().stream().map(this::buildOrderItem).collect(Collectors.toList());
  }

  private LineItemEntity buildOrderItem(OrderItem orderItem) {
    return LineItemEntity.builder()
        .itemId(orderItem.getItemId())
        .quantity(orderItem.getQuantity())
        .price(BigDecimal.valueOf(orderItem.getPrice()))
        .build();
  }

  private List<OrderItem> decodeOrderItems(OrderEntity order) {
    return order.getLineItems().stream().map(this::decodeOrderItem).collect(Collectors.toList());
  }

  private OrderItem decodeOrderItem(LineItemEntity lineItem) {
    return OrderItem.newBuilder()
        .setItemId(lineItem.getItemId())
        .setQuantity(lineItem.getQuantity())
        .setPrice(lineItem.getPrice().doubleValue())
        .build();
  }
}
