package com.consoleman.synthesizer;

import static org.lwjgl.openal.AL10.*;

public class OpenALException extends RuntimeException {
    public OpenALException(int errCode) {
        super("Internal " + (errCode == AL_INVALID_NAME ? "invalid name" : errCode == AL_INVALID_ENUM ? "invalid enum" : errCode == AL_INVALID_VALUE
        ? "invalid value" : errCode == AL_INVALID_OPERATION ? "invalid operation" : "unknown") + "OpenAL exception");
    }
}
