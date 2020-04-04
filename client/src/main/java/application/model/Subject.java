package application.model;

import application.model.managers.SubjectManager;

/**
 * The Subject Class is a container for all information
 * associated with a Subject. This is used by both the
 * Client and Server side as a compact way of sending
 * and receiving all information on a specific subject.
 * <p>
 *   The Subject Class is usually contained within a
 *   SubjectManager.
 * </p>
 * <p>
 *   The information on the Subject is obtained from
 *   the Server, specifically the MySQL database that
 *   contains all information on the subjects.
 * </p>
 *
 * @author James Gardner
 * @see    SubjectManager
 */
public class Subject {
  private int id;
  private String name;
  private String nameOfThumbnailFile;
  private String thumbnailPath;
  private String coverPhotoPath;
  private String coverPhotoFilename;

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
