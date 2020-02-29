package model;

public class Subject {
  private int id;
  private String name;
  private String nameOfThumbnailFile;
  private String thumbnailPath;

  /**
   * Description:
   *
   * @param id SQL row number in subjects table
   * @param name String mame of the subject
   * @param nameOfThumbnailFile String name of thumbnail image file
   * @param thumbnailPath String with path to thumbnail image referenced to repository root
   */
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
