package io.vividcode.happytakeaway.restaurant.dataloader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/upload")
@RegisterRestClient(configKey = "fileupload-service-api")
public interface FileUploadServiceClient {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  UploadResult upload(@MultipartForm FileUploadBody body);
}
