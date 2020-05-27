package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * // TODO Test Description.
 *
 * @author Eric Walker
 */
public class VideoHandlerTest {

  protected VideoHandler videoHandler;

  /**
   * Tests adding a single video to the map.
   */
  public void registerOneVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:0",
        (float)0.5, (float)0.5, true);
    assertTrue(videoHandler.getVideosMap().size() == 1);
  }

  /**
   * Tests registering two videos to the map.
   */
  public void registerTwoVideos() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:0",
        (float)0.5, (float)0.5, true);
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    assertTrue(videoHandler.getVideosMap().size() == 2);
  }

  /**
   * Tests registering two videos to the map with identical IDs.
   */
  public void registerTwoVideosSameId() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:0",
        (float)0.5, (float)0.5, true);
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:0",
        (float)0.5, (float)0.5, true);
    assertTrue(videoHandler.getVideosMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Video.
   */
  public void deregisterExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    videoHandler.deregisterVideo("0:1");
    assertTrue(videoHandler.getVideosMap().size() == 0);
  }

  /**
   * Tests deregistering a non-extant Video.
   */
  public void deregisterNonExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:2",
        (float)0.5, (float)0.5, true);
    videoHandler.deregisterVideo("0:1");
    assertTrue(videoHandler.getVideosMap().size() == 1);
  }

  /**
   * Tests deregistering an extant Video twice.
   */
  public void deregisterExtantVideoTwice() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:0",
        (float)0.5, (float)0.5, true);
    videoHandler.deregisterVideo("0:1");
    videoHandler.deregisterVideo("0:1");
    assertTrue(videoHandler.getVideosMap().size() == 1);
  }

  /**
   * Tests drawing an extant video.
   */
  public void drawExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(videoHandler.startVideo("0:1"));
    
  }

  /**
   * Tests drawing a non-extant video.
   */
  public void drawNonExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertFalse(videoHandler.startVideo("0:2"));
    
  }
  
  /**
   * Tests drawing an extant video twice.
   */
  public void drawExtantVideoTwice() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:2",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    assertTrue(videoHandler.startVideo("0:1"));
    assertFalse(videoHandler.startVideo("0:1"));
    
  }

  /**
   * Tests undrawing an extant video.
   */
  public void unDrawExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    videoHandler.startVideo("0:1");
    assertTrue(videoHandler.stopVideo("0:1"));
    
  }

  /**
   * Tests undrawing a non-extant video.
   */
  public void unDrawNonExtantVideo() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    videoHandler.startVideo("0:1");
    assertFalse(videoHandler.stopVideo("0:2"));
    
  }
  
  /**
   * Tests undrawing an extant video twice.
   */
  public void unDrawExtantVideoTwice() {
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:1",
        (float)0.5, (float)0.5, true);
    videoHandler.registerVideo("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8", "0:2",
        (float)0.5, (float)0.5, true);
    //can't mock the pane, so we just gotta trust that the return is correct
    videoHandler.startVideo("0:1");
    videoHandler.startVideo("0:2");
    assertTrue(videoHandler.stopVideo("0:1"));
    assertFalse(videoHandler.stopVideo("0:1"));
    
  }

}
