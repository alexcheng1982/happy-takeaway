package io.vividcode.happytakeaway.restaurant.dataloader;

import java.io.InputStream;
import javax.ws.rs.FormParam;
import lombok.Builder;
import lombok.Data;
import org.jboss.resteasy.annotations.providers.multipart.PartFilename;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

@Data
@Builder
public class FileUploadBody {

  @FormParam("file")
  @PartType("image/jpeg")
  @PartFilename("cover.jpg")
  private InputStream file;
}
