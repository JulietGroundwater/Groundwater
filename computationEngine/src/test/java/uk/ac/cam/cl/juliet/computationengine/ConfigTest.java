package uk.ac.cam.cl.juliet.computationengine;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

    private File file, file2, jsonFile;

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        file =
                new File(
                        classLoader
                                .getResource("uk/ac/cam/cl/juliet/computationengine/Config.ini")
                                .getFile());
        file2 =
                new File(
                        classLoader
                                .getResource(
                                        "uk/ac/cam/cl/juliet/computationengine/DATA2018-04-17-1300.DAT")
                                .getFile());

        jsonFile =
                new File(
                        classLoader
                                .getResource("uk/ac/cam/cl/juliet/computationengine/Config.json")
                                .getFile());
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withNullFile() throws Exception {
        new Config(null);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withWrongFileType() throws Exception {
        new Config(file2);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withNullTriples() throws Exception {
        Config config = new Config(file);
        config.setTriples(null);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withNonThreeMultipleList() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) arr.add(i);
        config.setTriples(arr);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withTooLargeList() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 13; i++) arr.add(i);
        config.setTriples(arr);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withNegativeValueBelow1() throws Exception {
        Config config = new Config(file);
        config.setWatchdogTaskSecs(-2);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withInterChirpDelayBelowOne() throws Exception {
        Config config = new Config(file);
        config.setInterChirpDelay(0);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withSettleCyclesBelowOne() throws Exception {
        Config config = new Config(file);
        config.setSettleCycles(0);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withNSubBurstsBelowZero() throws Exception {
        Config config = new Config(file);
        config.setNSubBursts(-1);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAverageBelowZero() throws Exception {
        Config config = new Config(file);
        config.setAverage(-1);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAverageAboveTwo() throws Exception {
        Config config = new Config(file);
        config.setAverage(3);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withRepSecsBelowOne() throws Exception {
        Config config = new Config(file);
        config.setRepSecs(0);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withMaxDataFileLengthBelowLimit() throws Exception {
        Config config = new Config(file);
        config.setMaxDataFileLength(999999);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAttenuator1Null() throws Exception {
        Config config = new Config(file);
        config.setAttenuator1(null);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAttenuator1ValuesAboveLimit() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) arr.add(i + 31);
        config.setAttenuator1(arr);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAttenuator1ValuesBelowLimit() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) arr.add(i - 3);
        config.setAttenuator1(arr);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAfGainNull() throws Exception {
        Config config = new Config(file);
        config.setAFGain(null);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAfGainValuesAboveLimit() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) arr.add(i + 31);
        config.setAFGain(arr);
    }

    @Test(expected = InvalidConfigException.class)
    public void config_throwsException_withAfGainValuesBelowLimit() throws Exception {
        Config config = new Config(file);
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 5; i++) arr.add(i - 3);
        config.setAFGain(arr);
    }

    @Test
    public void config_created_withCorrectParameters() throws Exception {
        Config config = new Config(file);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String configJson = gson.toJson(config);

        JsonReader reader = new JsonReader(new FileReader(jsonFile));

        Config canon = gson.fromJson(reader, Config.class);

        String canonJson = gson.toJson(canon);

        assertEquals(configJson, canonJson);
    }
}
