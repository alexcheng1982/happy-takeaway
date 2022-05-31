package io.vividcode.happytakeaway.restaurant.resource;

import io.vividcode.happytakeaway.restaurant.api.CreateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuItemWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateMenuItemWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateMenuItemWebResponse;
import io.vividcode.happytakeaway.restaurant.service.MenuItemService;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/{restaurantId}/menuitem")
public class MenuItemResource {

  @Inject MenuItemService menuItemService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menuItem")
  public Response create(
      @PathParam("restaurantId") String restaurantId,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CreateMenuItemWebRequest.class)))
          CreateMenuItemWebRequest request,
      @Context UriInfo uriInfo) {
    String id =
        this.menuItemService.createMenuItem(
            CreateMenuItemRequest.builder()
                .restaurantId(restaurantId)
                .name(request.getName())
                .coverImage(request.getCoverImage())
                .description(request.getDescription())
                .price(request.getPrice())
                .build());
    return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
  }

  @Path("{menuItemId}")
  @PATCH
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menuItem")
  public UpdateMenuItemWebResponse update(
      @PathParam("restaurantId") String restaurantId,
      @PathParam("menuItemId") String menuItemId,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UpdateMenuItemWebRequest.class)))
          UpdateMenuItemWebRequest request) {
    UpdateMenuItemResponse response =
        this.menuItemService.updateMenuItem(
            UpdateMenuItemRequest.builder()
                .restaurantId(restaurantId)
                .id(menuItemId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build());
    return UpdateMenuItemWebResponse.builder()
        .id(response.getId())
        .name(response.getName())
        .description(response.getDescription())
        .price(response.getPrice())
        .build();
  }

  @Path("{menuItemId}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menuItem")
  public Response delete(
      @PathParam("restaurantId") String restaurantId, @PathParam("menuItemId") String menuItemId) {
    this.menuItemService.deleteMenuItem(
        DeleteMenuItemRequest.builder().restaurantId(restaurantId).id(menuItemId).build());
    return Response.noContent().build();
  }
}
