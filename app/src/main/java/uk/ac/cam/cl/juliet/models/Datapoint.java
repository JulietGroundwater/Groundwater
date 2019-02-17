package uk.ac.cam.cl.juliet.models;

/**
 * A simple object for easy JSON serialising of the three dimensional data
 */
public class Datapoint {
    private double x;
    private double y;
    private double z;
    public Datapoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}