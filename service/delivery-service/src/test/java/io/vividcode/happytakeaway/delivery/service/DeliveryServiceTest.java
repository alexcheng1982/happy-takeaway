package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vividcode.happytakeaway.common.test.RedisResource;
import io.vividcode.happytakeaway.delivery.api.v1.DisableRiderRequest;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(RedisResource.class)
@DisplayName("Delivery service")
class DeliveryServiceTest {

  @Inject DeliveryService deliveryService;

  @Test
  @DisplayName("find riders")
  void findRiders() {
    RiderSearchRequest searchRequest =
        RiderSearchRequest.builder().lng(50.0).lat(0).radius(10).count(1).build();
    UniAssertSubscriber<List<String>> subscriber =
        this.deliveryService
            .updateRiderPosition(
                Multi.createFrom()
                    .items(
                        TestHelper.updateRiderPositionRequest("rider1", 50.00001, 0.00001),
                        TestHelper.updateRiderPositionRequest("rider2", 50.00002, 0.00002),
                        TestHelper.updateRiderPositionRequest("rider3", 50.00003, 0.00003)))
            .collect()
            .asList()
            .flatMap((list) -> this.deliveryService.findRiders(searchRequest))
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create());
    subscriber.awaitItem().assertItem(List.of("rider1"));

    subscriber =
        this.deliveryService
            .disableRider(DisableRiderRequest.newBuilder().setRiderId("rider1").build())
            .flatMap((r) -> this.deliveryService.findRiders(searchRequest))
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create());
    subscriber.awaitItem().assertItem(List.of("rider2"));
  }
}
