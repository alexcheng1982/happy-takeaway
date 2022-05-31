package io.vividcode.happytakeaway.restaurant.api.v1;

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
public class Address {

  @NonNull
  @Schema(required = true)
  private String code;

  @NonNull
  @Schema(required = true)
  private String addressLine;

  @NonNull
  @Schema(required = true)
  private Double lng;

  @NonNull
  @Schema(required = true)
  private Double lat;
}
