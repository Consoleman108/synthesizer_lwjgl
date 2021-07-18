package com.consoleman.synthesizer;

import com.consoleman.synthesizer.audio.Audio;
import com.consoleman.synthesizer.audio.AudioInfo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class View {
    public static final String TITLE = "Audio Synthesizer";
    public static final int WIDTH = 613;
    public static final int HIGH = 357;
    static final int BUFFER_SIZE = 512;
    static final int BUFFER_COUNT = 8;

    private boolean shouldGenerate;
    private int wavePos;
    private final JFrame frame = new JFrame(TITLE);
    private final Audio audio = new Audio(() -> {
        if(!shouldGenerate) {
            return null;
        }
        short[] s = new short[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; ++i) {
            s[i] = (short) (Short.MAX_VALUE * Math.sin((2 * Math.PI * AudioInfo.FREQUENCY_C5) / AudioInfo.SAMPLE_RATE * wavePos++));
        }
        return s;
    });


    public View() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               if(!audio.isRunning()) {
                   shouldGenerate = true;
                   audio.playback();
               }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                shouldGenerate = false;
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audio.close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HIGH);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
