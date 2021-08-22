package io.vividcode.happytakeaway.restaurant.dataloader;

import lombok.Data;

@Data
public class UploadResult {

  UploadedFile[] files;

  @Data
  public static class UploadedFile {

    String path;
  }
}
