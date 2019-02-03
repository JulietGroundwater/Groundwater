package uk.ac.cam.cl.juliet.computationengine.range;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;
import uk.ac.cam.cl.juliet.computationengine.Burst;

import java.util.ArrayList;
import java.util.List;

/**
 * A class corresponding to {@code fmcw_range}
 */
public class Range {
    /**
     * Computes the Discrete Fourier Transform of {@code arr} using Fast Fourier Transform
     * library JTransforms.
     *
     * @param arr The {@link ComplexVector} to be transformed
     * @return A new {@link ComplexVector} containing the DFT values
     */
    private static ComplexVector complexFFT(ComplexVector arr) {
        DoubleFFT_1D doubleFFT = new DoubleFFT_1D(arr.size());
        ComplexVector result = new ComplexVector();
        double[] fft = new double[arr.size() * 2];

        for (int i = 0; i < arr.size(); i++) {
            fft[2 * i] = arr.getReal(i);
            fft[2 * i + 1] = arr.getImaginary(i);
        }

        doubleFFT.complexForward(fft);

        for (int i = 0; i < arr.size(); i++) {
            result.add(new Complex(fft[2 * i], fft[2 * i + 1]));
        }

        return result;
    }

    /**
     * Java implementation of {@code fmcw_range}. The function parameters correspond to the
     * MATLAB parameters.
     *
     * @return A {@link RangeResult} containing the results.
     */
    public static RangeResult computeRange(Burst burst, int padding, double maxrange, WindowFunction window) {
        double B = burst.getB();
        double K = burst.getK();
        double ci = burst.getCi();
        double fc = burst.getFc();
        double lambdac = burst.getLambdac();
        List<List<Double>> vif = burst.getVif();
        int N = vif.get(0).size();
        int xn = (N + 1) / 2;
        int nchirps = vif.size();
        int nf = (padding * N) / 2;
        ComplexVector win = new ComplexVector(window.generateWindow(N));
        ComplexVector xvals;
        ComplexVector rangeToN = new ComplexVector();

        for (int i = 0; i < nf; i++) {
            rangeToN.add((double) (i));
        }

        xvals = rangeToN.multiplyByConstant(ci / (2.0 * B * (double) (padding)));

        int rangeCut = -1;

        for (int i = 0; i < xvals.size(); i++) {
            if (xvals.getReal(i) <= maxrange) {
                rangeCut = i;
            }
        }

        ComplexVector phiref;

        phiref = rangeToN.multiplyByConstant(2.0 * Math.PI * fc).divideByConstant(B * (double) (padding));
        phiref = phiref.subtractElements(xvals.squareElements()
                .multiplyByConstant(K)
                .divideByConstant(2.0 * B * B * (double) (padding) * (double) padding));

        List<List<Complex>> spec = new ArrayList<>();
        List<List<Complex>> specCor = new ArrayList<>();

        for (int i = 0; i < nchirps; i++) {
            ComplexVector curVif = new ComplexVector(vif.get(i));

            curVif = curVif.addConstant(curVif.mean().negate());
            curVif = win.multiplyElements(curVif);

            ComplexVector paddedVif = new ComplexVector();

            for (int j = 0; j < curVif.size(); j++) {
                paddedVif.add(curVif.get(j));
            }

            for (int j = curVif.size(); j < padding * N; j++) {
                paddedVif.add(0.0);
            }

            ComplexVector fftVif = paddedVif.slice(xn, paddedVif.size());

            for (int j = 0; j < xn; j++) {
                fftVif.add(paddedVif.get(j));
            }

            fftVif = complexFFT(fftVif);

            fftVif = fftVif.multiplyByConstant(Math.sqrt((double) (2 * padding)) / (double) (fftVif.size()));

            fftVif = fftVif.divideByConstant(win.rms());

            spec.add(fftVif.slice(0, rangeCut + 1).toList());
            specCor.add(phiref
                    .multiplyByConstant(new Complex(0, -1))
                    .expElements()
                    .multiplyElements(fftVif.slice(0, nf))
                    .slice(0, rangeCut + 1)
                    .toList());
        }

        List<Double> Rcoarse = new ArrayList<>();

        for (int i = 0; i < rangeCut + 1; i++) {
            Rcoarse.add(xvals.getReal(i));
        }

        List<List<Double>> Rfine = new ArrayList<>();

        for (int i = 0; i < specCor.size(); i++) {
            Rfine.add(new ArrayList<Double>());

            for (int j = 0; j < specCor.get(0).size(); j++) {
                Double divConstant = ((4.0 * Math.PI) / lambdac) - (4.0 * Rcoarse.get(j) * K / (ci * ci));
                Double specAngle = specCor.get(i).get(j).log().getImaginary() / divConstant;
                Rfine.get(i).add(specAngle);
            }
        }

        return new RangeResult(Rcoarse, Rfine, spec, specCor);
    }
}
