import java.net.*;
import javax.sound.sampled.*;

public class Player {
    public static void playPreview(String previewUrl) {
        try {
            URL url = new URL(previewUrl);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(30000); // Play for 30 seconds (preview duration)
            clip.stop();
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}