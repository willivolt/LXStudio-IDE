package org.projectempire.lx.effect;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.TriangleLFO;

@LXCategory("Empire")
public class BreatheEffect extends LXEffect {
    TriangleLFO triangleLFO = new TriangleLFO(0, 50, 8000);
    SinLFO sinLFO = new SinLFO(0, 50, 6000);

    public BreatheEffect(LX lx) {
        super(lx);
        startModulator(triangleLFO);
        startModulator(sinLFO);
    }

    @Override
    protected void run(double deltaMs, double enabledAmount) {
        if (enabledAmount == 0) {
            return;
        }

        float dim = sinLFO.getValuef();
        //LX.log("dim: " + dim);
        for (int i = 0; i < this.colors.length; i++) {
            this.colors[i] = LXColor.multiply(colors[i], LXColor.gray(100 - dim), 256);
        }

    }
}
