package com.lucandia;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class BackgroundMusic {

    private MediaPlayer mediaPlayer;

    public void playMusic(String audioFileName) {
        try {
            // Loading the audio file
            URL musicFileUrl = getClass().getResource("/audio/" + audioFileName);
            if (musicFileUrl != null) {
                Media media = new Media(musicFileUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.seek(javafx.util.Duration.ZERO); // Loop back to the start
                    }
                });
                Platform.runLater(() -> {
                    mediaPlayer.play();
                });
                // mediaPlayer.play();
                // mediaPlayer.setVolume(0.7);
            } else {
                System.err.println("Could not find the audio file: " + audioFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error playing the audio file.");
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }
}
