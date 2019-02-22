package uk.ac.cam.cl.juliet.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.Datapoint;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;
import uk.ac.cam.cl.juliet.tasks.IProcessingCallback;
import uk.ac.cam.cl.juliet.tasks.ProcessingTask;

/**
 * Displays more detail about the currently open data file.
 *
 * @author Ben Cole
 */
public class InfoMoreDetailFragment extends Fragment implements IProcessingCallback {

    private WebView webview;
    private TextView webviewText;
    private final int BURST_CODE = 1;
    private InternalDataHandler idh;
    private Map<String, List<PlotData3D>> cache;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_detail, container, false);

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

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        BURST_CODE);
            }
        } else {
            if (checkFile()) {
                updateChart();
            }
        }

        return view;
    }

    /**
     * This method checks to see if we have the right type of file selected and then runs
     * the processing if we haven't already cached the results from a previous round of processing.
     * The uniqueness in the cache is dependent on the file/directory name.
     */
    private void updateChart() {
        if (checkFile()) {
            // Create datapoints, json-ise and pass to Javascript
            try {
                webviewText.setText(idh.getSelectedData().getNameToDisplay());
                final List<SingleOrManyBursts> singles = idh.getSelectedData().getListOfBursts();
                // Recover data from cache or process new data
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
            } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
                e.printStackTrace();
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
    public void onTaskCompleted(List<Datapoint> result) {
        updateWebview(result);
    }
}
