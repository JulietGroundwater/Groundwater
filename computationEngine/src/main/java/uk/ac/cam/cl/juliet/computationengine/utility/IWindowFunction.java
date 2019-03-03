package uk.ac.cam.cl.juliet.computationengine.utility;

/** An interface defining a Window Function */
public interface IWindowFunction {
    /**
     * Evaluates the corresponding window function
     *
     * @param n The size of the Window Function
     * @param k The value to evaluate
     * @return
     */
    double evaluate(int n, int k);
}
