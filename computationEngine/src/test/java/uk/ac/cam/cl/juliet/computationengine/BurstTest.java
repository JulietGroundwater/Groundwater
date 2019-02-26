package uk.ac.cam.cl.juliet.computationengine;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import org.junit.Before;
import org.junit.Test;

public class BurstTest {

    File file, file2, jsonFile;

    @Before
    public void initialise() {
        ClassLoader classLoader = getClass().getClassLoader();
        file =
                new File(
                        classLoader
                                .getResource(
                                        "uk/ac/cam/cl/juliet/computationengine/DATA2018-04-17-1300.DAT")
                                .getFile());

        file2 =
                new File(
                        classLoader
                                .getResource("uk/ac/cam/cl/juliet/computationengine/Config.ini")
                                .getFile());

        jsonFile =
                new File(
                        classLoader
                                .getResource(
                                        "uk/ac/cam/cl/juliet/computationengine/DATA2018-04-17-1300.json")
                                .getFile());
    }

    @Test(expected = InvalidBurstException.class)
    public void burst_throwsException_withNullFile() throws Exception {
        new Burst(null);
    }

    @Test(expected = InvalidBurstException.class)
    public void burst_throwsException_withWrongFileType() throws Exception {
        new Burst(file2);
    }

    @Test
    public void burst_created_withValidFilename() throws Exception {
        Burst burst = new Burst(file);

        assertEquals(burst.getFilename(), file.getName());
    }

    @Test
    public void burst_created_withCorrectParameters() throws Exception {
        Burst burst = new Burst(file);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String burstJson = gson.toJson(burst);

        JsonReader reader = new JsonReader(new FileReader(jsonFile));

        Burst canon = gson.fromJson(reader, Burst.class);

        String canonJson = gson.toJson(canon);

        assertEquals(burstJson, canonJson);
    }
}
