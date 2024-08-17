package org.projectempire.lx.pattern.ui.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.pattern.LXPattern;

@LXCategory("Empire")
public class IndicatorPattern extends LXPattern {
    // TODO: implements UIDeviceControls<IndicatorPattern>
    public final ColorParameter color1 = new ColorParameter("Color1").setDescription("Color of the pattern");
    public final ColorParameter color2 = new ColorParameter("Color2").setDescription("Color of the pattern");
    public final ColorParameter color3 = new ColorParameter("Color3").setDescription("Color of the pattern");

    private double runtime = 0;

    public IndicatorPattern(LX lx) {
        this(lx, LXColor.RED,
                LXColor.hsb(60, 100, 100), // Yellow
                LXColor.GREEN);
    }

    public IndicatorPattern(LX lx, int color1, int color2, int color3) {
        super(lx);
        this.color1.setColor(color1);
        this.color2.setColor(color2);
        this.color3.setColor(color3);
        addParameter("color1", this.color1);
        addParameter("color2", this.color2);
        addParameter("color3", this.color3);
    }

    @Override
    protected void run(double deltaMs) {
        runtime += deltaMs;
        if (runtime >= 5000) {
            // Let's change something every 5 seconds
            for (LXModel fixture : model.children) {
                int color = 0;
                int nextColor = (int) (Math.random() * 3);
                switch (nextColor) {
                    case 0:
                        color = color1.getColor();
                        break;
                    case 1:
                        color = color2.getColor();
                        break;
                    case 2:
                    default:
                        color = color3.getColor();
                        break;
                }
                for (int i = 0; i < fixture.points.length; i++) {
                    colors[fixture.points[i].index] = color;
                }
            }
            runtime = 0;
        }
    }
}
