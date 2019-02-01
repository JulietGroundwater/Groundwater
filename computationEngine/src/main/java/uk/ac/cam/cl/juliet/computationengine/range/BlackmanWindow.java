package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;
import java.util.List;

public class BlackmanWindow implements WindowFunction {
    private List<Double> window;
    private int n;

    private double evaluateFunction(int k) {
        return 0.42 - 0.5 * (Math.cos((2.0 * Math.PI * (double)(k)) / (double)(n - 1)))
                + 0.08 * (Math.cos((4.0 * Math.PI * (double)(k)) / (double)(n - 1)));
    }

    public BlackmanWindow(int n) {
        this.n = n;
        window = new ArrayList<>();

        int m = (n + 1) / 2;
        for (int i = 0; i < m; i++) {
            window.add(evaluateFunction(i));
        }

        for (int i = m - 1 - (n % 2); i >= 0; i--) {
            window.add(evaluateFunction(i));
        }
    }

    @Override
    public List<Double> generateWindow() {
        return new ArrayList<>(window);
    }
}
