package com.esame;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Questa classe gestisce la riproduzione di musica di sottofondo nel gioco.
 */
public class BackgroundMusic {

    /**
     * Il lettore multimediale per la riproduzione della musica.
     */
    private MediaPlayer mediaPlayer;

    /**
     * Riproduce la musica di sottofondo specificata dal nome del file audio.
     *
     * @param audioFileName il nome del file audio da riprodurre
     */
    public void playMusic(String audioFileName) {
        try {
            // Carica il file audio
            URL musicFileUrl = getClass().getResource("/audio/" + audioFileName);
            if (musicFileUrl != null) {
                Media media = new Media(musicFileUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.seek(javafx.util.Duration.ZERO); // Torna all'inizio
                    }
                });
                mediaPlayer.play();
                mediaPlayer.setVolume(0.7);
            } else {
                System.err.println("Impossibile trovare il file audio: " + audioFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante la riproduzione del file audio.");
        }
    }

    /**
     * Ferma la riproduzione della musica di sottofondo.
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

}
