package io.vividcode.happytakeaway.restaurant.resource;

import io.vividcode.happytakeaway.order.api.v1.PageRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.ConfirmOrderResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FindOrdersResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.MarkAsReadyForDeliveryResponse;
import io.vividcode.happytakeaway.restaurant.service.OrderService;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/{restaurantId}/order")
public class OrderResource {

  @Inject OrderService orderService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "order")
  public FindOrdersResponse listOrders(
      @PathParam("restaurantId") String restaurantId,
      @QueryParam("status") String status,
      @QueryParam("page") @DefaultValue("0") Integer page,
      @QueryParam("size") @DefaultValue("10") Integer size) {
    return this.orderService.listOrders(
        restaurantId, status, PageRequest.newBuilder().setPage(page).setSize(size).build());
  }

  @Path("{orderId}/confirm")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "order")
  public ConfirmOrderResponse confirmOrder(
      @PathParam("restaurantId") String restaurantId, @PathParam("orderId") String orderId) {
    boolean result = this.orderService.confirmOrder(restaurantId, orderId);
    return ConfirmOrderResponse.builder().orderId(orderId).result(result).build();
  }

  @Path("{orderId}/markAsReadyForDelivery")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "order")
  public MarkAsReadyForDeliveryResponse markAsReadyForDelivery(
      @PathParam("restaurantId") String restaurantId, @PathParam("orderId") String orderId) {
    boolean result = this.orderService.markAsReadyForDelivery(restaurantId, orderId);
    return MarkAsReadyForDeliveryResponse.builder().orderId(orderId).result(result).build();
  }
}
