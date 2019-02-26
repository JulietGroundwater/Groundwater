package uk.ac.cam.cl.juliet.computationengine;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import org.junit.Before;
import org.junit.Test;

public class HeaderTest {

    private File file, file2, jsonFile;

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
                                        "uk/ac/cam/cl/juliet/computationengine/DATA2018-04-17-1300-HEADER.json")
                                .getFile());
    }

    @Test(expected = ComputationEngineException.class)
    public void header_throwsException_withNullFile() throws Exception {
        new Header(null);
    }

    @Test(expected = ComputationEngineException.class)
    public void burst_throwsException_withWrongFileType() throws Exception {
        new Header(file2);
    }

    @Test
    public void header_created_withCorrectParameters() throws Exception {
        Header header = new Header(file);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String headerJson = gson.toJson(header);

        JsonReader reader = new JsonReader(new FileReader(jsonFile));

        Header canon = gson.fromJson(reader, Header.class);

        String canonJson = gson.toJson(canon);

        assertEquals(headerJson, canonJson);
    }
}
