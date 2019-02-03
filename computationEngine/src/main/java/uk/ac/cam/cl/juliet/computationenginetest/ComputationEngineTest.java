package uk.ac.cam.cl.juliet.computationenginetest;

import uk.ac.cam.cl.juliet.computationengine.ComputationEngine;
import uk.ac.cam.cl.juliet.computationengine.Vdat;

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
        Vdat d = new Vdat("/home/chris/Downloads/DATA2018-04-17-1300.DAT", 1, 40000);
        System.out.println("Hello World");
    }
}
