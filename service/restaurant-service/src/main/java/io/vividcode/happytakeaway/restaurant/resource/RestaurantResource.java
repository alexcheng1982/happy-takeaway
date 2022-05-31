package io.vividcode.happytakeaway.restaurant.resource;

import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.CreateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.FullTextSearchRequest;
import io.vividcode.happytakeaway.restaurant.api.GetRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.SetActiveMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.SetActiveMenuResponse;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import io.vividcode.happytakeaway.restaurant.api.v1.RestaurantWithMenuItems;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateRestaurantWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.GetRestaurantWebResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.SetActiveMenuWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.SetActiveMenuWebResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateRestaurantWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.UpdateRestaurantWebResponse;
import io.vividcode.happytakeaway.restaurant.service.FullTextSearchService;
import io.vividcode.happytakeaway.restaurant.service.RestaurantService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
public class RestaurantResource {

  @Inject RestaurantService restaurantService;

  @Inject FullTextSearchService fullTextSearchService;

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get restaurant with menu items")
  @Tag(ref = "restaurant")
  public GetRestaurantWebResponse get(
      @PathParam("id")
          @Parameter(description = "restaurant id", in = ParameterIn.PATH, required = true)
          String id,
      @QueryParam("numberOfMenuItems")
          @DefaultValue("0")
          @Parameter(description = "number of menu items", in = ParameterIn.QUERY)
          Integer numberOfMenuItems) {
    RestaurantWithMenuItems restaurant =
        this.restaurantService.getRestaurant(
            GetRestaurantRequest.builder().id(id).numberOfMenuItems(numberOfMenuItems).build());
    return GetRestaurantWebResponse.builder()
        .id(restaurant.getId())
        .name(restaurant.getName())
        .description(restaurant.getDescription())
        .address(restaurant.getAddress())
        .menuItems(restaurant.getMenuItems())
        .build();
  }

  @POST
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  @Operation(summary = "Create a restaurant")
  @Tag(ref = "restaurant")
  public Response create(
      @RequestBody(
              description = "create request",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CreateRestaurantWebRequest.class)))
          CreateRestaurantWebRequest request,
      @Context UriInfo uriInfo) {
    String id =
        this.restaurantService.createRestaurant(
            CreateRestaurantRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build());
    return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
  }

  @PATCH
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Updated restaurant",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateRestaurantWebResponse.class))),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  @Operation(summary = "Update a restaurant")
  @Tag(ref = "restaurant")
  public UpdateRestaurantWebResponse update(
      @PathParam("id")
          @Parameter(description = "restaurant id", in = ParameterIn.PATH, required = true)
          String id,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UpdateRestaurantWebRequest.class)))
          UpdateRestaurantWebRequest request) {
    UpdateRestaurantResponse response =
        this.restaurantService.updateRestaurant(
            UpdateRestaurantRequest.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .build());
    return UpdateRestaurantWebResponse.builder()
        .id(response.getId())
        .name(response.getName())
        .description(response.getDescription())
        .address(response.getAddress())
        .build();
  }

  @DELETE
  @Path("{id}")
  @Tag(ref = "restaurant")
  public Response delete(@PathParam("id") String id) {
    this.restaurantService.deleteRestaurant(DeleteRestaurantRequest.builder().id(id).build());
    return Response.noContent().build();
  }

  @POST
  @Path("{id}/activeMenu")
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "restaurant")
  public SetActiveMenuWebResponse setActiveMenu(
      @PathParam("id") String id,
      @RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = SetActiveMenuWebRequest.class)))
          SetActiveMenuWebRequest request) {
    SetActiveMenuResponse response =
        this.restaurantService.setActiveMenu(
            SetActiveMenuRequest.builder().restaurantId(id).menuId(request.getMenuId()).build());
    return SetActiveMenuWebResponse.builder().menuId(response.getMenuId()).build();
  }

  @GET
  @Path("{id}/menuItems")
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "restaurant")
  public PagedResult<MenuItem> getMenuItems(
      @PathParam("id") String id,
      @QueryParam("page") @DefaultValue("0") Integer page,
      @QueryParam("size") @DefaultValue("10") Integer size) {
    return this.restaurantService.getMenuItems(id, PageRequest.of(page, size));
  }

  @POST
  @Path("search")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Tag(ref = "restaurant")
  public FullTextSearchResponse search(FullTextSearchWebRequest request) {
    return this.fullTextSearchService.search(
        FullTextSearchRequest.builder()
            .query(request.getQuery())
            .minPrice(request.getMinPrice())
            .maxPrice(request.getMaxPrice())
            .pageRequest(PageRequest.of(request.getPage(), request.getSize()))
            .build());
  }
}
