package uk.ac.cam.cl.juliet.computationengine;

public class InvalidConfigException extends Exception {

    /**
     * Exception for a malformed argument for a {@code Config.ini} file
     *
     * @param s Message of the exception
     */
    public InvalidConfigException(String s) {
        super(s);
    }
}
