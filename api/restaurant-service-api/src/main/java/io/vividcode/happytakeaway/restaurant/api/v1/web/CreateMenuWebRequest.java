package io.vividcode.happytakeaway.restaurant.api.v1.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuWebRequest {

  @NonNull private String name;
  private boolean current;
}
