package org.projectempire.lx.structure;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.structure.LXBasicFixture;
import heronarts.lx.studio.LXStudio;
import heronarts.lx.studio.ui.fixture.UIFixture;
import heronarts.lx.studio.ui.fixture.UIFixtureControls;
import heronarts.lx.transform.LXMatrix;

import java.util.List;
import java.util.Map;

public class HornSFixture extends LXBasicFixture implements UIFixtureControls<HornSFixture> {
    private static final float SPACING = 0.5468f;
    public static final int MAX_POINTS = 4096;
    public static final float CONTROL_WIDTH = 41.0F;
    public static final float INCHES_PER_METER = 39.3701f;

    public final DiscreteParameter ppm = (DiscreteParameter)
            new DiscreteParameter("PPM", 72, 30, 144)
                    .setUnits(LXParameter.Units.INTEGER)
                    .setDescription("Number of pixels per meter");

    public final DiscreteParameter points = (DiscreteParameter)
            new DiscreteParameter("Points", 150, 1, MAX_POINTS + 1)
                    .setUnits(LXParameter.Units.INTEGER)
                    .setDescription("Number of points in the string");

    public final BoundedParameter radius1 = new BoundedParameter("Radius 1", 100, 0, 1000000)
            .setDescription("Radius of the first part of horn");

    public final BoundedParameter degrees1 = (BoundedParameter) new BoundedParameter("Degrees 1", 90, 0, 360)
            .setUnits(LXParameter.Units.DEGREES)
            .setDescription("Number of degrees the first arc covers");

    public final BoundedParameter radius2 = new BoundedParameter("Radius 2", 100, 0, 1000000)
            .setDescription("Radius of the second part of horn");

    public final BoundedParameter degrees2 = (BoundedParameter) new BoundedParameter("Degrees 2", 90, 0, 360)
            .setUnits(LXParameter.Units.DEGREES)
            .setDescription("Number of degrees the second arc covers");

    public HornSFixture(LX lx) {
        super(lx, "HornS");
    }

    @Override
    protected void computePointGeometry(LXMatrix transform, List<LXPoint> points) {
        // spacing between pixels
        final float pixelSpacing = getPixelSpacing();
        // radius of the first arc
        final float r1 = this.radius1.getValuef();
        // circumference of the first arc
        final float c1 = LX.TWO_PIf * r1;
        // arc length of the first arc
        final float arc1 = c1 * (this.degrees1.getValuef() / 360f);
        // angle between pixels on the first arc
        final float theta1 = LX.TWO_PIf * (pixelSpacing / c1);
        // number of pixels on the first arc
        final int pixelCount1 = (int)(arc1 / pixelSpacing);
        for (int i = 0; i < pixelCount1; i++) {
            LXPoint p = points.get(i);
            p.set(transform);
            transform.translateY(r1);
            transform.rotateZ(theta1);
            transform.translateY(-r1);
        }
        // radius of the second arc
        final float r2 = this.radius2.getValuef();
        // circumference of the second arc
        final float c2 = LX.TWO_PIf * r2;
        // arc length of the second arc
        final float theta2 = LX.TWO_PIf * (pixelSpacing / c2);
        for (int i = pixelCount1; i < points.size(); i++) {
            LXPoint p = points.get(i);
            p.set(transform);
            transform.translateY(-r2);
            transform.rotateZ(theta2);
            transform.translateY(r2);
        }
    }

    private float getPixelSpacing() {
        return 1f / (ppm.getValuei() / INCHES_PER_METER);
    }

    @Override
    protected int size() {
        return points.getValuei();
    }

    @Override
    protected String[] getDefaultTags() {
        return new String[]{"horn"};
    }

    @Override
    protected void addModelMetaData(Map<String, String> metaData) {
        super.addModelMetaData(metaData);
    }

    @Override
    public void buildFixtureControls(LXStudio.UI ui, UIFixture uiFixture, HornSFixture hornSFixture) {

    }
}
