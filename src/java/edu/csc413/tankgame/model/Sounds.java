package edu.csc413.tankgame.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sounds {
    private static Clip clip = null;

    static {
        File file = new File("resources/Sound_lost.wav");
        AudioInputStream audioStream = null;

        try {
            audioStream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        try {
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void PewPewSound() {
        clip.setFramePosition(0);
        clip.start();
    }
}