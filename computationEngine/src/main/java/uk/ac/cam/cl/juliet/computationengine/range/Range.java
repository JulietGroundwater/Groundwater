package uk.ac.cam.cl.juliet.computationengine.range;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import uk.ac.cam.cl.juliet.computationengine.Burst;

import java.util.ArrayList;
import java.util.List;

public class Range {
    public static void main(String[] args) {
        System.out.println("Cat");

        computeRange(new Burst("test",0), 2, 100, new BlackmanWindow());
    }

    public static RangeResult computeRange(Burst burst, int padding, double maxrange, WindowFunction window) {
        //Extraction
        double B = burst.getB();
        double K = burst.getK();
        double ci = burst.getCi();
        double fc = burst.getFc();
        double lambdac = burst.getLambdac();

        //Processing
        List<List<Double>> vif = burst.getVif();
        int N = vif.get(0).size();
        int xn = (N + 1) / 2;
        int nchirps = vif.size();
        int nf = (padding * N) / 2;

        ComplexVector win = new ComplexVector(window.generateWindow(N));
        ComplexVector xvals = new ComplexVector();

        for (int i = 0; i < nf; i++) {
            xvals.add((double)(i));
        }

        xvals = xvals.multiplyByConstant(ci / (2.0 * B * (double)(padding)));

        int rangeCut = -1;

        for (int i = 0; i < xvals.size(); i++) {
            if (xvals.getReal(i) <= maxrange) {
                rangeCut = i;
            }
        }

        ComplexVector phiref;

        phiref = xvals.multiplyByConstant(2.0 * Math.PI * fc).divideByConstant(B * (double)(padding));
        phiref = phiref.subtract(xvals.squareElements()
                                    .multiplyByConstant(K)
                                    .divideByConstant(2.0 * B * B * (double)(padding) * (double)padding));

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

            FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);

            int powerOfTwo = 1;

            while(powerOfTwo < fftVif.size()) {
                powerOfTwo *= 2;
            }

            //Pad length to power of 2
            while(fftVif.size() < powerOfTwo) {
                fftVif.add(0.0);
            }

            //FFT
            fftVif = new ComplexVector(fastFourierTransformer.transform(fftVif.toArray(), TransformType.FORWARD));

            //Remove padding
            fftVif = fftVif.slice(0, padding * N);

            fftVif = fftVif.multiplyByConstant(Math.sqrt((double)(2 * padding)) / (double)(fftVif.size()));
            fftVif = fftVif.divideByConstant(fftVif.rms());

            spec.add(fftVif.slice(0, rangeCut + 1).toList());
            specCor.add(phiref
                        .multiplyByConstant(new Complex(0, -1))
                        .exp()
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
