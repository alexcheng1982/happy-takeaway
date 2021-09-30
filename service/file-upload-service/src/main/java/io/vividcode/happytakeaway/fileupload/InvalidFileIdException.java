package io.vividcode.happytakeaway.fileupload;

public class InvalidFileIdException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public InvalidFileIdException() {
    super("Invalid file id");
  }
}
