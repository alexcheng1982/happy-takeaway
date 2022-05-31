package io.vividcode.happytakeaway.restaurant.search;

import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchRequest;
import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchResponse;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GeoLocationSearchResource {

  @Inject GeoLocationSearchService searchService;

  @Path("search")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeoLocationSearchResponse search(GeoLocationSearchRequest request) {
    return this.searchService.search(request);
  }
}
