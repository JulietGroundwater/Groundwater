package uk.ac.cam.cl.juliet.computationengine.plotdata;

import java.util.ArrayList;
import java.util.List;

public class PlotData2D {
    private List<Double> xValues;
    private List<Double> yValues;

    public PlotData2D(List<Double> xValues, List<Double> yValues) {
        this.xValues = new ArrayList<>(xValues);
        this.yValues = new ArrayList<>(yValues);
    }

    public List<Double> getXValues() {
        return new ArrayList<>(xValues);
    }

    public List<Double> getYValues() {
        return new ArrayList<>(yValues);
    }
}
