package uk.ac.cam.cl.juliet.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encodes a pair of <code>List<Integer></code> values for the attenuator and gain values.
 *
 * <p>Can be serialized for passing between fragments.
 */
public class AttenuatorSettings implements Serializable {

    private List<Integer> attenuatorValues;
    private List<Integer> gainValues;

    public AttenuatorSettings(List<Integer> attenuatorValues, List<Integer> gainValues) {
        this.attenuatorValues = attenuatorValues;
        this.gainValues = gainValues;
    }

    public void setAttenuatorValues(List<Integer> attenuatorValues) {
        this.attenuatorValues = attenuatorValues;
    }

    public List<Integer> getAttenuatorValues() {
        return attenuatorValues;
    }

    public void setGainValues(List<Integer> gainValues) {
        this.gainValues = gainValues;
    }

    public List<Integer> getGainValues() {
        return gainValues;
    }

    private List<String> appendDecabelSuffix(List<Integer> input) {
        List<String> result = new ArrayList<>();
        for (int i : input) {
            result.add(i + "dB");
        }
        return result;
    }

    public List<String> getDisplayableAttenuatorValues() {
        return appendDecabelSuffix(getAttenuatorValues());
    }

    public List<String> getDisplayableGainValues() {
        return appendDecabelSuffix(getGainValues());
    }
}
