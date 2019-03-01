package uk.ac.cam.cl.juliet.computationengine.plotdata;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.range.Range;
import uk.ac.cam.cl.juliet.computationengine.range.RangeResult;
import uk.ac.cam.cl.juliet.computationengine.utility.BlackmanWindow;
import uk.ac.cam.cl.juliet.computationengine.utility.IWindowFunction;

/**
 * A class responsible for generating {@link PlotData3D} for various plots from a list of {@link
 * Burst} objects.
 */
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

    /**
     * Creates a generator using the parameters. The parameters correspond to values used in
     * fmcw_range and SidiElAidiApp
     */
    public PlotDataGenerator3D(
            List<Burst> bursts, double maxDepth, double nCoef, int padding, IWindowFunction win) {
        this.bursts = new ArrayList<>(bursts);
        this.maxDepth = maxDepth;
        this.nCoef = nCoef;
        this.padding = padding;
        this.win = win;

        computePlotData();
    }

    /**
     * Creates a generator using the parameters. Uses default padding and window function. The
     * parameters correspond to values used in fmcw_range and SidiElAidiApp
     */
    public PlotDataGenerator3D(List<Burst> bursts, double maxDepth, double nCoef) {
        this.bursts = new ArrayList<>(bursts);
        this.maxDepth = maxDepth;
        this.nCoef = nCoef;
        this.padding = DEFAULT_PADDING;
        this.win = DEFAULT_WINDOW;

        computePlotData();
    }

    /**
     * Creates a generator using the parameters. Uses default refraction index, padding and window
     * function. The parameters correspond to values used in fmcw_range and SidiElAidiApp
     */
    public PlotDataGenerator3D(List<Burst> bursts, double maxDepth) {
        this.bursts = new ArrayList<>(bursts);
        this.maxDepth = maxDepth;
        this.nCoef = DEFAULT_N_COEF;
        this.padding = DEFAULT_PADDING;
        this.win = DEFAULT_WINDOW;

        computePlotData();
    }

    /**
     * Creates a generator using the parameters. Uses default max depth, refraction index, padding
     * and window function. The parameters correspond to values used in fmcw_range and SidiElAidiApp
     */
    public PlotDataGenerator3D(List<Burst> bursts) {
        this.bursts = new ArrayList<>(bursts);
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.nCoef = DEFAULT_N_COEF;
        this.padding = DEFAULT_PADDING;
        this.win = DEFAULT_WINDOW;

        computePlotData();
    }

    /** Computes data for power and phase difference plots. */
    private void computePlotData() {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        List<List<Double>> phaseValues = new ArrayList<>();
        List<List<Double>> powerValues = new ArrayList<>();
        RangeResult rangeResult = null;

        Complex[] specCor = null;
        Complex[] lastSpecCor = null;

        for (Burst burst : bursts) {
            rangeResult =
                    Range.computeRange(
                            burst,
                            padding,
                            (maxDepth * nCoef / SCALE_CONSTANT_1) + SCALE_CONSTANT_2,
                            win);

            specCor = rangeResult.getSpecCor().get(0).toArray(new Complex[0]);

            List<Double> zValues = new ArrayList<>();

            // Compute power
            for (int i = 0; i < specCor.length; i++) {
                double value = FastMath.log(10.0, specCor[i].abs()) * 20.0;
                zValues.add(value);
            }

            powerValues.add(zValues);

            // Compute phase difference
            zValues = new ArrayList<>();

            if (lastSpecCor == null) {
                for (int i = 0; i < specCor.length; i++) {
                    zValues.add(0.0);
                }
            } else {
                for (int i = 0; i < specCor.length; i++) {
                    zValues.add(
                            (specCor[i].multiply(lastSpecCor[i].conjugate())).log().getImaginary());
                }
            }

            phaseValues.add(zValues);

            xValues.add((double) burst.getChirpTime().get(0).getTime());

            lastSpecCor = specCor;
        }

        for (Double y : rangeResult.getRcoarse()) {
            yValues.add((y - SCALE_CONSTANT_2) * SCALE_CONSTANT_1 / nCoef);
        }

        // Remove negative values
        int nonNegativeCutoff = 0;
        for (int i = 0; i < yValues.size(); i++) {
            if (yValues.get(i) >= 0.0) {
                nonNegativeCutoff = i;
                break;
            }
        }

        if (nonNegativeCutoff > 0) {
            yValues.subList(0, nonNegativeCutoff).clear();

            for (int i = 0; i < xValues.size(); i++) {
                powerValues.get(i).subList(0, nonNegativeCutoff).clear();
                phaseValues.get(i).subList(0, nonNegativeCutoff).clear();
            }
        }

        powerPlotData = new PlotData3D(xValues, yValues, powerValues);
        phaseDiffPlotData = new PlotData3D(xValues, yValues, phaseValues);
    }

    /**
     * Returns the power plot data associated with this generator
     *
     * @return A {@link PlotData3D} object containing the power plot data associated with this
     *     generator.
     */
    public PlotData3D getPowerPlotData() {
        return powerPlotData;
    }

    /**
     * Returns the phase difference plot data associated with this generator
     *
     * @return A {@link PlotData3D} object containing the phase difference plot data associated with
     *     this generator.
     */
    public PlotData3D getPhaseDiffPlotData() {
        return phaseDiffPlotData;
    }
}
