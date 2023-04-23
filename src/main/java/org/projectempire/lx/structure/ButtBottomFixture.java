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
import heronarts.p4lx.ui.UI2dComponent;

import java.util.List;
import java.util.Map;

public class ButtBottomFixture extends LXBasicFixture implements UIFixtureControls<ButtBottomFixture> {
    public static final int MAX_POINTS = 4096;
    public static final float CONTROL_WIDTH = 41.0F;

    public final DiscreteParameter numPoints = (DiscreteParameter)
            new DiscreteParameter("Num", 30, 1, MAX_POINTS + 1)
                    .setUnits(LXParameter.Units.INTEGER)
                    .setDescription("Number of points in the fixture");

    public final BoundedParameter width =
            new BoundedParameter("Width", 15, 0, 1000000)
                    .setDescription("Width of the fixture in inches");

    public final BoundedParameter height =
            new BoundedParameter("Height", 5, 0, 1000000)
                    .setDescription("Height of the fixture in inches");

    public ButtBottomFixture(LX lx) {
        super(lx, "ButtBottom");
        addMetricsParameter("numPoints", this.numPoints);
        addGeometryParameter("width", this.width);
        addGeometryParameter("height", this.height);
    }

    @Override
    protected void computePointGeometry(LXMatrix transform, List<LXPoint> points) {
        // Length of each curve = 1/2 * pi * height, total of curves => pi * height
        // Length of line segment = width - 2 * height
        // Total length = pi * height + width - 2 * height => (pi - 2) * height + width
        final float radius = this.height.getValuef();
        float curveLength = LX.HALF_PIf * radius;
        float lineLength = this.width.getValuef() - (2f * radius);
        float totalLength = (2f * curveLength) + lineLength;
        float spacing = totalLength / this.numPoints.getValuef();
        int firstCurvePoints = (int)(curveLength / spacing);
        int remainingPoints = this.numPoints.getValuei() - firstCurvePoints;
        int linePoints = (int)(lineLength / spacing);
        int secondCurvePoints = remainingPoints - linePoints;
        float rotation = LX.HALF_PIf / firstCurvePoints;
        int i = 1;
        // First 1/4 circle
        for (LXPoint p: points.subList(0, firstCurvePoints)) {
            p.set(transform);
            LX.log(String.format("Point %d: %f, %f", i++, p.x, p.y));
            transform.translateX(-radius);
            transform.rotateZ(-rotation);
            transform.translateX(radius);
        }
        // Line segment
        //transform.translateY(radius);
        for (LXPoint p: points.subList(firstCurvePoints, firstCurvePoints + linePoints)) {
            p.set(transform);
            LX.log(String.format("Point %d: %f, %f", i++, p.x, p.y));
            transform.translateY(-spacing);
        }
        //transform.translateY(-radius);
        // Second 1/4 circle
        rotation = LX.HALF_PIf / secondCurvePoints;
        for (LXPoint p: points.subList(firstCurvePoints + linePoints, points.size())) {
            p.set(transform);
            LX.log(String.format("Point %d: %f, %f", i++, p.x, p.y));
            transform.translateX(-radius);
            transform.rotateZ(-rotation);
            transform.translateX(radius);
        }
    }

    @Override
    protected int size() {
        return this.numPoints.getValuei();
    }

    @Override
    public String[] getDefaultTags() {
        return new String[]{"Butt"};
    }

    @Override
    public void addModelMetaData(Map<String, String> metaData) {
        metaData.put("numPoints", String.valueOf(this.numPoints.getValuei()));
        metaData.put("height", String.valueOf(this.height.getValue()));
        metaData.put("width", String.valueOf(this.width.getValue()));
    }

    // UI controls
    @Override
    public void buildFixtureControls(LXStudio.UI ui, UIFixture uiFixture, ButtBottomFixture buttBottomFixture) {
        uiFixture.addTagSection();
        uiFixture.addGeometrySection();
        uiFixture.addSection("B Bottom", new UI2dComponent[][]{
                {uiFixture.newControlIntBox(buttBottomFixture.numPoints, CONTROL_WIDTH),
                        uiFixture.newControlBox(buttBottomFixture.width, CONTROL_WIDTH),
                        uiFixture.newControlBox(buttBottomFixture.height, CONTROL_WIDTH)
                },
                {uiFixture.newControlLabel("Points", CONTROL_WIDTH),
                        uiFixture.newControlLabel("Width", CONTROL_WIDTH),
                        uiFixture.newControlLabel("Height", CONTROL_WIDTH)
                }});
        uiFixture.addProtocolSection(buttBottomFixture, true);
    }

}
