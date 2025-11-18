package InteractiveReptail;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    private static boolean isPlaying = false;
    private static long lastSoundTime = 0;
    private static final long MIN_DELAY_MS = 1000; // delay between repeats
    private static float volume = 0.5f;

    public static void playSound(String filename) {
        long currentTime = System.currentTimeMillis();

        // we check if the sound is playing AND if enough time has passed since the last launch
        if (isPlaying || (currentTime - lastSoundTime) < MIN_DELAY_MS) {
            return;
        }

        new Thread(() -> {
            try {
                File soundFile = new File("src/AddFile" + File.separator + filename); // File.separator for different OS
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                isPlaying = true;
                lastSoundTime = System.currentTimeMillis(); // Memorizing the launch time

                // Volume control
                setVolume(clip, volume/10);

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        isPlaying = false;
                        clip.close();
                        try {
                            audioStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
                isPlaying = false;
            }
        }).start();
    }

    // The method for setting the volume
    private static void setVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Convert the linear value (0.0-1.0) to decibels
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);

            // Limiting the value to an acceptable range
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));

            gainControl.setValue(dB);
        }
    }
}