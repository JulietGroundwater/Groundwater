package uk.ac.cam.cl.juliet.computationengine.utility;

/** An implementation of {@link IWindowFunction} that represents the Blackman Window Function. */
public class BlackmanWindow implements IWindowFunction {
    /**
     * Evaluates a value of the Blackman Window Function of size n. Uses the default Blackman Window
     * Function parameter values.
     *
     * <p>For k less than (n+1)/2 the value W(k) is evaluted.
     *
     * <p>For k not less than (n+1)/2 the value {@code W(n-k-1)} is evaluated.
     *
     * @param n the size of the window
     * @param k the value to compute
     */
    @Override
    public double evaluate(int n, int k) {
        int m = (n + 1) / 2;

        if (k >= m) {
            k = n - k - 1;
        }

        return 0.42
                - 0.5 * (Math.cos((2.0 * Math.PI * (double) (k)) / (double) (n - 1)))
                + 0.08 * (Math.cos((4.0 * Math.PI * (double) (k)) / (double) (n - 1)));
    }
}
