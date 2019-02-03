package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;

/** An implementation of {@link WindowFunction} that represents the Blackman Window Function. */
public class BlackmanWindow implements WindowFunction {
    /**
     * Evaluate {@code W(k)} for Blackman Window Function {@code W} of size {@code n}. Uses the
     * default Blackman Window Function parameter values
     *
     * @param n the size of the window
     * @param k the value to compute
     * @return value of {@code W(k)}
     */
    private double evaluateFunction(int n, int k) {
        return 0.42
                - 0.5 * (Math.cos((2.0 * Math.PI * (double) (k)) / (double) (n - 1)))
                + 0.08 * (Math.cos((4.0 * Math.PI * (double) (k)) / (double) (n - 1)));
    }

    /**
     * Evaluates the default Blackman Window Function of size {@code n} in all integer values from
     * {@code 0} to {@code n-1}.
     *
     * @param n The size of the Window Function
     * @return An {@link ArrayList} containing the evaluated values of the Blackman Window Function
     */
    @Override
    public ArrayList<Double> generateWindow(int n) {
        ArrayList<Double> window = new ArrayList<>();

        int m = (n + 1) / 2;
        for (int i = 0; i < m; i++) {
            window.add(evaluateFunction(n, i));
        }

        for (int i = m - 1 - (n % 2); i >= 0; i--) {
            window.add(evaluateFunction(n, i));
        }

        return window;
    }
}
