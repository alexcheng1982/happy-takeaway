package io.vividcode.happytakeaway.restaurant.api.v1.web;

import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateRestaurantRequest", description = "Request to create a restaurant")
public class CreateRestaurantWebRequest {

  @NonNull
  @Schema(required = true)
  private String name;

  private String description;

  @NonNull
  @Schema(required = true)
  private String phoneNumber;

  @NonNull
  @Schema(required = true)
  private Address address;
}
