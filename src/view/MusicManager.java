package view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class MusicManager {

    private static MediaPlayer mediaPlayer;
    private static double volume = 0.5;
    private static boolean muted = false;

    public static void play(String filePath) {
        stop();
        try {
            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(muted ? 0 : volume);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
        } catch (Exception e) {
            System.out.println("Music file not found: " + filePath);
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public static void setVolume(double v) {
        volume = Math.max(0, Math.min(1, v));
        if (mediaPlayer != null && !muted)
            mediaPlayer.setVolume(volume);
    }

    public static double getVolume() { return volume; }

    public static void setMuted(boolean m) {
        muted = m;
        if (mediaPlayer != null)
            mediaPlayer.setVolume(muted ? 0 : volume);
    }

    public static boolean isMuted() { return muted; }
}