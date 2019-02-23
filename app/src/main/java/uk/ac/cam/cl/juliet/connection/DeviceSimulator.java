package uk.ac.cam.cl.juliet.connection;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import uk.ac.cam.cl.juliet.computationengine.Config;

/** An object to represent the radar device */
public class DeviceSimulator {
    // All files for mocking should be place in a folder called data_files
    private final String DATA_FILE = "data_files";
    private File root;
    private int delay;
    private String deviceName;
    private List<IConnection> connections;
    private Config configuration;

    /**
     * Constructor for the simulated device
     *
     * @param name - a name for the device
     * @param delay - the delay between sending data files
     */
    public DeviceSimulator(String name, int delay) {
        this.root =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath(), DATA_FILE);
        this.deviceName = name;
        this.delay = delay;
        this.connections = new ArrayList<>();
    }

    /**
     * Adds a listening connection (for our use their is only one)
     *
     * @param connection - the connection which should implement the connection interface <code>
     *     IConnection</code>
     */
    public void addConnection(IConnection connection) {
        connections.add(connection);
    }

    /**
     * Removes the specified listening connection
     *
     * @param connection - the connection to remove
     */
    public void destoryConnection(IConnection connection) {
        connections.remove(connection);
    }

    /**
     * A simple mock way to set the configuration file
     *
     * @param config - the configuration file
     */
    public void setConfiguration(Config config) {
        this.configuration = config;
    }

    /**
     * A mock data gathering method that runs on a separate thread. It sleeps for a specified time
     * between each of the file transfers. If the connections become empty then it returns.
     *
     * @param queue - A concurrent queue that the connection will be reading from as this device
     *     writes to
     */
    public void takeMeasurement(final ConcurrentLinkedQueue<File> queue) {
        if (root.listFiles() == null) {
            System.out.println("NO FILES PLEASE ADD A FOLDER CALLED DATA_FILES");
            // Notify all connections that we are done after reading all files
            for (IConnection connection : connections) {
                connection.dataFinished();
            }
            return;
        }

        Thread thread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                for (int fileIndex = 0;
                                        fileIndex < root.listFiles().length;
                                        fileIndex++) {

                                    // Return if no more connections
                                    if (connections.isEmpty()) {
                                        return;
                                    }

                                    // Otherwise add a file to the queue and notify the connections
                                    queue.add(root.listFiles()[fileIndex]);
                                    for (IConnection connection : connections) {
                                        connection.notifyDataReady();
                                    }

                                    // Mimic dath gathering by sleeping for a specified time
                                    try {
                                        Thread.sleep(delay);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Notify all connections that we are done after reading all files
                                for (IConnection connection : connections) {
                                    connection.dataFinished();
                                }
                            }
                        });
        thread.start();
    }
}
