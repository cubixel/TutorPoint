package application.controller.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class WebcamService extends Thread{
  private int StreamID;

  final private static int WEBCAM_DEVICE_INDEX = 1;
  final private static int AUDIO_DEVICE_INDEX = 4;

  final private static int FRAME_RATE = 30;
  final private static int GOP_LENGTH_IN_FRAMES = 60;

  private static long startTime = 0;
  private static long videoTS = 0;

  public WebcamService(int StreamID) throws Exception {
    this.StreamID = StreamID;
    final int captureWidth = 1280;
    final int captureHeight = 720;

    // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
    // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber,
    // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
    final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
    grabber.setImageWidth(captureWidth);
    grabber.setImageHeight(captureHeight);
    grabber.start();

    // org.bytedeco.javacv.FFmpegFrameRecorder.FFmpegFrameRecorder(String
    // filename, int imageWidth, int imageHeight, int audioChannels)
    // For each param, we're passing in...
    // filename = either a path to a local file we wish to create, or an
    // RTMP url to an FMS / Wowza server
    // imageWidth = width we specified for the grabber
    // imageHeight = height we specified for the grabber
    // audioChannels = 2, because we like stereo
    final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
        "rtmp://my-streaming-server/app_name_here/instance_name/"+this.StreamID,
        captureWidth, captureHeight, 2);
    recorder.setInterleaved(true);

    // decrease "startup" latency in FFMPEG (see:
    // https://trac.ffmpeg.org/wiki/StreamingGuide)
    recorder.setVideoOption("tune", "zerolatency");
    // tradeoff between quality and encode speed
    // possible values are ultrafast,superfast, veryfast, faster, fast,
    // medium, slow, slower, veryslow
    // ultrafast offers us the least amount of compression (lower encoder
    // CPU) at the cost of a larger stream size
    // at the other end, veryslow provides the best compression (high
    // encoder CPU) while lowering the stream size
    // (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
    recorder.setVideoOption("preset", "ultrafast");
    // Constant Rate Factor (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
    recorder.setVideoOption("crf", "28");
    // 2000 kb/s, reasonable "sane" area for 720
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
    recorder.start();
  }
}
