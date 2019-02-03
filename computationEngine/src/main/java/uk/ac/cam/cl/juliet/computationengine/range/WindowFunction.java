package uk.ac.cam.cl.juliet.computationengine.range;

import java.util.ArrayList;

/**
 * An interface defining a Window Function
 */
public interface WindowFunction {
    /**
     * Computes the corresponding window function in all integer values from {@code 0} to {@code n-1}.
     * @param n The size of the Window Function
     * @return An {@link ArrayList} containing the evaluated values of the Window Function
     */
    ArrayList<Double> generateWindow(int n);
}
