package io.vividcode.happytakeaway.restaurant.dataloader;

import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuItemWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateRestaurantWebRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "restaurant-service-api")
public interface RestaurantServiceClient {

  @Path("/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  Response createRestaurant(CreateRestaurantWebRequest request);

  @Path("/{restaurantId}/menu")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  Response createMenu(@PathParam("restaurantId") String restaurantId, CreateMenuWebRequest request);

  @Path("/{restaurantId}/menuitem")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  Response createMenuItem(
      @PathParam("restaurantId") String restaurantId, CreateMenuItemWebRequest request);

  @Path("/{restaurantId}/menu/{menuId}/association")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  AssociateMenuItemsWebResponse associateMenuItems(
      @PathParam("restaurantId") String restaurantId,
      @PathParam("menuId") String menuId,
      AssociateMenuItemsWebRequest request);
}
