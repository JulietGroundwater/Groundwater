package uk.ac.cam.cl.juliet.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData2D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator2D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the key information page.
 *
 * @author Ben Cole
 */
public class InfoOverviewFragment extends Fragment {

    private LineChart exampleChart;
    private final int READ_CONSTANT = 1;
    private InternalDataHandler idh;
    private Map<String, PlotData2D> cache;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_overview, container, false);

        // Create the example chart
        exampleChart = (LineChart) view.findViewById(R.id.twoD_chart);
        exampleChart.setPinchZoom(true);
        exampleChart.setDragEnabled(true);

        // Initialise the cache
        cache = new HashMap<>();

        // Try external storage
        idh = InternalDataHandler.getInstance();

        // Listen for file changes
        idh.addListener(
                new InternalDataHandler.FileListener() {
                    @Override
                    public void onChange() {
                        AsyncTask.execute(
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
                        READ_CONSTANT);

                // READ_CONSTANT is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            updateChart();
        }
        return view;
    }

    private void updateChart() {
        if (checkFile()) {
            showProcessing();
            try {
                PlotDataGenerator2D twoDimDataGen = null;
                PlotData2D twoDimData = null;

                // Check the cache in case the same file was selected again and it is already
                // computed
                if (cache.containsKey(idh.getSelectedData().getNameToDisplay())) {
                    twoDimData = cache.get(idh.getSelectedData().getNameToDisplay());
                } else {
                    twoDimDataGen = new PlotDataGenerator2D(idh.getSelectedData().getSingleBurst());
                    twoDimData = twoDimDataGen.getAmpPlotData();
                    // Add to the cache
                    cache.put(idh.getSelectedData().getNameToDisplay(), twoDimData);
                }

                // Generate entries for the chart
                List<Entry> entries = new ArrayList<>();
                Iterator<Double> xs = twoDimData.getXValues().iterator();
                Iterator<Double> ys = twoDimData.getYValues().iterator();

                while (xs.hasNext() && ys.hasNext()) {
                    entries.add(new Entry(xs.next().floatValue(), ys.next().floatValue()));
                }

                // Create a line data set and then the line data
                LineDataSet dataset = new LineDataSet(entries, "Two Dim. Data");
                LineData data = new LineData(dataset);

                // Set the data and invalidate the chart (re-render)
                exampleChart.setData(data);
                exampleChart.invalidate();
            } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException e) {
                e.printStackTrace();
            }
        }
    }

    private void showProcessing() {
        // TODO: Add a spinny wheel or something
    }

    private boolean checkFile() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        if (idh.getSelectedData() == null) return false;
        return idh.getSelectedData().getIsSingleBurst();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CONSTANT:
                {
                    AsyncTask.execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    updateChart();
                                }
                            });
                }
        }
    }
}
