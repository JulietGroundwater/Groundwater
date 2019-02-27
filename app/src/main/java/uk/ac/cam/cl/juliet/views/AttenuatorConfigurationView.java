package uk.ac.cam.cl.juliet.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import uk.ac.cam.cl.juliet.R;

public class AttenuatorConfigurationView extends ConstraintLayout {

    private static final int ATTENUATION_MIN = 0;
    private static final int ATTENUATION_MAX = 31;

    private SeekBar attenuationSeekBar;
    private TextView attenuationValueOutput;
    private Spinner gainSpinner;

    private int[] possibleGainValues;
    private int chosenGainIndex;

    public AttenuatorConfigurationView(Context context) {
        super(context);
        inflateViews(context);
    }

    public AttenuatorConfigurationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateViews(context);
    }

    public AttenuatorConfigurationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateViews(context);
    }

    /**
     * Inflates the views from the layout resource file.
     *
     * @param context The context from which this is called
     */
    private void inflateViews(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listelement_attenuator_config, this);

        // Generate references to Views
        attenuationSeekBar = view.findViewById(R.id.attenuationSeekBar);
        attenuationValueOutput = view.findViewById(R.id.attenuationOutputText);
        gainSpinner = view.findViewById(R.id.gainSpinner);

        // Set the bounds of the attenuation seek bar
        attenuationSeekBar.setMax(ATTENUATION_MAX - ATTENUATION_MIN);

        // Set the callback function for when the attenuation seek bar is changed
        attenuationSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        attenuationValueOutput.setText(getFormattedAttenuationValue());
                    }

                    /** Not required */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    /** Not required */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        // Set the default attenuation to the middle value
        attenuationSeekBar.setProgress((ATTENUATION_MIN + ATTENUATION_MAX) / 2);

        // Set up the dropdown spinner of available gain choices
        possibleGainValues = getResources().getIntArray(R.array.gain_values);
        ArrayList<String> displayableValues = new ArrayList<>();
        for (int i : possibleGainValues) {
            displayableValues.add(i + "db");
        }
        ArrayAdapter adapter =
                new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        displayableValues);
        gainSpinner.setAdapter(adapter);
        chosenGainIndex = 0;
        gainSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        chosenGainIndex = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
    }

    public void setAttenuation(int attenuation) {
        attenuationSeekBar.setProgress(attenuation - ATTENUATION_MIN);
    }

    public int getAttenuation() {
        return ATTENUATION_MIN + attenuationSeekBar.getProgress();
    }

    private String getFormattedAttenuationValue() {
        //        return (new DecimalFormat("#.00").format(getAttenuation())) + "dB";
        return Integer.toString(getAttenuation()) + "dB";
    }

    public void setGain(int gain) {
        // TODO: See if this can be done better!
        for (int i = 0; i < possibleGainValues.length; i++) {
            if (possibleGainValues[i] == gain) {
                gainSpinner.setSelection(i);
            }
        }
    }

    public int getGain() {
        return possibleGainValues[chosenGainIndex];
    }
}
