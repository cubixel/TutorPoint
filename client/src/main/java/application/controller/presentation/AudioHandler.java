package application.controller.presentation;

import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
// CUBIUXEL ADDED LOGGING
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code AudioHandler} class provides the ability to store, play and stop audio files.
 * @author Goose Software Design on behalf of CUBIXEL.
 * 
 *      CUBIXEL have reworked the error messages to print to log instead of console,
 *      and performed small bugfixes.
 */
public class AudioHandler {
  private AudioList audios;

  //CUBIXEL ADDED LOGGER
  private static final Logger log = LoggerFactory.getLogger("AudioHandler");
  
  public AudioHandler() {
    audios = new AudioList();
  }

  /**
   * The {@code AudioFile} class is a construct made by the {@code AudioHandler} class
   * to store registered audio files, as well as generate {@code MediaPlayer} for each file stored.
   */
  private class AudioFile {
    private String id; //the unique identifier assigned to the audio file
    private MediaPlayer player; //the player object for the audio file
    
    private AudioFile(String url, Boolean looping, String id) {
      this.id = id;
      //Generate a player for the Media object generated from the url given by the parameter
      this.player = new MediaPlayer(new Media(url));
      
      //If the audio should loop, make the player cycle indefinitely
      if (looping == true) {
        this.player.setCycleCount(MediaPlayer.INDEFINITE);
      } else {
        this.player.setCycleCount(1);
      }
      
    }
    
    public String getId() {
      return id;
    }
  }
  
  /**
   * {@code AudioList} is an extension of {@code ArrayList} specifically for
   * {@code AudioFile} objects which includes methods to add and get
   * objects based on their unique ID String field.
   *
   */
  private class AudioList extends ArrayList<AudioFile> {

    AudioList() {
      super();
    }
    
    /**
     * .
     * @param newFile An AudioFile object to be added to the list. 
     * @return True if the ID is unique, False if the ID is already in use
     *         and thus the object not added
     */
    private Boolean addAudioFile(AudioFile newFile) {
    
      for (int i = 0; i < this.size(); i++) {
        if (this.get(i).getId().equals(newFile.getId())) {
          log.error("Could not register audio: ID "
              + newFile.getId() + " is already used.");
          return false;
        }
      }
      
      this.add(newFile);
      return true;
    }
    
    /**
     * Finds and returns the list entry with a matching ID.
     * 
     * @param id The ID being searched for in the audio list
     * @return Returns the AudioFile object with the same ID parameter, or a null pointer if
     *         there is no list entry with that ID
     */
    private AudioFile getFromId(String id) {
      for (int i = 0; i < this.size(); i++) {
        if (this.get(i).getId().equals(id)) {
          return this.get(i);
        }
      }
      log.error("Warning: Requested AudioFile ID not found. Expect a NullPointerException");
      return null;
    }
  }
  
  /**
   * .
   * @param url  The file path or url of the audio file
   * @param looping A boolean as to whether the audio should repeat indefinitely
   * @param id A unique ID reference to the audio
   * @return Returns True if the requested ID was unique and the file has been registered,
   *         False if not.
   */
  public Boolean registerAudio(String url, Boolean looping, String id) {
    return audios.addAudioFile(new AudioFile(url, looping, id));
  }
  
  /**
   * Starts a registered audio file playing.
   * @param id ID of the audio file to start playing
   * 
   */
  public void startAudio(String id) {
    try {
      audios.getFromId(id).player.play();
    } catch (NullPointerException e) {
      log.error("Cannot play audio: "
          + id + " is not a registered audio ID.", e);
    }
  }
  
  /**
   * Searches through {@code audios} and stops an audio from playing.
   * @param id ID of the audio file to be stopped from playing
   */
  public void stopAudio(String id) {
    try {
      audios.getFromId(id).player.stop();
    } catch (NullPointerException e) {
      log.error("Cannot stop audio: "
          + id + " is not a registered audio ID.", e);
    }
  }
  
  /**
   * Deregisters an audio file.
   * @param id ID
   * @return Returns {@code true} if successful. Returns {@code false}
   *         if the ID was not already registered.
   */
  public Boolean deregisterAudio(String id) {
    for (int i = 0; i < audios.size(); i++) {
      if (audios.get(i).getId().equals(id)) {
        audios.get(i).player.stop();
        audios.remove(i);
        return true;
      }
    }
    log.error("Cannot deregister audio: " + id + " is not a registered ID");
    return false;
  }

  /**
   * CUBIXEL Added Function: Validate provided ID.
   * @author CUBIXEL
   */
  public static boolean validateUrl(String url) {
    try {
      new Media(url);
      log.info("Audio url is valid");
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}