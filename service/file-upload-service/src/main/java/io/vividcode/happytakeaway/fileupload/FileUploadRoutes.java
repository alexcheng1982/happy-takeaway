package io.vividcode.happytakeaway.fileupload;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HandlerType;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vividcode.happytakeaway.fileupload.UploadResult.UploadedFile;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileUploadRoutes {

  @Route(path = "/upload", methods = HttpMethod.POST, produces = "application/json", order = 10)
  UploadResult upload(RoutingContext context) {
    return new UploadResult(
        context.fileUploads().stream()
            .map(fileUpload -> new UploadedFile(this.encodeFilePath(fileUpload)))
            .toArray(UploadedFile[]::new));
  }

  @Route(path = "/file/:id", methods = HttpMethod.GET, order = 1)
  void file(RoutingContext context) {
    String fileId = context.request().getParam("id");
    if (fileId == null) {
      throw new InvalidFileIdException();
    }
    EncodedFile encodedFile = EncodedFile.decode(fileId);
    context
        .response()
        .putHeader(HttpHeaders.CONTENT_TYPE, encodedFile.getContentType())
        .sendFile(encodedFile.getFilePath());
  }

  @Route(path = "/file/:id", type = HandlerType.FAILURE)
  void invalidFileId(InvalidFileIdException e, HttpServerResponse response) {
    response.setStatusCode(400).end(e.getMessage());
  }

  private String encodeFilePath(FileUpload fileUpload) {
    return EncodedFile.builder()
        .filePath(fileUpload.uploadedFileName())
        .contentType(fileUpload.contentType())
        .build()
        .encode();
  }
}
