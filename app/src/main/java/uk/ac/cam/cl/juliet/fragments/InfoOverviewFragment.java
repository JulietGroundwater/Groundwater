package uk.ac.cam.cl.juliet.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import uk.ac.cam.cl.juliet.models.BurstDataTypes;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the key information page.
 *
 * @author Ben Cole
 */
public class InfoOverviewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private LineChart exampleChart;
    private Spinner overviewSpinner;
    private InternalDataHandler idh;
    private Map<String, PlotDataGenerator2D> cache;

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

        // Create the spinner and set values
        overviewSpinner = view.findViewById(R.id.overview_spinner);
        String[] datatypes =
                new String[] {
                    BurstDataTypes.AMPLITUDE.getDisplayableName(),
                    BurstDataTypes.PHASE.getDisplayableName(),
                    BurstDataTypes.TIME.getDisplayableName()
                };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(), R.layout.support_simple_spinner_dropdown_item, datatypes);
        overviewSpinner.setAdapter(adapter);
        overviewSpinner.setOnItemSelectedListener(this);

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
                file.setSingleBurst(new Burst(idh.getFileByName(file.getNameToDisplay()), 1));

                // Check the cache in case the same file was selected again and it is already
                // computed
                if (cache.containsKey(idh.getSelectedData().getNameToDisplay())) {
                    twoDimDataGen = cache.get(idh.getSelectedData().getNameToDisplay());
                } else {
                    twoDimDataGen = new PlotDataGenerator2D(idh.getSelectedData().getSingleBurst());
                    // Add to the cache
                    cache.put(idh.getSelectedData().getNameToDisplay(), twoDimDataGen);
                }

                // Choose the correct data to visualise
                BurstDataTypes selected =
                        BurstDataTypes.fromString((String) overviewSpinner.getSelectedItem());
                if (selected == BurstDataTypes.AMPLITUDE) {
                    twoDimData = twoDimDataGen.getAmpPlotData();
                } else if (selected == BurstDataTypes.PHASE) {
                    twoDimData = twoDimDataGen.getPhasePlotData();
                } else {
                    twoDimData = twoDimDataGen.getTimePlotData();
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
                dataset.setCircleColor(Color.LTGRAY);
                dataset.setColor(ColorTemplate.MATERIAL_COLORS[0]);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AsyncTask.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        updateChart();
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("Spinner", "Nothing selected");
    }
}
