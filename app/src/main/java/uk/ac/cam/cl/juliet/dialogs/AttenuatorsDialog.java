package uk.ac.cam.cl.juliet.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.data.AttenuatorSettings;
import uk.ac.cam.cl.juliet.views.AttenuatorConfigurationView;

/** A fullscreen dialog for inputting an arbitrary number of (attenuator, gain) pairs. */
public class AttenuatorsDialog extends DialogFragment implements View.OnClickListener {

    /**
     * Sets the maximum number of attenuators that can be added. Once they have all been added, the
     * "add" button will be disabled until one is removed again.
     */
    public static int MAX_ATTENUATORS = 4;

    /**
     * Used to pass in the currently selected attenuator settings so that the user's previous
     * choices are restored if they click "configure attenuators" again.
     */
    public static final String ATTENUATOR_SETTINGS = "attenuator_settings";

    private Button closeButton;
    private Button doneButton;
    private Button addAttenuatorButton;
    private Button removeAttenuatorButton;
    private LinearLayout attenuatorsContainer;
    private List<AttenuatorConfigurationView> attenuatorsList;
    private OnAttenuatorsSelectedListener listener;

    public void setOnAttenuatorsSelectedListener(OnAttenuatorsSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_attenuators_fullscreen, container, false);
        closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
        doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);
        addAttenuatorButton = view.findViewById(R.id.addAttenuatorButton);
        addAttenuatorButton.setOnClickListener(this);
        removeAttenuatorButton = view.findViewById(R.id.removeAttenuatorButton);
        removeAttenuatorButton.setOnClickListener(this);
        attenuatorsContainer = view.findViewById(R.id.attenuatorsContainer);
        attenuatorsList = generateAttenuatorUIs();
        for (View v : attenuatorsList) {
            attenuatorsContainer.addView(v);
        }
        updateAddRemoveAttenuatorsButtonsEnabled();
        return view;
    }

    /**
     * Builds a list of <code>AttenuatorConfigurationView</code> objects from the list of attenuator
     * and gain values passed to this fragment.
     *
     * <p>If invalid or no arguments were passed, then a single new view will be initialised
     * instead.
     *
     * @return A list of Views, one for each attenuator/gain setting pair.
     */
    private List<AttenuatorConfigurationView> generateAttenuatorUIs() {
        List<AttenuatorConfigurationView> result = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(ATTENUATOR_SETTINGS)) {
            return generateSingleAttenuator();
        }
        Object attenuatorSettingsObj = arguments.get(ATTENUATOR_SETTINGS);
        if (!(attenuatorSettingsObj instanceof AttenuatorSettings)) {
            return generateSingleAttenuator();
        }
        AttenuatorSettings settings = (AttenuatorSettings) attenuatorSettingsObj;
        List<Integer> attenuatorValues = settings.getAttenuatorValues();
        List<Integer> gainValues = settings.getGainValues();
        if (attenuatorValues == null
                || attenuatorValues.isEmpty()
                || gainValues == null
                || gainValues.isEmpty()) {
            return generateSingleAttenuator();
        }
        for (int i = 0; i < Math.min(attenuatorValues.size(), gainValues.size()); i++) {
            AttenuatorConfigurationView view = new AttenuatorConfigurationView(getContext());
            view.setAttenuation(attenuatorValues.get(i));
            view.setGain(gainValues.get(i));
            result.add(view);
        }
        return result;
    }

    /**
     * Generates a single <code>View</code> for one attenuator/gain value pair.
     *
     * @return A list containing a single <code>AttenuatorConfigurationView</code>
     */
    private List<AttenuatorConfigurationView> generateSingleAttenuator() {
        List<AttenuatorConfigurationView> result = new ArrayList<>();
        result.add(new AttenuatorConfigurationView(getContext()));
        return result;
    }

    /**
     * Updates the enabled status of the "add" and "remove" buttons.
     *
     * <p>If there is only one attenuator remaining then the "remove" button will be disabled;
     * otherwise it will be enabled. The "add" button will be enabled until <code>MAX_ATTENUATORS
     * </code> is reached, at which point it will be disabled until an attenuator is removed.
     */
    private void updateAddRemoveAttenuatorsButtonsEnabled() {
        removeAttenuatorButton.setEnabled(attenuatorsList.size() > 1);
        addAttenuatorButton.setEnabled(attenuatorsList.size() < MAX_ATTENUATORS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                showConfirmDiscardChangesDialog();
                break;
            case R.id.doneButton:
                onDone();
                break;
            case R.id.addAttenuatorButton:
                addAttenuator();
                break;
            case R.id.removeAttenuatorButton:
                removeAttenuator();
        }
    }

    /**
     * Displays a dialog asking the user whether they wish to discard or keep their changes.
     *
     * <p>If the user chooses "keep" then nothing will happen. If the user chooses "discard" then
     * the <code>AttenuatorsDialog</code> will close and the user will be returned to the <code>
     * SettingsFragment</code> with no changes applied.
     */
    private void showConfirmDiscardChangesDialog() {
        Context context = getContext();
        if (context == null) return;
        new AlertDialog.Builder(context)
                .setTitle(R.string.discard_changes)
                .setMessage(R.string.are_you_sure_discard_changes)
                .setPositiveButton(
                        R.string.discard,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dismiss();
                            }
                        })
                .setNegativeButton(
                        R.string.keep,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    /**
     * Passes the selected attenuator and gain values to the callback, then dismisses the dialog.
     */
    private void onDone() {
        if (listener == null) return;
        List<Integer> attenuators = new ArrayList<>();
        List<Integer> gains = new ArrayList<>();
        for (AttenuatorConfigurationView v : attenuatorsList) {
            attenuators.add(v.getAttenuation());
            gains.add(v.getGain());
        }
        listener.onAttenuatorsSelected(attenuators, gains);
        dismiss();
    }

    /**
     * Adds another attenuator to the UI.
     *
     * <p>If this action causes there to be more than 1 attenuator, the "remove attenuator" button
     * will now be re-enabled.
     */
    private void addAttenuator() {

        // Create another attenuator
        AttenuatorConfigurationView view = new AttenuatorConfigurationView(getContext());
        attenuatorsList.add(view);
        attenuatorsContainer.addView(view);

        // If needed, re-enable the "remove attenuator" button
        updateAddRemoveAttenuatorsButtonsEnabled();
    }

    /**
     * Removes the bottom attenuator from the UI.
     *
     * <p>If this leaves only one attenuator remaining, then the "remove attenuator" button will be
     * disabled: this prevents creating a configuration with no attenuator values.
     */
    private void removeAttenuator() {

        // Prevent having fewer than one attenuator value
        if (attenuatorsList.size() <= 1) return;

        // Remove the most recent view
        attenuatorsList.remove(attenuatorsList.size() - 1);
        attenuatorsContainer.removeViewAt(attenuatorsList.size());

        // If needed, disable the "remove attenuator" button
        updateAddRemoveAttenuatorsButtonsEnabled();
    }

    /** Ensures that a listener has an <code>onAttenuatorsSelected</code> callback. */
    public interface OnAttenuatorsSelectedListener {
        void onAttenuatorsSelected(List<Integer> attenuators, List<Integer> gains);
    }
}
