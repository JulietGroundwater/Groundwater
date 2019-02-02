package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;

public class BlackmanWindow implements WindowFunction {
    private double evaluateFunction(int n, int k) {
        return 0.42 - 0.5 * (Math.cos((2.0 * Math.PI * (double) (k)) / (double) (n - 1)))
                + 0.08 * (Math.cos((4.0 * Math.PI * (double) (k)) / (double) (n - 1)));
    }

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
