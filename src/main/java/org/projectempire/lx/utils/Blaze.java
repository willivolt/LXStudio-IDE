package org.projectempire.lx.utils;

import heronarts.lx.LX;
import heronarts.lx.utils.LXUtils;

public class Blaze {
    private static final LXUtils.LookupTable.Sin sin = new LXUtils.LookupTable.Sin(720);

    /**
     * Sin approximation.
     * <p>
     * Convince method, so we only need one lookup table.
     */
    public static float sin(float v) {
        return sin.sin(v);
    }

    /**
     * A sawtooth waveform between 0.0 and 1.0 that loops about every 65.536*interval seconds. e.g. use .015
     * for approximately 1 second.
     */
    public static float time(float interval) {
        return (System.currentTimeMillis() % (long) (65536 * interval)) / (65536 * interval);
    }

    /**
     * Converts a sawtooth waveform v between 0.0 and 1.0 to a sinusoidal waveform between 0.0 to 1.0. Same as
     * (1+sin(v*PI2))/2 but faster. v "wraps" between 0.0 and 1.0.
     */
    public static float wave(float v) {
        return (1f + sin.sin(v * LX.TWO_PIf)) / 2f;
    }

    /**
     * Converts a sawtooth waveform v between 0.0 and 1.0 to a triangle waveform between 0.0 to 1.0. v "wraps"
     * between 0.0 and 1.0.
     */
    public static float triangle(float v) {
        return 1f - 2f * Math.abs(0.5f - v);
    }
}
