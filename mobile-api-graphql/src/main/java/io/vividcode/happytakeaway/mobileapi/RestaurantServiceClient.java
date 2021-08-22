package io.vividcode.happytakeaway.mobileapi;

import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.GetRestaurantWebResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "restaurant-service-api")
public interface RestaurantServiceClient {

  @POST
  @Path("search")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  FullTextSearchResponse search(FullTextSearchWebRequest request);

  @GET
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  GetRestaurantWebResponse get(@PathParam("id") String id);
}
