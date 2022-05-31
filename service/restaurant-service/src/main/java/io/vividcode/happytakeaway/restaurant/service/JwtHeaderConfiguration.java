package io.vividcode.happytakeaway.restaurant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

public class JwtHeaderConfiguration {

  @Produces
  @RequestScoped
  @IfBuildProfile("prod")
  public OwnerIdProvider jwtHeader(RoutingContext context, ObjectMapper objectMapper) {
    return () -> {
      String payload = context.request().getHeader("X-JWT-PAYLOAD");
      if (payload != null) {
        try {
          HashMap<String, Object> data =
              objectMapper.readValue(Base64.getDecoder().decode(payload), new TypeReference<>() {});
          return (String) data.get("sub");
        } catch (IOException e) {
          context.response().setStatusCode(403).end("Invalid token");
        }
      }
      return "anonymous";
    };
  }

  @Produces
  @UnlessBuildProfile("prod")
  public OwnerIdProvider dev() {
    return () -> "test-user";
  }
}
