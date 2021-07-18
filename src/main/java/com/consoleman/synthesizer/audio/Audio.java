package com.consoleman.synthesizer.audio;
import com.consoleman.synthesizer.OpenALException;
import com.consoleman.synthesizer.utils.Utils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class Audio extends Thread {
    static final int BUFFER_SIZE = 512;
    static final int BUFFER_COUNT = 8;

    private final Supplier<short[]> bufferSupplier;
    private final int[] buffers = new int[BUFFER_COUNT];
    private final long device = alcOpenDevice(alcGetString(0, ALC_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device, new int[1]);
    private final int source;

    private int bufferIndex;
    private boolean closed;
    private boolean running;

    public Audio(Supplier<short[]> bufferSupplier) {
        this.bufferSupplier = bufferSupplier;
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
        source = alGenSources();
        for (int i = 0; i < BUFFER_COUNT; i++) {
            bufferSamples(new short[0]);
        }

        alSourcePlay(source);
        catchInternalException();
        start();
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            while (!running) {
                Utils.handleProcedure(this::wait, false);
            }
            int processedBuf = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i = 0; i < processedBuf; ++i) {
                short[] samples = bufferSupplier.get();
                if(samples == null) {
                    running = false;
                    break;
                }

                alDeleteBuffers(alSourceUnqueueBuffers(source));
                buffers[bufferIndex] = alGenBuffers();
                bufferSamples(samples);
            }
            if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) {
                alSourcePlay(source);
            }
            catchInternalException();
        }
        alDeleteSources(source);
        alDeleteBuffers(buffers);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    private void bufferSamples(short[] samples) {
        int buf = buffers[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, AudioInfo.SAMPLE_RATE);
        alSourceQueueBuffers(source, buf);
        bufferIndex %=BUFFER_COUNT;
    }

    private void catchInternalException() {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new OpenALException(err);
        }
    }

    public synchronized void playback() {
        running = true;
        notify();
    }

    public void close() {
        closed = true;
        playback();
    }

    public boolean isRunning() {
        return running;
    }
}
