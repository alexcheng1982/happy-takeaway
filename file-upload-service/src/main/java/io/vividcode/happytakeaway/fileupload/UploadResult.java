package io.vividcode.happytakeaway.fileupload;

import lombok.Value;

@Value
public class UploadResult {

  UploadedFile[] files;

  @Value
  public static class UploadedFile {

    String path;
  }
}
