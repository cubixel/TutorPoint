package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaPlayerController extends BaseController implements Initializable {


    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;

    private String Resource;

    private Media media;

    public MediaPlayerController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* This is just copied straight from here https://www.youtube.com/watch?v=sjiS4mhb0gQ as an example
         * of some way we could implement this. VLCJ is also an option but javafx does come with this built in
         * media player. */


        this.Resource = "http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8";
        //String path = new File("client/src/main/resources/application/media/TestVideo.mp4").getAbsolutePath();
        media = new Media(this.Resource);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);
        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
    }
}
