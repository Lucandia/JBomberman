package com.lucandia;
import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioUtils {

    private static final Map<String, AudioClip> clips = new HashMap<>();

    // Preload method
    public static void preloadSoundEffect(String audioFileName) {
        URL soundEffectUrl = AudioUtils.class.getResource("/audio/" + audioFileName);
        if (soundEffectUrl != null) {
            AudioClip clip = new AudioClip(soundEffectUrl.toString());
            clips.put(audioFileName, clip);
        } else {
            System.err.println("Could not preload the audio file: " + audioFileName);
        }
    }

    // Modified play method to use preloaded clips
    public static void playSoundEffect(String audioFileName) {
        AudioClip clip = clips.get(audioFileName);
        if (clip != null) {
            clip.play();
        } else {
            System.err.println("AudioClip not preloaded: " + audioFileName);
        }
    }

    // Preload all your sound effects
    public static void preloadAll() {
        preloadSoundEffect("BombExplodes.mp3");
        preloadSoundEffect("BombermanDies.mp3");
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
