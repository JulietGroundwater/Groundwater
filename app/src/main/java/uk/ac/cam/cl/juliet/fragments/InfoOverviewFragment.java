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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import uk.ac.cam.cl.juliet.models.BurstDataTypes;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the key information page.
 *
 * @author Ben Cole
 */
public class InfoOverviewFragment extends Fragment implements Spinner.OnItemSelectedListener {

    private LineChart exampleChart;
    private Spinner overviewSpinner;
    private InternalDataHandler idh;
    private Map<String, PlotDataGenerator2D> cache;

    private TextView generatingPlotText;
    private ProgressBar generatingPlotSpinner;

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
        idh.addSingleListener(
                new InternalDataHandler.FileListener() {
                    @Override
                    public void onChange() {
                        new AsyncUpdateChartTask(InfoOverviewFragment.this).execute();
                    }
                });

        generatingPlotSpinner = view.findViewById(R.id.generatingPlotSpinner);
        generatingPlotSpinner.setVisibility(View.INVISIBLE);
        generatingPlotText = view.findViewById(R.id.generatingPlotText);
        generatingPlotText.setVisibility(View.INVISIBLE);

        return view;
    }

    private void updateChart() {
        if (checkFile()) {
            try {
                PlotDataGenerator2D twoDimDataGen = null;
                PlotData2D twoDimData = null;

                // Compute burst
                SingleOrManyBursts file = idh.getSingleSelected();
                File fileToProcess = idh.getSingleSelectedDataFile();
                Burst burst = new Burst(fileToProcess);
                file.setSingleBurst(burst); // getting an invalid burst exception here

                // Check the cache in case the same file was selected again and it is already
                // computed
                if (cache.containsKey(idh.getSingleSelectedDataFile().getAbsolutePath())) {
                    twoDimDataGen = cache.get(idh.getSingleSelectedDataFile().getAbsolutePath());
                } else {
                    twoDimDataGen =
                            new PlotDataGenerator2D(idh.getSingleSelected().getSingleBurst());
                    // Add to the cache
                    cache.put(idh.getSingleSelectedDataFile().getAbsolutePath(), twoDimDataGen);
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
                LineDataSet dataset =
                        new LineDataSet(entries, idh.getSingleSelected().getNameToDisplay());
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

    /**
     * Shows or hides the spinner and text that indicate that the application is currently
     * processing the data and generating a plot.
     *
     * @param processing true to show the spinner; false to hide it
     */
    private void setShowProcessing(boolean processing) {
        int chartVisibility = processing ? View.INVISIBLE : View.VISIBLE;
        int spinnerVisibility = processing ? View.VISIBLE : View.INVISIBLE;
        exampleChart.setVisibility(chartVisibility);
        generatingPlotSpinner.setVisibility(spinnerVisibility);
        generatingPlotText.setVisibility(spinnerVisibility);
    }

    private boolean checkFile() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        if (idh.getSingleSelected() == null) return false;
        return idh.getSingleSelected().getIsSingleBurst();
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

    /**
     * Updates the chart with the newly selected data file, and also shows and then hides a spinner
     * to inform the user that processing is underway.
     */
    private static class AsyncUpdateChartTask extends AsyncTask<Void, Void, Void> {

        private InfoOverviewFragment fragment;

        public AsyncUpdateChartTask(InfoOverviewFragment fragment) {
            super();
            this.fragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            fragment.setShowProcessing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fragment.updateChart();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fragment.setShowProcessing(false);
        }
    }
}
