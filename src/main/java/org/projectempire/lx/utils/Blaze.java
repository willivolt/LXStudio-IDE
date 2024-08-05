package org.projectempire.lx.utils;

import heronarts.lx.LX;
import heronarts.lx.utils.LXUtils;

public class Blaze {
    private static final LXUtils.LookupTable.Sin sin = new LXUtils.LookupTable.Sin(720);

    // A sawtooth waveform between 0.0 and 1.0 that loops about every
    // 65.536*interval seconds. e.g. use .015 for approximately 1 second.
    public static float time(float interval) {
        return (System.currentTimeMillis() % (long) (65536 * interval)) / (65536 * interval);
    }

    // Converts a sawtooth waveform v between 0.0 and 1.0 to a sinusoidal waveform between 0.0 to 1.0. Same as
    // (1+sin(v*PI2))/2 but faster. v "wraps" between 0.0 and 1.0.
    public static float wave(float v) {
        return (1f + sin.sin(v * LX.TWO_PIf)) / 2f;
    }

    public static void main(String[] args) {
        float[] values = new float[11];
        for (float i = 0; i <= 1; i += 0.05) {
            System.out.println(String.format("wave(%f) = %f", i, wave(i)));
        }

//        final long startMillis = System.currentTimeMillis();
//        TimerTask task = new TimerTask() {
//            public void run() {
//                System.out.println(String.valueOf((System.currentTimeMillis() - startMillis) / 1000d)
//                        + ": Blaze.time(0.1) = " + Blaze.time(0.015f));
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(task, 0, 50);
//        try {
//            Thread.sleep(13000);
//        } catch (InterruptedException e) {
//            // do nothing
//        }
//        timer.cancel();
    }
}
