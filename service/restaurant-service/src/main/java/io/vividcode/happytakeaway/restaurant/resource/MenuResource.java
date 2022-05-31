package io.vividcode.happytakeaway.restaurant.resource;

import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsRequest;
import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsResponse;
import io.vividcode.happytakeaway.restaurant.api.CreateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateMenuWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateMenuWebResponse;
import io.vividcode.happytakeaway.restaurant.service.MenuService;
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

@Path("/{restaurantId}/menu")
public class MenuResource {

  @Inject MenuService menuService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menu")
  public Response create(
      @PathParam("restaurantId") String restaurantId,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CreateMenuWebRequest.class)))
          CreateMenuWebRequest request,
      @Context UriInfo uriInfo) {
    String id =
        this.menuService.createMenu(
            CreateMenuRequest.builder()
                .restaurantId(restaurantId)
                .name(request.getName())
                .current(request.isCurrent())
                .build());
    return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
  }

  @Path("{menuId}")
  @PATCH
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menu")
  public UpdateMenuWebResponse update(
      @PathParam("restaurantId") String restaurantId,
      @PathParam("menuId") String menuId,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UpdateMenuWebRequest.class)))
          UpdateMenuWebRequest request) {
    UpdateMenuResponse response =
        this.menuService.updateMenu(
            UpdateMenuRequest.builder()
                .restaurantId(restaurantId)
                .id(menuId)
                .name(request.getName())
                .build());
    return UpdateMenuWebResponse.builder().id(response.getId()).name(response.getName()).build();
  }

  @Path("{menuId}")
  @DELETE
  @Tag(ref = "menu")
  public Response delete(
      @PathParam("restaurantId") String restaurantId, @PathParam("menuId") String menuId) {
    this.menuService.deleteMenu(
        DeleteMenuRequest.builder().restaurantId(restaurantId).id(menuId).build());
    return Response.noContent().build();
  }

  @Path("{menuId}/association")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "menu")
  public AssociateMenuItemsWebResponse associateMenuItems(
      @PathParam("restaurantId") String restaurantId,
      @PathParam("menuId") String menuId,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = AssociateMenuItemsWebRequest.class)))
          AssociateMenuItemsWebRequest request) {
    AssociateMenuItemsResponse response =
        this.menuService.associateMenuItems(
            AssociateMenuItemsRequest.builder()
                .restaurantId(restaurantId)
                .menuId(menuId)
                .menuItems(request.getMenuItems())
                .build());
    return AssociateMenuItemsWebResponse.builder()
        .menuId(response.getMenuId())
        .menuItems(response.getMenuItems())
        .build();
  }
}
