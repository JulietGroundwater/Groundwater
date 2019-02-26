package uk.ac.cam.cl.juliet.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.connection.ConnectionSimulator;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.Datapoint;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;
import uk.ac.cam.cl.juliet.tasks.ILiveProcessingTask;
import uk.ac.cam.cl.juliet.tasks.IProcessingCallback;
import uk.ac.cam.cl.juliet.tasks.LiveProcessingTask;
import uk.ac.cam.cl.juliet.tasks.ProcessingTask;

/**
 * Displays more detail about the currently open data file.
 *
 * @author Ben Cole
 */
public class InfoMoreDetailFragment extends Fragment implements ILiveProcessingTask {

    private WebView webview;
    private TextView webviewText;
    private final int BURST_CODE = 1;
    private InternalDataHandler idh;
    private Map<String, List<PlotData3D>> cache;
    private MenuItem connect;
    private MenuItem measure;
    private MenuItem disconnect;
    private ConnectionSimulator simulator;
    private boolean connected;
    private boolean gatheringData;
    private SingleOrManyBursts currentLiveBursts;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_detail, container, false);

        // Menu boolean
        setHasOptionsMenu(true);
        this.connected = false;
        this.gatheringData = false;

        // Initialise text
        webviewText = (TextView) view.findViewById(R.id.webview_title);

        // Initialise cache
        cache = new HashMap<>();

        // Initialise webview
        webview = (WebView) view.findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());

        idh = InternalDataHandler.getInstance();

        // Listen for file changes
        idh.addListener(
                new InternalDataHandler.FileListener() {
                    @Override
                    public void onChange() {
                        webview.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        updateChart();
                                    }
                                });
                    }
                });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_data, menu);
        measure = menu.findItem(R.id.take_measurement_button);
        connect = menu.findItem(R.id.connect_button);
        disconnect = menu.findItem(R.id.disconnect_button);

        // Only have connect visible if we aren't running any data gathering
        toggleMenuItems();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_button:
                establishConnection();
                return true;
            case R.id.take_measurement_button:
                gatherData();
                return true;
            case R.id.disconnect_button:
                destroyConnection();
                return true;
        }
        return false;
    }

    /**
     * This method checks to see if we have the right type of file selected and then runs the
     * processing if we haven't already cached the results from a previous round of processing. The
     * uniqueness in the cache is dependent on the file/directory name.
     */
    private void updateChart() {
        if (checkFile()) {
            // Create datapoints, json-ise and pass to Javascript
            webviewText.setText(idh.getSelectedData().getNameToDisplay());

            List<Datapoint> datapoints = new ArrayList<>();
            if (!cache.containsKey(idh.getSelectedData().getNameToDisplay())) {
                ProcessingTask task = new ProcessingTask(this);
                task.execute();
            } else {
                List<PlotData3D> dataSets = cache.get(idh.getSelectedData().getNameToDisplay());
                for (int set = 0; set < dataSets.size(); set++) {
                    PlotData3D current = dataSets.get(set);
                    for (int y = 0; y < current.getYValues().size(); y++) {
                        datapoints.add(
                                new Datapoint(
                                        set,
                                        current.getYValues().get(y),
                                        current.getZValues().get(0).get(y)));
                    }
                }
                updateWebview(datapoints);
            }
        }
    }

    /**
     * Checking for many bursts
     *
     * @return <code>boolean</code> if it is a many-burst file
     */
    private boolean checkFile() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        if (idh.getSelectedData() == null) return false;
        return idh.getSelectedData().getIsManyBursts();
    }

    /**
     * A method for passing the datapoints to the webview and JSON-ising them
     *
     * @param datapoints
     */
    private void updateWebview(final List<Datapoint> datapoints) {
        webview.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // After the HTML page loads, run JS to initialize graph
                        Gson gson = new Gson();
                        // Convert the data to json which the D3 can handle
                        String json = gson.toJson(datapoints);
                        webview.loadUrl("javascript:initGraph(" + json + ")");
                    }
                });
        // Load base html from the assets directory
        webview.loadUrl("file:///android_asset/html/graph.html");
    }

    /** Establishes a connection and waits for the data gathering to commence */
    private void establishConnection() {
        // We know we will have a good connection so change buttons
        this.connected = true;
        toggleMenuItems();

        simulator = ConnectionSimulator.getInstance();

        // Create a new directory in groundwater for the incoming data
        String name = new Date().toString();
        idh.setCurrentLiveData(name);
        idh.addNewDirectory(idh.getCurrentLiveData());
        idh.setProcessingLiveData(true);

        // Create store for the bursts
        List<SingleOrManyBursts> singles = new ArrayList<>();
        this.currentLiveBursts = new SingleOrManyBursts(singles, false, idh.getCurrentLiveData());

        AsyncTask.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        // Connect to our simulated connection
                        InternalDataHandler idh = InternalDataHandler.getInstance();
                        simulator.connect();

                        // TODO: Perhaps sleep the thread instead of spinning and wake it

                        while (simulator.getConnecitonLive()) {
                            while (!simulator.getTransientFiles().isEmpty()
                                    || simulator.getDataReady()) {
                                List<File> batch = new ArrayList<>();
                                batch.add(simulator.pollData());
                                if (batch.size() > 0) {
                                    for (File file : batch) {
                                        if (file != null) {
                                            idh.addFileToDirectory(idh.getCurrentLiveData(), file);
                                            processLiveData(batch, !simulator.getDataReady());
                                        }
                                    }
                                }
                            }
                        }
                        simulator.disconnect();
                    }
                });
    }

    /**
     * Starts a new <code>LiveProcessingTask</code> to process the data coming in
     *
     * @param batch - the list of files to process
     */
    private void processLiveData(List<File> batch, boolean lastFile) {
        List<IProcessingCallback> listeners = new ArrayList<>();
        listeners.add(this);
        // TODO: This is undoubtedly a hack and should be fixed in the future...
        listeners.add(
                (IProcessingCallback)
                        getParentFragment()
                                .getFragmentManager()
                                .findFragmentByTag("android:switcher:" + 2131230781 + ":1"));
        LiveProcessingTask task = new LiveProcessingTask(listeners, batch, lastFile);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** Called to initialise the data gathering phase */
    private void gatherData() {
        ConnectionSimulator simulator = ConnectionSimulator.getInstance();
        simulator.beginDataGathering();
        this.gatheringData = true;
        toggleMeasuringButton();
    }

    /** Destroying the connection and setting the menu items correctly */
    private void destroyConnection() {
        this.connected = false;
        simulator.disconnect();
        idh.setProcessingLiveData(false);
        // TODO: Same hack as above - communication between fragments is difficult
        DataFragment dataFragment =
                (DataFragment)
                        getParentFragment()
                                .getFragmentManager()
                                .findFragmentByTag("android:switcher:" + 2131230781 + ":1");
        dataFragment.notifyFilesChanged(this.currentLiveBursts);
        toggleMenuItems();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case BURST_CODE:
                {
                    updateChart();
                }
        }
    }

    @Override
    public void onTaskCompleted(
            List<Datapoint> result, List<PlotData3D> dataset, boolean isLive, boolean isLast) {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        // If we are drawing live data then we need to be updating the cached values because we
        // don't yet have them all
        if (isLive) {
            if (cache.containsKey(idh.getCurrentLiveData())) {
                cache.get(idh.getCurrentLiveData()).addAll(dataset);
            } else {
                List<PlotData3D> data = new ArrayList<>(dataset);
                cache.put(idh.getCurrentLiveData(), data);
            }
            updateWebview(generateDatapoints(cache.get(idh.getCurrentLiveData())));
        } else {
            cache.put(idh.getSelectedData().getNameToDisplay(), dataset);
            updateWebview(result);
        }

        // For live processing we need to check for the last file received
        if (isLast) {
            this.connected = false;
            idh.setProcessingLiveData(false);
            // TODO: Same hack as above - communication between fragments is difficult
            DataFragment dataFragment =
                    (DataFragment)
                            getParentFragment()
                                    .getFragmentManager()
                                    .findFragmentByTag("android:switcher:" + 2131230781 + ":1");
            dataFragment.notifyFilesChanged(this.currentLiveBursts);
            toggleMenuItems();
        }
    }

    /**
     * For generating the datapoints to plot from the <code>PlotData3D</code> dataset
     *
     * @param datasets - the processed data collections
     * @return <code>List<Datapoint></code> - the plottable points
     */
    private List<Datapoint> generateDatapoints(List<PlotData3D> datasets) {
        // Convert time to natural numbers for the x-axis
        Map<Double, Integer> converter = new HashMap<>();
        int count = 1;
        for (int set = 0; set < datasets.size(); set++) {
            PlotData3D current = datasets.get(set);
            for (int x = 0; x < current.getXValues().size(); x++) {
                if (!converter.containsKey(current.getXValues().get(x))) {
                    converter.put(current.getXValues().get(x), count);
                    count++;
                }
            }
        }

        List<Datapoint> datapoints = new ArrayList<>();
        // Convert to datapoints for JSON serialisation later
        for (int set = 0; set < datasets.size(); set++) {
            PlotData3D current = datasets.get(set);
            for (int x = 0; x < current.getXValues().size(); x++) {
                for (int y = 0; y < current.getYValues().size(); y++) {
                    datapoints.add(
                            new Datapoint(
                                    converter.get(current.getXValues().get(x)),
                                    current.getYValues().get(y),
                                    current.getZValues().get(0).get(y)));
                }
            }
        }
        return datapoints;
    }

    /** Helper function for measure button toggling */
    private void toggleMeasuringButton() {
        if (this.gatheringData) {
            measure.setEnabled(false);
            measure.setVisible(false);
        }
    }

    /** Helper function for menu items toggling */
    private void toggleMenuItems() {
        if (!connected) {
            connect.setVisible(true);
            connect.setEnabled(true);
            disconnect.setVisible(false);
            disconnect.setEnabled(false);
            measure.setVisible(false);
            measure.setEnabled(false);
        } else {
            connect.setVisible(false);
            connect.setEnabled(false);
            disconnect.setVisible(true);
            disconnect.setEnabled(true);
            measure.setVisible(true);
            measure.setEnabled(true);
        }
    }

    /**
     * When computing live data we need to keep track of this in order to update the Internal Data
     * Handler correctly.
     *
     * @param bursts - the few bursts that are computed at a time
     */
    @Override
    public void receiveSingleOrManyBursts(List<SingleOrManyBursts> bursts) {
        try {
            // Adding the small list (usually of one) to the current live bursts session
            this.currentLiveBursts.getListOfBursts().addAll(bursts);
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        }
    }
}
