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
 * A class responsible for generating {@link PlotData2D} for various plots from a single {@link
 * Burst}.
 */
public class PlotDataGenerator2D {
    private static final int DEFAULT_PADDING = 2;
    private static final double DEFAULT_MAXRANGE = 100.0;
    private static final IWindowFunction DEFAULT_WINDOW = new BlackmanWindow();

    private Burst burst;
    private int padding;
    private double maxrange;
    private IWindowFunction win;

    private PlotData2D timePlotData;
    private PlotData2D amplitudePlotData;
    private PlotData2D phasePlotData;

    /**
     * Creates a generator using the parameters. The parameters correspond to values used in
     * fmcw_range.
     */
    public PlotDataGenerator2D(Burst burst, double maxrange, int padding, IWindowFunction win) {
        this.burst = burst;
        this.maxrange = maxrange;
        this.padding = padding;
        this.win = win;

        computePlotData();
    }

    /**
     * Creates a generator using the parameters. Uses default padding and window function. The
     * parameters correspond to values used in fmcw_range.
     */
    public PlotDataGenerator2D(Burst burst, double maxrange) {
        this.burst = burst;
        this.maxrange = maxrange;
        this.padding = DEFAULT_PADDING;
        this.win = DEFAULT_WINDOW;

        computePlotData();
    }

    /**
     * Creates a generator using the parameters. Uses default padding, maxrange and window function.
     * The parameters correspond to values used in fmcw_range.
     */
    public PlotDataGenerator2D(Burst burst) {
        this.burst = burst;
        this.maxrange = DEFAULT_MAXRANGE;
        this.padding = DEFAULT_PADDING;
        this.win = DEFAULT_WINDOW;

        computePlotData();
    }

    /** Computes data for time, amplitude and phase plots. */
    private void computePlotData() {
        RangeResult rangeResult = Range.computeRange(burst, padding, maxrange, win);
        List<Double> xValues;
        List<Double> yValues;

        // Compute time plot data
        xValues = burst.getTList();
        yValues = burst.getVif().get(0);

        timePlotData = new PlotData2D(xValues, yValues);

        // Compute amplitude and phase plot data
        xValues = rangeResult.getRcoarse();
        Complex[] spec = rangeResult.getSpec().get(0).toArray(new Complex[0]);

        yValues = new ArrayList<>();
        for (int i = 0; i < spec.length; i++) {
            yValues.add(spec[i].getArgument());
        }

        amplitudePlotData = new PlotData2D(xValues, yValues);

        // Phase
        yValues = new ArrayList<>();
        for (int i = 0; i < spec.length; i++) {
            double value = FastMath.log(10.0, spec[i].abs()) * 20.0;
            yValues.add(value);
        }

        phasePlotData = new PlotData2D(xValues, yValues);
    }

    /**
     * Returns the time plot data associated with this generator
     *
     * @return A {@link PlotData2D} object containing the time plot data associated with this
     *     generator.
     */
    public PlotData2D getTimePlotData() {
        return timePlotData;
    }

    /**
     * Returns the amplitude plot data associated with this generator
     *
     * @return A {@link PlotData2D} object containing the amplitude plot data associated with this
     *     generator.
     */
    public PlotData2D getAmpPlotData() {
        return amplitudePlotData;
    }

    /**
     * Returns the phase plot data associated with this generator
     *
     * @return A {@link PlotData2D} object containing the phase plot data associated with this
     *     generator.
     */
    public PlotData2D getPhasePlotData() {
        return phasePlotData;
    }
}
