package io.vividcode.happytakeaway.restaurant.resource;

import static io.restassured.RestAssured.given;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.restaurant.PostgreSQLResource;
import io.vividcode.happytakeaway.restaurant.TestHelper;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgreSQLResource.class)
@TestHTTPEndpoint(RestaurantResource.class)
@DisplayName("restaurant resource")
class RestaurantResourceTest {

  @Test
  @DisplayName("create a restaurant")
  void create() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(TestHelper.createRestaurantRequest())
        .post()
        .then()
        .statusCode(201);
  }
}
