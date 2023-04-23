package org.projectempire.lx.structure;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.structure.LXBasicFixture;
import heronarts.lx.studio.LXStudio;
import heronarts.lx.studio.ui.fixture.UIFixture;
import heronarts.lx.studio.ui.fixture.UIFixtureControls;
import heronarts.lx.transform.LXMatrix;
import heronarts.lx.transform.LXTransform;
import heronarts.p4lx.ui.UI2dComponent;

import java.util.List;
import java.util.Map;

public class ButtBottomFixture extends LXBasicFixture implements UIFixtureControls<ButtBottomFixture> {
    public static final int MAX_POINTS = 4096;
    public static final float CONTROL_WIDTH = 41.0F;

    public enum ButtMode {
        WIDE("Wide"),
        TALL("Tall");

        private final String label;

        ButtMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

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

    public final EnumParameter<ButtMode> buttMode =
            new EnumParameter<ButtMode>("Mode", ButtMode.WIDE)
                    .setDescription("Whether the arc wide or tall");

    public ButtBottomFixture(LX lx) {
        super(lx, "ButtBottom");
        addMetricsParameter("numPoints", this.numPoints);
        addGeometryParameter("width", this.width);
        addGeometryParameter("height", this.height);
        addGeometryParameter("buttMode", this.buttMode);
    }

    @Override
    protected void computePointGeometry(LXMatrix matrix, List<LXPoint> points) {
        // Length of each curve = 1/2 * pi * height, total of curves => pi * height
        // Length of line segment = width - 2 * height
        // Total length = pi * height + width - 2 * height => (pi - 2) * height + width
        // Spacing = total length / (numPoints - 1)
        float radius = this.height.getValuef();
        final float curveLength = LX.HALF_PIf * radius;
        final float lineLength = this.width.getValuef() - (2f * radius);
        final float totalLength = (2f * curveLength) + lineLength;
        final float spacing = totalLength / (this.numPoints.getValuef() - 1);
        final int firstCurvePoints = (int) (curveLength / spacing);
        final int linePoints = (int) (lineLength / spacing);
        final int secondCurvePoints = this.numPoints.getValuei() - (firstCurvePoints + linePoints);

        LXTransform transform = new LXTransform(matrix);
        switch (this.buttMode.getEnum()) {
            case WIDE:
                // We are drawing right to left. Move the view to the top left
                transform.translateX(-this.width.getValuef() / 2f);
                transform.push(); // Save origin for later return
                // First 1/4 circle
                float rotation = LX.HALF_PIf / firstCurvePoints;
                // TODO: can we figure out how much unused curve there is?
                LX.log(String.format("Transform Start 1: %f, %f", transform.x(), transform.y()));
                for (LXPoint p : points.subList(0, firstCurvePoints)) {
                    p.set(transform);
                    transform.translateX(radius);
                    transform.rotateZ(rotation);
                    transform.translateX(-radius);
                }
                // Line segment
                // Unlikely that spacing on the line segment will be exact, so move to start of line
                transform.pop(); // Return to origin
                transform.push(); // Save again
                transform.translateX(radius);
                transform.translateY(-radius);
                LX.log(String.format("Transform Start 2: %f, %f", transform.x(), transform.y()));
                if (firstCurvePoints < firstCurvePoints + linePoints) {
                    for (LXPoint p : points.subList(firstCurvePoints, firstCurvePoints + linePoints)) {
                        p.set(transform);
                        transform.translateX(spacing);
                    }
                }
                // Second 1/4 circle
                transform.pop(); // Return to origin
                transform.translateX(radius + lineLength);
                transform.translateY(-radius);
                rotation = LX.HALF_PIf / secondCurvePoints;
                LX.log(String.format("Transform Start 3: %f, %f", transform.x(), transform.y()));
                for (LXPoint p : points.subList(firstCurvePoints + linePoints, this.numPoints.getValuei())) {
                    p.set(transform);
                    transform.translateY(radius);
                    transform.rotateZ(rotation);
                    transform.translateY(-radius);
                }
                break;
            case TALL:
                // todo: tall, narrow butt. For now, just a half circle
                transform.rotateZ(-LX.HALF_PIf);
                radius = this.width.getValuef() / 2f;
                rotation = (LX.PIf / (this.points.size() - 1));
                for (LXPoint p : this.points) {
                    transform.translateY(-radius);
                    p.set(transform);
                    transform.translateY(radius);
                    transform.rotateZ(rotation);
                }
                break;

        }
    }

    @Override
    protected int size() {
        return this.numPoints.getValuei();
    }

    @Override
    public String[] getDefaultTags() {
        return new String[]{"butt"};
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
                        uiFixture.newControlBox(buttBottomFixture.height, CONTROL_WIDTH),
                        uiFixture.newControlButton(buttBottomFixture.buttMode, CONTROL_WIDTH)
                },
                {uiFixture.newControlLabel("Points", CONTROL_WIDTH),
                        uiFixture.newControlLabel("Width", CONTROL_WIDTH),
                        uiFixture.newControlLabel("Height", CONTROL_WIDTH),
                        uiFixture.newControlLabel("Mode", CONTROL_WIDTH)
                }});
        uiFixture.addProtocolSection(buttBottomFixture, true);
    }

}
