package com.esame;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Classe per la gestione degli effetti sonori corti (come esplosioni o  inizio del gioco).
 */
public class AudioUtils {

    /**
     * Insieme contenente tutte le clip sonore in riproduzione.
     */
    private static Set<MediaPlayer> playingClips = new HashSet<>();

    /**
     * Mappa contenente gli effetti sonori caricati in memoria.
     */
    private static final Map<String, MediaPlayer> clips = new HashMap<>();

    /**
     * Carica in memoria un effetto sonoro specificato dal nome del file.
     *
     * @param audioFileName Il nome del file audio da caricare.
     */
    public static void preloadSoundEffect(String audioFileName) {
        URL soundEffectUrl = AudioUtils.class.getResource("/audio/" + audioFileName);
        if (soundEffectUrl != null) {
            Media media = new Media(soundEffectUrl.toExternalForm());
            MediaPlayer clip = new MediaPlayer(media);
            clip.setOnEndOfMedia(() -> {
                clip.stop();
                playingClips.remove(clip);
            });
            clips.put(audioFileName, clip);
        } else {
            System.err.println("Impossibile caricare in memoria il file audio: " + audioFileName);
        }
    }

    /**
     * Riproduce un effetto sonoro specificato dal nome del file.
     *
     * @param audioFileName Il nome del file audio da riprodurre.
     */
    public static void playSoundEffect(String audioFileName) {
        MediaPlayer clip = clips.get(audioFileName);
        if (clip != null) {
            if (!playingClips.contains(clip)) {
                clip.play();
                playingClips.add(clip);
            }
        } else {
            System.err.println("Effetto sonoro non caricato in memoria: " + audioFileName);
        }
    }
    

    /**
     * Ferma tutti gli effetti sonori attualmente in riproduzione.
     */
    public static void stopAll() {
        playingClips.forEach(MediaPlayer::stop);
        playingClips.clear();
    }

    /**
     * Carica in memoria tutti gli effetti sonori.
     */
    public static void preloadAll() {
        preloadSoundEffect("BombExplodes.mp3");
        preloadSoundEffect("GameOver.mp3");
        preloadSoundEffect("ItemGet.mp3");
        preloadSoundEffect("LoseLife.mp3");
        preloadSoundEffect("MainMenu.mp3");
        preloadSoundEffect("PlaceBomb.mp3");
        preloadSoundEffect("StageClear.mp3");
        preloadSoundEffect("StageStart.mp3");
        preloadSoundEffect("Walking.mp3");
    }
}
