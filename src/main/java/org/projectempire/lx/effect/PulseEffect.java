package org.projectempire.lx.effect;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.BoundedParameter;

@LXCategory("Empire")
public class PulseEffect extends LXEffect {
    BoundedParameter period = new BoundedParameter("Period", 8000, 1000, 20000);
    SawLFO offset = new SawLFO(0, LX.TWO_PI, period);

    public PulseEffect(LX lx) {
        super(lx);
        addParameter("period", this.period);
        startModulator(offset);
    }

    @Override
    protected void run(double deltaMs, double enabledAmount) {
        final float patternOffset = this.offset.getValuef();
        for (LXPoint p: this.model.points) {
            int i = p.index;
            float radians = patternOffset + (p.zn * LX.TWO_PIf / 4f);
            float b = (((float)Math.sin(radians) + 1f)/ 2f) * 90f;
            this.colors[i] = LXColor.multiply(this.colors[i],
                    LXColor.gray(100 - b), 256);
        }
    }
}
