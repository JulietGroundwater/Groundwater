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

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.hss.heatmaplib.HeatMap;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;

/**
 * Displays more detail about the currently open data file.
 *
 * @author Ben Cole
 */
public class InfoMoreDetailFragment extends Fragment {

    private ScatterChart scatter;
    private final int BURST_CODE = 1;
    private InternalDataHandler idh;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_detail, container, false);

        scatter = view.findViewById(R.id.threeD_Chart);

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
                        BURST_CODE);

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
        System.out.println("IS THIS BAD?");
        int BATCH_SIZE = 1;
        List<String> filenames = idh.getCollectionOfFiles("collection-2017-03-29");
        System.out.println(filenames);
        List<Burst> bursts = new ArrayList<>();
        List<PlotData3D> dataSets = new ArrayList<>();

        try {
          for (int batch = 0; batch < filenames.size() / BATCH_SIZE; batch++) {
          PlotDataGenerator3D pdg;
            for (int j = 0; j < BATCH_SIZE; j++) {
              bursts.add(new Burst(idh.getFileByNameIn("collection-2017-03-29", filenames.get(batch * BATCH_SIZE + j)), 1));
              pdg = new PlotDataGenerator3D(bursts);
              dataSets.add(pdg.getPowerPlotData());
              bursts.clear();
            }
          }
        } catch (InvalidBurstException ibe) {
            ibe.printStackTrace();
        }

        File dir = idh.getFileByName("test.csv");
        Writer writer = null;
        try {
            writer = new FileWriter(dir);
            for (int d = 0; d < dataSets.size(); d++) {
                for(int i = 0; i < dataSets.get(d).getXValues().size(); i++) {
                    for(int j = 0; j < dataSets.get(d).getYValues().size(); j++) {
                        writer.write(d + "," + dataSets.get(d).getYValues().get(j) +"," + dataSets.get(d).getZValues().get(i).get(j) + " \n");
                    }
                }
            }
        } catch(IOException io) {
            io.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case BURST_CODE: {
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
