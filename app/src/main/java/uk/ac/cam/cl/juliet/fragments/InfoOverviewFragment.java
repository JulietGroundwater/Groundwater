package uk.ac.cam.cl.juliet.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
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
        return view;
    }

    private void updateChart() {
        if (checkFile()) {
            showProcessing();
            try {
                PlotDataGenerator2D twoDimDataGen = null;
                PlotData2D twoDimData = null;

                // Compute burst
                SingleOrManyBursts file = idh.getSelectedData();
                File fileToProcess = idh.getSelectedDataFile();
                Burst burst = new Burst(fileToProcess);
                file.setSingleBurst(burst); // getting an invalid burst exception here

                // Check the cache in case the same file was selected again and it is already
                // computed
                if (cache.containsKey(idh.getSelectedDataFile().getAbsolutePath())) {
                    twoDimData = cache.get(idh.getSelectedDataFile().getAbsolutePath());
                } else {
                    twoDimDataGen = new PlotDataGenerator2D(idh.getSelectedData().getSingleBurst());
                    twoDimData = twoDimDataGen.getAmpPlotData();
                    // Add to the cache
                    cache.put(idh.getSelectedDataFile().getAbsolutePath(), twoDimData);
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
                exampleChart.postInvalidate();
            } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException e) {
                e.printStackTrace();
            } catch (InvalidBurstException e) {
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
}
