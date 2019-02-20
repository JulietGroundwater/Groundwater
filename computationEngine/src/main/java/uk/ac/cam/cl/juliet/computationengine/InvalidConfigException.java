package uk.ac.cam.cl.juliet.computationengine;

public class InvalidConfigException extends ComputationEngineException {

    /**
     * Exception for a malformed argument for a {@code Config.ini} file
     *
     * @param s Message of the exception
     */
    public InvalidConfigException(String s) {
        super(s);
    }
}
