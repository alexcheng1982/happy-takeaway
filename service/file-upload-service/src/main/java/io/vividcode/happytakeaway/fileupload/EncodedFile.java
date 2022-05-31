package io.vividcode.happytakeaway.fileupload;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EncodedFile {

  private String filePath;
  private String contentType;

  public String encode() {
    return Base64.getEncoder()
        .encodeToString((this.filePath + "#" + this.contentType).getBytes(StandardCharsets.UTF_8));
  }

  public static EncodedFile decode(String path) {
    String decoded = new String(Base64.getDecoder().decode(path), StandardCharsets.UTF_8);
    String[] parts = decoded.split("#");
    if (parts.length != 2) {
      throw new InvalidFileIdException();
    }
    return new EncodedFile(parts[0], parts[1]);
  }
}
