package uk.ac.cam.cl.juliet.computationenginetest;

import uk.ac.cam.cl.juliet.computationengine.ComputationEngine;

/**
 * Class used to allow the running of a {@code ComputationEngine} without requiring the android app.
 */
public class ComputationEngineTest {

    /**
     * The main function for the app.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        ComputationEngine engine = new ComputationEngine();
        System.out.println("Hello World");
    }
}
