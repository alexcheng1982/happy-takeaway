package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Response;
import io.vividcode.happytakeaway.delivery.api.v1.Address;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryPickupInvitation;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.delivery.api.v1.DisableRiderRequest;
import io.vividcode.happytakeaway.delivery.api.v1.DisableRiderResponse;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionRequest;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionResponse;
import io.vividcode.happytakeaway.delivery.entity.DeliveryPickupInvitationStatus;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskInfo;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskStatus;
import io.vividcode.happytakeaway.delivery.repository.DeliveryTaskRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.Value;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeliveryService {

  @Inject ReactiveRedisClient redisClient;

  @Inject DeliveryTaskRepository deliveryTaskRepository;

  @Inject
  @Channel("delivery-rider-search")
  Emitter<DeliveryRiderSearch> retrySearchEmitter;

  @ConfigProperty(name = "app.delivery.check-interval", defaultValue = "10")
  int checkInterval;

  private static final Logger LOGGER = Logger.getLogger(DeliveryService.class);

  private static final String REDIS_RIDER_SEARCH = "delivery_rider_search";

  private static final String REDIS_CURRENT_POSITION = "delivery_";

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public Uni<DeliveryTaskInfo> getTask(String taskId) {
    return this.deliveryTaskRepository.getTask(taskId);
  }

  public Uni<DeliveryRiderSearch> taskCreated(DeliveryTask deliveryTask) {
    return this.deliveryTaskRepository
        .createTask(deliveryTask.getId())
        .map($void -> DeliveryRiderSearch.builder().deliveryTask(deliveryTask).tryCount(1).build());
  }

  public Multi<DeliveryPickupInvitation> searchForRiders(DeliveryRiderSearch search) {
    Uni<List<String>> existingRiders =
        this.deliveryTaskRepository.getExistingRiders(search.getDeliveryTask().getId());
    Uni<List<String>> riders = this.search(search, existingRiders);
    return riders
        .toMulti()
        .flatMap(
            riderIds ->
                Multi.createFrom()
                    .items(riderIds.stream())
                    .flatMap(
                        riderId ->
                            this.deliveryTaskRepository
                                .createPickupInvitation(search.getDeliveryTask().getId(), riderId)
                                .map(
                                    $void ->
                                        DeliveryPickupInvitation.builder()
                                            .deliveryTask(search.getDeliveryTask())
                                            .riderId(riderId)
                                            .build())
                                .toMulti()));
  }

  public Uni<Void> acceptDeliveryPickupInvitation(String taskId, String riderId) {
    return this.deliveryTaskRepository.updatePickupStatus(
        taskId, riderId, DeliveryPickupInvitationStatus.ACCEPTED);
  }

  public void updateDeliveryTaskStatus(DeliveryTask deliveryTask, DeliveryTaskStatus status) {
    this.deliveryTaskRepository
        .updateTaskStatus(deliveryTask.getId(), status)
        .await()
        .indefinitely();
  }

  public void selectRider(String taskId, String riderId) {
    this.deliveryTaskRepository
        .selectRider(taskId, riderId)
        .subscribe()
        .with(
            ($void) -> LOGGER.infov("Marked task {0} as selected by {1}", taskId, riderId),
            (e) -> LOGGER.warn("Failed to select rider", e));
  }

  public void checkForRidersAcceptance(DeliveryRiderSearch search) {
    this.scheduleCheckDeliveryPickupInvitationTask(
        search.getDeliveryTask(),
        Duration.ofSeconds(this.checkInterval),
        search.getTryCount(),
        (task, riderId) -> this.selectRider(task.getId(), riderId),
        this::updateDeliveryTaskStatus);
  }

  public void scheduleCheckDeliveryPickupInvitationTask(
      DeliveryTask deliveryTask,
      Duration interval,
      int attempt,
      BiConsumer<DeliveryTask, String> successCallback,
      BiConsumer<DeliveryTask, DeliveryTaskStatus> failureCallback) {
    this.scheduler.schedule(
        new CheckDeliveryPickupAcceptanceTask(
            this, deliveryTask, attempt, successCallback, failureCallback),
        interval.toMillis(),
        TimeUnit.MILLISECONDS);
  }

  public Uni<Optional<String>> selectRider(DeliveryTask deliveryTask) {
    return this.deliveryTaskRepository
        .getAcceptedRiders(deliveryTask.getId())
        .flatMap(riders -> this.searchForSelect(deliveryTask, riders));
  }

  private Uni<Optional<String>> searchForSelect(DeliveryTask deliveryTask, List<String> riders) {
    String key = "search_" + deliveryTask.getId();
    return Multi.createFrom()
        .items(riders.stream())
        .flatMap(
            riderId ->
                this.getCurrentPosition(riderId)
                    .toMulti()
                    .flatMap(
                        geoLocation -> {
                          List<String> args = new ArrayList<>(4);
                          args.add(key);
                          args.add(geoLocation.getLng());
                          args.add(geoLocation.getLat());
                          args.add(riderId);
                          return this.redisClient.geoadd(args).toMulti();
                        }))
        .flatMap(
            r -> {
              Address address = deliveryTask.getRestaurantAddress();
              return this.redisClient
                  .georadius(
                      List.of(
                          key,
                          Double.toString(address.getLng()),
                          Double.toString(address.getLat()),
                          "30",
                          "km",
                          "COUNT",
                          "1",
                          "ASC"))
                  .map(
                      response ->
                          StreamSupport.stream(response.spliterator(), false)
                              .map(Response::toString)
                              .findFirst())
                  .toMulti();
            })
        .toUni();
  }

  public Uni<List<String>> search(DeliveryRiderSearch search, Uni<List<String>> existingRiders) {
    Address restaurantAddress = search.getDeliveryTask().getRestaurantAddress();
    return this.findRiders(
            RiderSearchRequest.builder()
                .lng(restaurantAddress.getLng())
                .lat(restaurantAddress.getLat())
                .radius(search.getTryCount() * 10)
                .count(10)
                .build())
        .flatMap(
            results ->
                existingRiders.map(
                    riders -> {
                      results.removeAll(riders);
                      return results;
                    }));
  }

  public Multi<UpdateRiderPositionResponse> updateRiderPosition(
      Multi<UpdateRiderPositionRequest> request) {
    return request.flatMap(
        req ->
            this.updateCurrentPosition(req)
                .flatMap(r -> this.addRiderPositionForSearch(req))
                .map(
                    response ->
                        UpdateRiderPositionResponse.newBuilder()
                            .setRiderId(req.getRiderId())
                            .setResult(true)
                            .build())
                .toMulti());
  }

  private Uni<Response> addRiderPositionForSearch(UpdateRiderPositionRequest request) {
    List<String> args = new ArrayList<>(4);
    args.add(REDIS_RIDER_SEARCH);
    args.add(Double.toString(request.getLng()));
    args.add(Double.toString(request.getLat()));
    args.add(request.getRiderId());
    return this.redisClient.geoadd(args);
  }

  private Uni<Response> updateCurrentPosition(UpdateRiderPositionRequest request) {
    List<String> args = new ArrayList<>(5);
    args.add(REDIS_CURRENT_POSITION + request.getRiderId());
    args.add("lng");
    args.add(Double.toString(request.getLng()));
    args.add("lat");
    args.add(Double.toString(request.getLat()));
    return this.redisClient.hset(args);
  }

  private Uni<GeoLocation> getCurrentPosition(String riderId) {
    String key = REDIS_CURRENT_POSITION + riderId;
    return this.redisClient
        .hgetall(key)
        .flatMap(
            response ->
                Uni.createFrom()
                    .optional(
                        Optional.ofNullable(
                            response.size() == 4
                                ? new GeoLocation(
                                    response.get("lng").toString(), response.get("lat").toString())
                                : null)));
  }

  public Uni<DisableRiderResponse> disableRider(DisableRiderRequest request) {
    return this.redisClient
        .zrem(List.of(REDIS_RIDER_SEARCH, request.getRiderId()))
        .map(
            response ->
                DisableRiderResponse.newBuilder()
                    .setRiderId(request.getRiderId())
                    .setResult(
                        Optional.ofNullable(response)
                            .map(Response::toInteger)
                            .map(v -> v > 0)
                            .orElse(false))
                    .build());
  }

  public Uni<List<String>> findRiders(RiderSearchRequest request) {
    return this.redisClient
        .georadius(
            List.of(
                REDIS_RIDER_SEARCH,
                Double.toString(request.getLng()),
                Double.toString(request.getLat()),
                Double.toString(request.getRadius()),
                "km",
                "COUNT",
                Integer.toString(request.getCount()),
                "ASC"))
        .map(
            response ->
                StreamSupport.stream(response.spliterator(), false)
                    .map(Response::toString)
                    .collect(Collectors.toList()));
  }

  @Value
  private static class GeoLocation {

    String lng;
    String lat;
  }

  private static class CheckDeliveryPickupAcceptanceTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CheckDeliveryPickupAcceptanceTask.class);

    private final DeliveryService deliveryService;
    private final DeliveryTask deliveryTask;
    private final int attempt;
    private final BiConsumer<DeliveryTask, String> successCallback;
    private final BiConsumer<DeliveryTask, DeliveryTaskStatus> failureCallback;

    private CheckDeliveryPickupAcceptanceTask(
        DeliveryService deliveryService,
        DeliveryTask deliveryTask,
        int attempt,
        BiConsumer<DeliveryTask, String> successCallback,
        BiConsumer<DeliveryTask, DeliveryTaskStatus> failureCallback) {
      this.deliveryService = deliveryService;
      this.deliveryTask = deliveryTask;
      this.attempt = attempt;
      this.successCallback = successCallback;
      this.failureCallback = failureCallback;
    }

    @Override
    public void run() {
      Optional<String> result =
          this.deliveryService.selectRider(this.deliveryTask).await().indefinitely();
      if (result != null && result.isPresent()) {
        LOGGER.infov("Select rider {0} for delivery task {1}", result.get(), this.deliveryTask);
        this.successCallback.accept(this.deliveryTask, result.get());
      } else {
        if (this.attempt < 3) {
          LOGGER.infov("Retry search for delivery task {0}", this.deliveryTask);
          this.deliveryService.retrySearchEmitter.send(
              DeliveryRiderSearch.builder()
                  .deliveryTask(this.deliveryTask)
                  .tryCount(this.attempt + 1)
                  .build());
        } else {
          LOGGER.infov("No riders for delivery task {0}", this.deliveryTask);
          this.failureCallback.accept(this.deliveryTask, DeliveryTaskStatus.FAILED_NO_RIDERS);
        }
      }
    }
  }
}
