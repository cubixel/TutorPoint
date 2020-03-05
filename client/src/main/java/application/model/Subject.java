package application.model;

public class Subject {
    private int id;
    private String name;
    private String nameOfThumbnailFile;
    private String thumbnailPath;

    public Subject(int id, String name, String nameOfThumbnailFile, String thumbnailPath) {
        this.id = id;
        this.name = name;
        this.nameOfThumbnailFile = nameOfThumbnailFile;
        this.thumbnailPath = thumbnailPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

  public String getNameOfThumbnailFile() {
    return nameOfThumbnailFile;
  }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
