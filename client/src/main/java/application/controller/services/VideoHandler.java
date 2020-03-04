package application.controller.services;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoHandler {

    public MediaPlayer player = null;
    public MediaView mediaView;

    public VideoHandler(MediaView mediaView){
        this.mediaView = mediaView;
    }

    public void fetchVideo(String VideoRef){

        Media media = new Media(VideoRef);
        this.player = new MediaPlayer(media);
        this.mediaView.setMediaPlayer(player);
        this.player.setAutoPlay(true);
        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

    }
}
