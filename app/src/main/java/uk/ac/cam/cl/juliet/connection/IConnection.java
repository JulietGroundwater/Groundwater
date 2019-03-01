package uk.ac.cam.cl.juliet.connection;

import uk.ac.cam.cl.juliet.computationengine.Config;

/** Representing the interface for connecting to the radar device */
public interface IConnection {
    /** Establish a connection with the device */
    void connect();

    /** Destroy the connection gracefully with the device */
    void disconnect();

    /** Test the connection - should return true if everything is okay */
    boolean testConnection();

    /**
     * Send the configuration file to the device
     *
     * @param configuration
     */
    void sendConfigurations(Config configuration);

    /** Start a data gathering sessions - most likely will be asynchronous */
    void beginDataGathering();

    /** Interrupt the data gathering process gracefully */
    void interruptDataGathering();

    /** Notify the connection that a file is ready to be processed */
    void notifyDataReady();

    /** Notify the connection that all data has been sent and no longer should wait */
    void dataFinished();
}
