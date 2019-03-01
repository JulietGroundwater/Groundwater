package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.utility.IWindowFunction;

/**
 * A class representing the result of {@link Range#computeRange(Burst, int, double,
 * IWindowFunction)}
 */
public class RangeResult {
    private List<Double> Rcoarse;
    private List<List<Double>> Rfine;
    private List<List<Complex>> spec;
    private List<List<Complex>> specCor;

    public RangeResult(
            List<Double> Rcoarse,
            List<List<Double>> Rfine,
            List<List<Complex>> spec,
            List<List<Complex>> specCor) {
        this.Rcoarse = Rcoarse;
        this.Rfine = Rfine;
        this.spec = spec;
        this.specCor = specCor;
    }

    public int getSize() {
        return Rcoarse.size();
    }

    public List<Double> getRcoarse() {
        return new ArrayList<>(Rcoarse);
    }

    public List<List<Double>> getRfine() {
        List<List<Double>> clone = new ArrayList<>();

        for (List<Double> row : Rfine) {
            clone.add(new ArrayList<>(row));
        }

        return clone;
    }

    public List<List<Complex>> getSpec() {
        List<List<Complex>> clone = new ArrayList<>();

        for (List<Complex> row : spec) {
            clone.add(new ArrayList<>(row));
        }

        return clone;
    }

    public List<List<Complex>> getSpecCor() {
        List<List<Complex>> clone = new ArrayList<>();

        for (List<Complex> row : specCor) {
            clone.add(new ArrayList<>(row));
        }

        return clone;
    }
}
