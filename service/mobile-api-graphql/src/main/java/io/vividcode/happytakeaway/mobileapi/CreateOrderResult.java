package io.vividcode.happytakeaway.mobileapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Name("CreateOrderResult")
public class CreateOrderResult {

  private String orderId;
}
