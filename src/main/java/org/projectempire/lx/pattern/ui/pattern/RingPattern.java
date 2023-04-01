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
        // TODO!
        double rot = rotation.getValue();
        float offset = (float)(rot * (Math.PI/180));
        for (LXPoint p : model.points) {
            float b = Math.abs(LXUtils.sinf((offset + p.theta) / 2f));
            colors[p.index] = LX.hsb(color.hue.getValuef(), color.saturation.getValuef(), b * 100);
        }
    }
}
