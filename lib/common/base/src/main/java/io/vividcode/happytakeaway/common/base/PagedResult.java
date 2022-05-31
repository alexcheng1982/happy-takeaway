package io.vividcode.happytakeaway.common.base;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedResult<T> {

  private List<T> data;
  private long currentPage;
  private long totalItems;
  private long totalPages;

  public static <T> PagedResult<T> empty() {
    return PagedResult.<T>builder()
        .data(Collections.emptyList())
        .currentPage(0)
        .totalItems(0)
        .totalPages(0)
        .build();
  }

  public static <T> PagedResult<T> fromData(
      List<T> data, PageRequest pageRequest, long totalItems) {
    return PagedResult.<T>builder()
        .data(data)
        .currentPage(pageRequest.getPage())
        .totalItems(totalItems)
        .totalPages((int) Math.ceil((double) totalItems / pageRequest.getSize()))
        .build();
  }
}
