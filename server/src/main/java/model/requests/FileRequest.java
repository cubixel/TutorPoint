package model.requests;

public class FileRequest {
  String filePath;

  public FileRequest(String path) {
    filePath = path;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}
