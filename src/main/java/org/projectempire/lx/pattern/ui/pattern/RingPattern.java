package org.projectempire.lx.pattern.ui.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.utils.LXUtils;

@LXCategory("Nozzle")
public class RingPattern extends LXPattern {
    private static final int NUM_BANDS = 5;
    public final ColorParameter color = new ColorParameter("Color").setDescription("Color of the pattern");
    public final CompoundParameter speed = new CompoundParameter("Speed", 1000, 500, 5000);

    final SawLFO rotation = startModulator(new SawLFO(0, 360, speed));

    public RingPattern(LX lx, int color) {
        super(lx);
        this.color.setColor(color);
        this.speed.setUnits(LXParameter.Units.MILLISECONDS); // TODO: Hertz?
        addParameter("color", this.color);
        addParameter("speed", this.speed);
    }

    public RingPattern(LX lx) {
        this(lx, LXColor.RED);
    }

    @Override
    protected void run(double deltaMs) {
        double rot = rotation.getValue();
        float offset = (float)(rot * (Math.PI/180));
        for (LXPoint p : model.points) {
            int band = (int)(p.zn / (1f / (float)(NUM_BANDS - 1)));
            int sign = (band % 2 == 0) ? -1 : 1;
            float b = Math.abs(LXUtils.sinf(((sign * offset) + p.theta) / 1f));
            //b = Math.abs(LXUtils.trif((sign * offset) + (p.theta / LX.TWO_PIf)));
            colors[p.index] = LX.hsb(color.hue.getValuef(), color.saturation.getValuef(), b * 100);
        }
    }
}
