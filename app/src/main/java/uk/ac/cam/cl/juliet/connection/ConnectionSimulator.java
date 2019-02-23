package uk.ac.cam.cl.juliet.connection;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import uk.ac.cam.cl.juliet.computationengine.Config;

/** Our simulation for a connection to the radar device */
public class ConnectionSimulator implements IConnection {
    private ConcurrentLinkedQueue<File> transientFiles;
    private AtomicBoolean connectionLive;
    private AtomicBoolean dataReady;
    private DeviceSimulator device;
    private static ConnectionSimulator INSTANCE;

    private ConnectionSimulator(DeviceSimulator device) {
        this.device = device;
        this.transientFiles = new ConcurrentLinkedQueue<>();
        this.dataReady = new AtomicBoolean(false);
        this.connectionLive = new AtomicBoolean(false);
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
            device.addConnection(this);
            this.connectionLive.set(true);
        }
    }

    @Override
    public void disconnect() {
        if (device != null) {
            device.destoryConnection(this);
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
        this.connectionLive.set(true);
        device.takeMeasurement(transientFiles);
    }

    public File pollData() {
        if (transientFiles.isEmpty()) {
            this.dataReady.set(false);
        } else {
            this.dataReady.set(true);
        }
        return transientFiles.poll();
    }

    @Override
    public void interruptDataGathering() {
        // TODO: Implement interruption
    }

    @Override
    public void notifyDataReady() {
        this.dataReady.set(true);
    }

    @Override
    public void dataFinished() {
        disconnect();
        this.connectionLive.set(false);
    }

    public ConcurrentLinkedQueue<File> getTransientFiles() {
        return transientFiles;
    }

    public boolean getDataReady() {
        return this.dataReady.get();
    }

    public boolean getConnecitonLive() {
        return this.connectionLive.get();
    }
}
