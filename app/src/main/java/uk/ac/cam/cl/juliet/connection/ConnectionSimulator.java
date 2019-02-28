package uk.ac.cam.cl.juliet.connection;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import uk.ac.cam.cl.juliet.computationengine.Config;

/** Our simulation for a connection to the radar device */
public class ConnectionSimulator implements IConnection {
    private ConcurrentLinkedQueue<File> transientFiles;
    private boolean connectionLive;
    private boolean dataReady;
    private DeviceSimulator device;
    private static ConnectionSimulator INSTANCE;

    private ConnectionSimulator(DeviceSimulator device) {
        this.device = device;
        this.transientFiles = new ConcurrentLinkedQueue<>();
        this.dataReady = false;
        this.connectionLive = false;
    }

    public static ConnectionSimulator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionSimulator(new DeviceSimulator("BAS_RADAR_123", 10000));
        }
        return INSTANCE;
    }

    @Override
    public void connect() {
        if (device != null) {
            this.connectionLive = device.addConnection(this);
        }
    }

    @Override
    public void disconnect() {
        this.connectionLive = false;
        this.dataReady = false;
        if (device != null) {
            device.destoryConnection();
        }
    }

    @Override
    public boolean testConnection() {
        return true;
    }

    @Override
    public void sendConfigurations(Config configuration) {
        device.setConfiguration(configuration);
    }

    @Override
    public void beginDataGathering() {
        this.connectionLive = true;
        device.takeMeasurement(transientFiles);
    }

    public File pollData() {
        return transientFiles.poll();
    }

    @Override
    public void interruptDataGathering() {
        // TODO: Implement interruption
    }

    @Override
    public void notifyDataReady() {
        this.dataReady = true;
    }

    @Override
    public void dataFinished() {
        disconnect();
    }

    public ConcurrentLinkedQueue<File> getTransientFiles() {
        return transientFiles;
    }

    public boolean getDataReady() {
        return this.dataReady;
    }

    public boolean getConnecitonLive() {
        return this.connectionLive;
    }
}
