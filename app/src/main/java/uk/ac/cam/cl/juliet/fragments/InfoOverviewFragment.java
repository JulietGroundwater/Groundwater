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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator2D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;

/**
 * Fragment for the key information page.
 *
 * @author Ben Cole
 */
public class InfoOverviewFragment extends Fragment {

    private LineChart exampleChart;
    private final int READ_CONSTANT = 1;
    private Burst burst;
    private InternalDataHandler idh;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_overview, container, false);
        // Create the example chart
        exampleChart = (LineChart) view.findViewById(R.id.twoD_chart);

        // Try external storage
        idh = InternalDataHandler.getInstance();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
        try {
            File file = idh.getFileByName("DATA2018-04-17-1300.DAT");
            burst = new Burst(file, 1);
//            PlotDataGenerator2D twoDimData = new PlotDataGenerator2D(burst);

            // Generate entries for the chart
//            List<Entry> entries = new ArrayList<>();
//            Iterator<Double> xs = twoDimData.getAmpPlotData().getXValues().iterator();
//            Iterator<Double> ys = twoDimData.getAmpPlotData().getYValues().iterator();
//
//            while(xs.hasNext() && ys.hasNext()) {
//                entries.add(new Entry(xs.next().floatValue(), ys.next().floatValue()));
//            }

            // Create a line data set and then the line data
//            LineDataSet dataset = new LineDataSet(entries, "Two Dim. Data");
//            LineData data = new LineData(dataset);
//
//            // Set the data and invalidate the chart (re-render)
//            exampleChart.setData(data);
//            exampleChart.setPinchZoom(true);
//            exampleChart.setDragEnabled(true);
//            exampleChart.invalidate();
        } catch (InvalidBurstException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CONSTANT: {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        updateChart();
                    }
                });
            }
        }
    }
}
