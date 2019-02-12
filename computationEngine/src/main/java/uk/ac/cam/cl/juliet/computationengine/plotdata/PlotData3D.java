package uk.ac.cam.cl.juliet.computationengine.plotdata;

import java.util.ArrayList;
import java.util.List;

/**
 * A class encapsulating 3D/Heatmap plot data.
 */
public class PlotData3D {
    private List<Double> xValues;
    private List<Double> yValues;
    private List<List<Double>> zValues;

    public PlotData3D(List<Double> xValues, List<Double> yValues, List<List<Double>> zValues) {
        this.xValues = new ArrayList<>(xValues);
        this.yValues = new ArrayList<>(yValues);

        this.zValues = new ArrayList<>();
        for (List<Double> zCol : zValues) {
            this.zValues.add(new ArrayList<Double>(zCol));
        }
    }

    public List<Double> getXValues() {
        return new ArrayList<>(xValues);
    }

    public List<Double> getYValues() {
        return new ArrayList<>(yValues);
    }

    public List<List<Double>> getZValues() {
        List<List<Double>> clone = new ArrayList<>();

        for (List<Double> zCol : zValues) {
            clone.add(new ArrayList<Double>(zCol));
        }

        return clone;
    }
}
