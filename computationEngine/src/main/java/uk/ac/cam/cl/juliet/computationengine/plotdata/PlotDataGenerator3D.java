package uk.ac.cam.cl.juliet.computationengine.plotdata;

import org.apache.commons.math3.complex.Complex;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.range.Range;
import uk.ac.cam.cl.juliet.computationengine.range.RangeResult;
import uk.ac.cam.cl.juliet.computationengine.utility.BlackmanWindow;
import uk.ac.cam.cl.juliet.computationengine.utility.ComplexVector;
import uk.ac.cam.cl.juliet.computationengine.utility.IWindowFunction;

import java.util.ArrayList;
import java.util.List;

public class PlotDataGenerator3D {
    private static final double SCALE_CONSTANT_1 = 1.7833;
    private static final double SCALE_CONSTANT_2 = 5.486;
    private static final double DEFAULT_N_COEF = 1.9;
    private static final double DEFAULT_MAX_DEPTH = 40.0;
    private static final int DEFAULT_PADDING = 2;
    private static final IWindowFunction DEFAULT_WINDOW = new BlackmanWindow();

    private List<Burst> bursts;
    private int padding;
    private double maxDepth;
    private double nCoef;
    private IWindowFunction win;

    private PlotData3D powerPlotData;
    private PlotData3D phaseDiffPlotData;

    private void computePlotData() {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        List<List<Double>> phaseValues = new ArrayList<>();
        List<List<Double>> powerValues = new ArrayList<>();
        RangeResult rangeResult = null;
        RangeResult lastResult = null;

        for (Burst burst : bursts) {
            rangeResult = Range.computeRange(burst, padding, (maxDepth * nCoef / SCALE_CONSTANT_1) + SCALE_CONSTANT_2, win);
            ComplexVector specCor = new ComplexVector();

            for (Complex c : rangeResult.getSpecCor().get(0)) {
                specCor.add(c);
            }

            List<Double> zValues = new ArrayList<>();

            //Compute power
            ComplexVector powerVector = specCor.absElements().logElements().divideByConstant(Math.log(10.0)).multiplyByConstant(20.0);

            for (int i = 0; i < powerVector.size(); i++) {
                zValues.add(powerVector.getReal(i));
            }

            powerValues.add(zValues);

            //Compute phase difference
            zValues = new ArrayList<>();

            if (lastResult == null) {
                for (int i = 0; i < rangeResult.getSpecCor().get(0).size(); i++) {
                    zValues.add(0.0);
                }
            }
            else {
                for (int i = 0; i < rangeResult.getSpecCor().get(0).size(); i++) {
                    Complex curPhase = rangeResult.getSpecCor().get(0).get(i);
                    Complex lastPhase = lastResult.getSpecCor().get(0).get(i);

                    zValues.add((curPhase.multiply(lastPhase.conjugate())).log().getImaginary());
                }
            }

            phaseValues.add(zValues);

            xValues.add(burst.getChirpTime().get(0));
            lastResult = rangeResult;
        }

        for (Double y : rangeResult.getRcoarse()) {
            yValues.add((y - SCALE_CONSTANT_2) * SCALE_CONSTANT_1 / nCoef);
        }

        powerPlotData = new PlotData3D(xValues, yValues, powerValues);
        phaseDiffPlotData = new PlotData3D(xValues, yValues, phaseValues);
    }

    public PlotDataGenerator3D(List<Burst> bursts, int padding, double maxDepth, IWindowFunction win, double nCoef) {
        this.bursts = new ArrayList<>(bursts);
        this.padding = padding;
        this.maxDepth = maxDepth;
        this.win = win;
        this.nCoef = nCoef;

        computePlotData();
    }

    public PlotDataGenerator3D(List<Burst> bursts, int padding, double maxDepth, IWindowFunction win) {
        this.bursts = new ArrayList<>(bursts);
        this.padding = padding;
        this.maxDepth = maxDepth;
        this.win = win;
        this.nCoef = DEFAULT_N_COEF;

        computePlotData();
    }

    public PlotDataGenerator3D(List<Burst> bursts) {
        this.bursts = new ArrayList<>(bursts);
        this.padding = DEFAULT_PADDING;
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.win = DEFAULT_WINDOW;
        this.nCoef = DEFAULT_N_COEF;

        computePlotData();
    }

    public PlotData3D getPowerPlotData() {
        return powerPlotData;
    }

    public PlotData3D getPhaseDiffPlotData() {
        return phaseDiffPlotData;
    }
}
