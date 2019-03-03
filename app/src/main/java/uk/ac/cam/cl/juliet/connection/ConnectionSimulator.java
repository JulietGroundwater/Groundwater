package uk.ac.cam.cl.juliet.connection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import uk.ac.cam.cl.juliet.computationengine.Config;

/** Our simulation for a connection to the radar device */
public class ConnectionSimulator implements IConnection {
    private ConcurrentLinkedQueue<File> transientFiles;
    private boolean connectionLive;
    private boolean dataReady;
    private DeviceSimulator device;
    private List<ConnectionListener> listeners = new ArrayList<>();
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
            for (ConnectionListener listener : listeners) {
                listener.onConnectionChange(this.connectionLive);
            }
        }
    }

    @Override
    public void disconnect() {
        this.connectionLive = false;
        this.dataReady = false;
        if (device != null) {
            device.destoryConnection();
            for (ConnectionListener listener : listeners) {
                listener.onConnectionChange(this.connectionLive);
            }
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

    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
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

    public interface ConnectionListener {
        void onConnectionChange(boolean isConnected);
    }
}
