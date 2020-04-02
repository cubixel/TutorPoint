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

  /**
   * Initialises a newly created {@code Subject} object so that it
   * represents the same information as the supplied arguments.
   * 
   * @param id
   *        A ID Number associated with this subject on the MySQL Database.
   *
   * @param name
   *        The name of the Subject.
   *
   * @param nameOfThumbnailFile
   *        For ease of access the name of the thumbnail file is provided.
   *
   * @param thumbnailPath
   *        The path to the thumbnail on the Server.
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
