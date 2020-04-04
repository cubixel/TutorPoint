package model;

public class Subject {
  private int id;
  private String name;
  private String nameOfThumbnailFile;
  private String thumbnailPath;
  private String coverPhotoFilename;
  private String coverPhotoPath;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param id SQL row number in subjects table
   * @param name String mame of the subject
   * @param nameOfThumbnailFile String name of thumbnail image file
   * @param thumbnailPath String with path to thumbnail image referenced to repository root
   */
  public Subject(int id, String name, String nameOfThumbnailFile, String thumbnailPath,
      String coverPhotoFilename, String coverPhotoPath) {
    this.id = id;
    this.name = name;
    this.nameOfThumbnailFile = nameOfThumbnailFile;
    this.thumbnailPath = thumbnailPath;
    this.coverPhotoFilename = coverPhotoFilename;
    this.coverPhotoPath = coverPhotoPath;
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param name String mame of the subject
   * @param nameOfThumbnailFile String name of thumbnail image file
   * @param thumbnailPath String with path to thumbnail image referenced to repository root
   */
  public Subject(String name, String nameOfThumbnailFile, String thumbnailPath) {
    this.name = name;
    this.nameOfThumbnailFile = nameOfThumbnailFile;
    this.thumbnailPath = thumbnailPath;
  }

  public String getCoverPhotoPath() {
    return coverPhotoPath;
  }

  public void setCoverPhotoPath(String coverPhotoPath) {
    this.coverPhotoPath = coverPhotoPath;
  }

  public String getCoverPhotoFilename() {
    return coverPhotoFilename;
  }

  public void setCoverPhotoFilename(String coverPhotoFilename) {
    this.coverPhotoFilename = coverPhotoFilename;
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

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNameOfThumbnailFile(String nameOfThumbnailFile) {
    this.nameOfThumbnailFile = nameOfThumbnailFile;
  }

  public void setThumbnailPath(String thumbnailPath) {
    this.thumbnailPath = thumbnailPath;
  }
}
