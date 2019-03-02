package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;
import org.jtransforms.fft.DoubleFFT_1D;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.utility.IWindowFunction;

/** A class corresponding to {@code fmcw_range} */
public class Range {
    /**
     * Computes the Discrete Fourier Transform of {@code arr} using Fast Fourier Transform library
     * JTransforms.
     *
     * @param arr The double array to be transformed
     */
    private static void complexFastFFT(double[] arr) {
        DoubleFFT_1D doubleFFT = new DoubleFFT_1D(arr.length / 2);

        doubleFFT.complexForward(arr);
    }

    /**
     * Java implementation of {@code fmcw_range}. The function parameters correspond to the MATLAB
     * parameters.
     *
     * @return A {@link RangeResult} containing the results.
     */
    public static RangeResult computeRange(
            Burst burst, int padding, double maxrange, IWindowFunction window) {
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
        double[] xvals = new double[nf];

        for (int i = 0; i < nf; i++) {
            xvals[i] = (double) (i) * (ci / (2.0 * B * (double) (padding)));
        }

        int rangeCut = -1;

        for (int i = 0; i < xvals.length; i++) {
            if (xvals[i] <= maxrange) {
                rangeCut = i;
            }
        }

        double[] phiref = new double[nf];

        for (int i = 0; i < nf; i++) {
            phiref[i] = (double) (i) * (2.0 * FastMath.PI * fc) / (B * (double) (padding));

            phiref[i] -=
                    (xvals[i] * xvals[i])
                            * K
                            / (2.0 * B * B * (double) (padding) * (double) (padding));
        }

        List<List<Complex>> spec = new ArrayList<>();
        List<List<Complex>> specCor = new ArrayList<>();

        for (int i = 0; i < nchirps; i++) {
            double[] cv = new double[padding * N];
            double mean = 0.0;

            for (int j = 0; j < N; j++) {
                cv[j] = vif.get(i).get(j);
                mean += cv[j];
            }

            mean /= (double) (N);

            for (int j = 0; j < N; j++) {
                cv[j] -= mean;
                cv[j] *= window.evaluate(N, j);
            }

            for (int j = N; j < padding * N; j++) {
                cv[j] = 0.0;
            }

            ///

            double[] fftVif = new double[2 * padding * N];
            int p = 0;
            for (int j = xn; j < cv.length; j++) {
                fftVif[2 * p] = cv[j];
                fftVif[2 * p + 1] = 0.0;
                p++;
            }

            for (int j = 0; j < xn; j++) {
                fftVif[2 * p] = cv[j];
                fftVif[2 * p + 1] = 0.0;
                p++;
            }

            complexFastFFT(fftVif);

            double rms = 0;

            // Calculate rms
            for (int j = 0; j < N; j++) {
                double val = window.evaluate(N, j);
                rms = rms + (val * val) / (double) (N);
            }
            rms = FastMath.sqrt(rms);

            for (int j = 0; j < p; j++) {
                double scale = FastMath.sqrt((double) (2 * padding)) / (double) (p);
                fftVif[2 * j] *= scale;
                fftVif[2 * j + 1] *= scale;

                fftVif[2 * j] /= rms;
                fftVif[2 * j + 1] /= rms;
            }

            spec.add(new ArrayList<Complex>());
            specCor.add(new ArrayList<Complex>());
            for (int j = 0; j <= rangeCut; j++) {
                spec.get(i).add(new Complex(fftVif[2 * j], fftVif[2 * j + 1]));

                double r = phiref[j];
                double im = 0.0;

                // * (0, -1)
                double hswap = r;
                r = im;
                im = -hswap;

                // exp
                double expReal = FastMath.exp(r);
                r = expReal * FastMath.cos(im);
                im = expReal * FastMath.sin(im);

                // * fftVif
                double nr = r * fftVif[2 * j] - im * fftVif[2 * j + 1];
                double nim = r * fftVif[2 * j + 1] + im * fftVif[2 * j];

                specCor.get(i).add(new Complex(nr, nim));
            }
        }

        List<Double> Rcoarse = new ArrayList<>();

        for (int i = 0; i <= rangeCut; i++) {
            Rcoarse.add(xvals[i]);
        }

        List<List<Double>> Rfine = new ArrayList<>();

        for (int i = 0; i < specCor.size(); i++) {
            Rfine.add(new ArrayList<Double>());

            for (int j = 0; j < specCor.get(0).size(); j++) {
                double divConstant =
                        ((4.0 * Math.PI) / lambdac) - (4.0 * Rcoarse.get(j) * K / (ci * ci));
                double specAngle = specCor.get(i).get(j).log().getImaginary() / divConstant;
                Rfine.get(i).add(specAngle);
            }
        }

        return new RangeResult(Rcoarse, Rfine, spec, specCor);
    }
}
