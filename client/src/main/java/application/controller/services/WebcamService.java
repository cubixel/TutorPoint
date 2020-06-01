package application.controller.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.ImageView;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;


public class WebcamService extends Thread {
  private String streamID;
  private ImageView view;
  private boolean exit = false;
  //TODO Select camera and audio sources
  private static final int WEBCAM_DEVICE_INDEX = 0;
  //private static final int AUDIO_DEVICE_INDEX = 4;

  private static final int FRAME_RATE = 30;
  private static final int GOP_LENGTH_IN_FRAMES = 60;

  private static long startTime = 0;
  private static long videoTS = 0;

  public WebcamService(MainConnection connection, ImageView viewer,String streamID) throws 
      Exception, FrameRecorder.Exception {
    this.streamID = streamID;
    this.view = viewer;
  }

  @Override
  public void run() {
    // TODO - Nothing to run?
    //Webcam camera = Webcam.getDefault();
    //camera.open();
    JavaFXFrameConverter converter = new JavaFXFrameConverter();

    //TODO Try and get native camera resolution
    final int captureWidth = 160;
    final int captureHeight = 90;

    //TODO Create Default if we can't list devices look at soraxo see if index match
    final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
    grabber.setImageWidth(captureWidth);
    grabber.setImageHeight(captureHeight);
    try {
      grabber.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      this.view.setImage(converter.convert(grabber.grab()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // filename = either a path to a local file we wish to create, or an
    // RTMP url to a server
    // imageWidth = width we specified for the grabber
    // imageHeight = height we specified for the grabber
    // audioChannels = 2, because stereo
    //TODO Add server address
    final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
        "rtmp://cubixelservers.uksouth.cloudapp.azure.com/show/" + this.streamID,
        captureWidth, captureHeight, 2);
    recorder.setInterleaved(true);

    // decrease "startup" latency in FFMPEG (see:
    // https://trac.ffmpeg.org/wiki/StreamingGuide)
    recorder.setVideoOption("tune", "zerolatency");
    // tradeoff between quality and encode speed
    recorder.setVideoOption("preset", "ultrafast");
    recorder.setVideoOption("crf", "28");
    // 2000 kb/s, reasonable for 720
    //TODO Alter for quality if needed
    recorder.setVideoBitrate(2000000);
    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
    recorder.setFormat("flv");
    // FPS (frames per second)
    recorder.setFrameRate(FRAME_RATE);
    // Key frame interval, in our case every 2 seconds -> 30 (fps) * 2 = 60
    // (gop length)
    recorder.setGopSize(GOP_LENGTH_IN_FRAMES);

    // We don't want variable bitrate audio
    recorder.setAudioOption("crf", "0");
    // Highest quality
    recorder.setAudioQuality(0);
    // 192 Kbps
    recorder.setAudioBitrate(192000);
    recorder.setSampleRate(44100);
    recorder.setAudioChannels(2);
    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

    // Jack 'n coke... do it...
    //TODO Get a preview out of this
    //TODO Preview from server?
    try {
      recorder.start();
      System.out.println("Started recorder");
    } catch (FrameRecorder.Exception e) {
      e.printStackTrace();
    }

    //Audio Capture
    // Pick a format...
    // NOTE: It is better to enumerate the formats that the system supports,
    // because getLine() can error out with any particular format...
    // For us: 44.1 sample rate, 16 bits, stereo, signed, little endian
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

    // Get TargetDataLine with that format
    // Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
    // Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

    final TargetDataLine line = getAudioLine(dataLineInfo);
    ScheduledThreadPoolExecutor audioCapture = null;
    if (line != null) {
      try {
        // Open and start capturing audio
        line.open(audioFormat);
        line.start();

        final int sampleRate = (int) audioFormat.getSampleRate();
        final int numChannels = audioFormat.getChannels();

        // Let's initialize our audio buffer...
        final int audioBufferSize = sampleRate * numChannels;
        final byte[] audioBytes = new byte[audioBufferSize];

        // Using a ScheduledThreadPoolExecutor vs a while loop with
        // a Thread.sleep will allow
        // us to get around some OS specific timing issues, and keep
        // to a more precise
        // clock as the fixed rate accounts for garbage collection
        // time, etc
        // a similar approach could be used for the webcam capture
        // as well, if you wish
        audioCapture = new ScheduledThreadPoolExecutor(1);
        audioCapture.scheduleAtFixedRate(new Runnable() {
          @Override
          public void run() {
            try {
              // Read from the line... non-blocking
              int nbytesRead = 0;
              while (nbytesRead == 0) {
                nbytesRead = line.read(audioBytes, 0, line.available());
              }

              // Since we specified 16 bits in the AudioFormat,
              // we need to convert our read byte[] to short[]
              // (see source from FFmpegFrameRecorder.recordSamples for AV_SAMPLE_FMT_S16)
              // Let's initialize our short[] array
              int nsamplesRead = nbytesRead / 2;
              short[] samples = new short[nsamplesRead];

              // Let's wrap our short[] into a ShortBuffer and
              // pass it to recordSamples
              ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
              ShortBuffer shortBuff = ShortBuffer.wrap(samples, 0, nsamplesRead);

              // recorder is instance of
              // org.bytedeco.javacv.FFmpegFrameRecorder
              recorder.recordSamples(sampleRate, numChannels, shortBuff);
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
              e.printStackTrace();
            }
          }
        }, 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
      } catch (LineUnavailableException e1) {
        e1.printStackTrace();
      }
    }

    Frame capturedFrame = null;
    //JavaFXFrameConverter converter = new JavaFXFrameConverter();

    // While capturing...
    while (!exit) {
      try {
        //TODO Might need sync
        capturedFrame = grabber.grab();
        if (capturedFrame == null) {
          System.out.println("Stream Closed!");
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Show our frame in the preview


      // Let's define our start time...
      // This needs to be initialized as close to when we'll use it as
      // possible,
      // as the delta from assignment to computed time could be too high
      if (startTime == 0) {
        startTime = System.currentTimeMillis();
      }
      // Create timestamp for this frame
      videoTS = 1000 * (System.currentTimeMillis() - startTime);

      // Check for AV drift
      if (videoTS > recorder.getTimestamp()) {
        System.out.println(
            "Lip-flap correction: "
                + videoTS + " : "
                + recorder.getTimestamp() + " -> "
                + (videoTS - recorder.getTimestamp()));

        // We tell the recorder to write this frame at this timestamp
        recorder.setTimestamp(videoTS);
      }
      //System.out.println("Frame");
      // Send the frame to the org.bytedeco.javacv.FFmpegFrameRecorder
      try {
        recorder.record(capturedFrame);
      } catch (FrameRecorder.Exception e) {
        e.printStackTrace();
      }
      this.view.setImage(converter.convert(capturedFrame));
    }

    //TODO Cleanup
    //capturedFrame.de;
    try {
      System.out.println("Stopping");
      if (audioCapture != null) {
        audioCapture.shutdown();
        audioCapture.awaitTermination(500, TimeUnit.MILLISECONDS);
      }
      if (line != null) {
        line.close();
      }
      grabber.close();
      recorder.close();
    } catch (FrameRecorder.Exception | Exception | InterruptedException e) {
      e.printStackTrace();
    }


  }

  private TargetDataLine getAudioLine(DataLine.Info dataLineInfo) {
    // It's possible to have more control over the chosen audio device with this line:
    // TargetDataLine line = (TargetDataLine)mixer.getLine(dataLineInfo);
    try {
      return (TargetDataLine) AudioSystem.getLine(dataLineInfo);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void killService() {
    this.exit = true;
  }
}
