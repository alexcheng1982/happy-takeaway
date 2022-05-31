package io.vividcode.happytakeaway.common.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

  private int page;
  private int size;

  public int getOffset() {
    return this.page * this.size;
  }

  public static PageRequest of(int page, int size) {
    return PageRequest.builder().page(page).size(size).build();
  }
}
