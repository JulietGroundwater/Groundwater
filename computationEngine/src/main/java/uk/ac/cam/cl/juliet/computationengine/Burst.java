package uk.ac.cam.cl.juliet.computationengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.math3.complex.Complex;

/**
 * Class to represent a Burst from the radar, this is a java representation of the vdat struct from
 * {@code fmcw_load()}. The only required methods are getters for each of the fields in the struct.
 */
public class Burst {

    private String filename;
    private int subBurstsInBurst;
    private int average;
    private int nAttenuators;
    private int code = 0;
    private int nSamples = 0;
    private int chirpsInBurst = 0;
    private int burst = 0;
    private int fileFormat;
    private double fs;
    private Date dateTime;
    private double temperature1,
            temperature2,
            batteryVoltage,
            samplesPerChirp,
            f0,
            k,
            f1,
            t,
            b,
            fc,
            dt,
            er,
            ci,
            lambdac;
    private List<Double> attenuator1, attenuator2, v, processing, tList, fList;
    private List<Complex> chirpAtt = new ArrayList<>();
    private List<Date> chirpTime = new ArrayList<>();
    private List<Integer> startInd, endInd, TxAnt, RxAnt, chirpNum = new ArrayList<>();
    private List<List<Double>> vif;

    public Burst(String filename, int burstNum) throws InvalidBurstException {
        this.filename = filename;
        loadBurstRMB5(burstNum);

        if (temperature1 > 300) {
            temperature1 -= 512;
        }
        if (temperature2 > 300) {
            temperature2 -= 512;
        }

        if (code != 0) {
            throw new InvalidBurstException("Unable to correctly create burst");
        }

        fileFormat = 5;

        vif = new ArrayList<>();

        List<Complex> attSet =
                new ArrayList<>(); // Complex(attenuator1.get(0), attenuator1.get(0));
        for (int i = 0; i < attenuator1.size(); i++)
            attSet.add(new Complex(attenuator1.get(i), attenuator2.get(i)));

        double chirpInterval = 1.6384; // 1.6384 / (24 * 3600)
        Calendar cal = Calendar.getInstance();
        cal.set(2000, Calendar.JANUARY, 1);
        Date date2000 = cal.getTime();

        for (int chirp = 0; chirp < chirpsInBurst; chirp++) {
            ArrayList<Double> temp =
                    new ArrayList<>(v.subList(startInd.get(chirp), endInd.get(chirp)));
            vif.add(temp);
            chirpNum.add(chirp);
            chirpAtt.add(attSet.get((chirp) % attSet.size())); // TODO check for off by 1 error
            chirpTime.add(new Date(dateTime.getTime() + (long) (chirp * chirpInterval)));
        }

        this.filename = filename;
        samplesPerChirp = nSamples;
        fs = 4e4;
        f0 = 2e8;
        k = 2 * Math.PI * 2e8;
        processing = new ArrayList<>();

        loadParametersRMB2();
    }

    /**
     * Returns the Code parameter
     *
     * @return Code parameter
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns NSamples parameter
     *
     * @return NSamples parameter
     */
    public int getNSamples() {
        return nSamples;
    }

    /**
     * Returns SubBurstsInBurst parameter
     *
     * @return SubBurstsInBurst parameter
     */
    public int getSubBurstsInBurst() {
        return subBurstsInBurst;
    }

    /**
     * Returns Average parameter
     *
     * @return Average parameter
     */
    public int getAverage() {
        return average;
    }

    /**
     * Returns NAttenuators parameter
     *
     * @return NAttenuators parameter
     */
    public int getNAttenuators() {
        return nAttenuators;
    }

    /**
     * Returns Attenuator1 parameter
     *
     * @return Attenuator1 parameter
     */
    public List<Double> getAttenuator1() {
        return new ArrayList<>(attenuator1);
    }

    /**
     * Returns Attenuator2 parameter
     *
     * @return Attenuator2 parameter
     */
    public List<Double> getAttenuator2() {
        return new ArrayList<>(attenuator2);
    }

    /**
     * Returns TxAnt parameter
     *
     * @return TxAnt parameter
     */
    public List<Integer> getTxAnt() {
        return new ArrayList<>(TxAnt);
    }

    /**
     * Returns RxAnt parameter
     *
     * @return RxAnt parameter
     */
    public List<Integer> getRxAnt() {
        return new ArrayList<>(RxAnt);
    }

    /**
     * Returns ChirpsInBurst parameter
     *
     * @return ChirpsInBurst parameter
     */
    public int getChirpsInBurst() {
        return chirpsInBurst;
    }

    /**
     * Returns TimeStamp parameter
     *
     * @return TimeStamp parameter
     */
    public Date getTimeStamp() {
        return dateTime;
    }

    /**
     * Returns Temperature1 parameter
     *
     * @return Temperature1 parameter
     */
    public double getTemperature1() {
        return temperature1;
    }

    /**
     * Returns Temperature2 parameter
     *
     * @return Temperature1 parameter
     */
    public double getTemperature2() {
        return temperature2;
    }

    /**
     * Returns BatteryVoltage parameter
     *
     * @return BatteryVoltage parameter
     */
    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     * Returns V parameter
     *
     * <p>implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return V parameter
     */
    public List<Double> getV() {
        return new ArrayList<>(v);
    }

    /**
     * Returns StartInd parameter
     *
     * <p>implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a *
     * height of 1
     *
     * @return StartInd parameter
     */
    public List<Integer> getStartInd() {
        return new ArrayList<>(startInd);
    }

    /**
     * Returns EndInd parameter
     *
     * <p>implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a *
     * height of 1
     *
     * @return EndInd parameter
     */
    public List<Integer> getEndInd() {
        return new ArrayList<>(endInd);
    }

    /**
     * Returns Burst parameter
     *
     * @return Burst parameter
     */
    public int getBurst() {
        return burst;
    }

    /**
     * Returns FileFormat parameter
     *
     * @return FileFormat parameter
     */
    public int getFileFormat() {
        return fileFormat;
    }

    /**
     * Returns vif parameter
     *
     * <p>Implemented as {@code List<List<Double>>} since in the original MATLAB code it was a
     * 2D-Matrix
     *
     * @return vif parameter
     */
    public List<List<Double>> getVif() {
        List<List<Double>> vifClone = new ArrayList<>();

        for (List<Double> arr : vif) vifClone.add(new ArrayList<>(arr));

        return vifClone;
    }

    /**
     * Returns ChirpNum parameter
     *
     * <p>Implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return ChirpNum parameter
     */
    public List<Integer> getChirpNum() {
        return new ArrayList<>(chirpNum);
    }

    /**
     * Returns ChirpTime parameter
     *
     * <p>Implemented as {@code List<Date>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * <p>Returns a {@code Date} object as their is no simple equivalent for the {@code DateNumber}
     * from MATLAB in java
     *
     * @return ChirpTime parameter
     */
    public List<Date> getChirpTime() {
        return new ArrayList<>(chirpTime);
    }

    /**
     * Returns filename parameter
     *
     * @return filename parameter
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns SamplesPerChirp parameter
     *
     * @return SamplesPerChirp parameter
     */
    public double getSamplesPerChirp() {
        return samplesPerChirp;
    }

    /**
     * Returns fs parameter
     *
     * @return fs parameter
     */
    public double getFs() {
        return fs;
    }

    /**
     * Returns f0 parameter
     *
     * @return f0 parameter
     */
    public double getF0() {
        return f0;
    }

    /**
     * Returns K parameter
     *
     * @return K parameter
     */
    public double getK() {
        return k;
    }

    /**
     * Returns processing parameter
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * <p>In the original MATLAB processing is always set to {@code {}}, so this could be removed
     *
     * @return processing parameter
     */
    public List<Double> getProcessing() {
        return new ArrayList<>(processing);
    }

    /**
     * Returns f1 parameter
     *
     * @return f1 parameter
     */
    public double getF1() {
        return f1;
    }

    /**
     * Returns T parameter.
     *
     * <p>(Name conflict with t in vdat -> t renamed to tList)
     *
     * @return T parameter
     */
    public double getT() {
        return t;
    }

    /**
     * Returns B parameter
     *
     * @return B parameter
     */
    public double getB() {
        return b;
    }

    /**
     * Returns fc parameter
     *
     * @return fc parameter
     */
    public double getFc() {
        return fc;
    }

    /**
     * Returns dt parameter
     *
     * @return dt parameter
     */
    public double getDt() {
        return dt;
    }

    /**
     * Returns er parameter
     *
     * @return er parameter
     */
    public double getEr() {
        return er;
    }

    /**
     * Returns ci parameter
     *
     * @return ci parameter
     */
    public double getCi() {
        return ci;
    }

    /**
     * Returns lambdac parameter
     *
     * @return lambdac parameter
     */
    public double getLambdac() {
        return lambdac;
    }

    /**
     * Returns tList parameter (originally called t in vdat but changed due to name conflict with T
     * parameter).
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1.
     *
     * @return t parameter
     */
    public List<Double> getTList() {
        return new ArrayList<>(tList);
    }

    /**
     * Returns f parameter.
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1.
     *
     * @return f parameter
     */
    public List<Double> getFList() {
        return new ArrayList<>(fList);
    }

    /**
     * Returns ChirpAtt parameter.
     *
     * <p>Implemented as {code List<Complex>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return chirpAtt parameter
     */
    public List<Complex> getChirpAtt() {
        return new ArrayList<>(chirpAtt);
    }

    private void loadBurstRMB5(int totalNumberOfBursts) throws InvalidBurstException {
        int MaxHeaderLen = 1500;
        int burstpointer = 0;
        code = 0;

        long fileLength;
        // file = new File(filename);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        fileLength = file.length();

        try (FileInputStream f = new FileInputStream(file)) {
            FileChannel fc = f.getChannel();
            int burstCount = 1;
            long current_stream_pos = 0;
            int wordsPerBurst = 0;
            int WperChirpCycle;

            fc.position(burstpointer);
            byte headerBytes[] = new byte[MaxHeaderLen];
            while (current_stream_pos != MaxHeaderLen) {
                current_stream_pos +=
                        f.read(
                                headerBytes,
                                (int) current_stream_pos,
                                (int) (MaxHeaderLen - current_stream_pos));
            }
            String A = new String(headerBytes);

            nSamples = parseInt(A, "N_ADC_SAMPLES=");
            WperChirpCycle = nSamples;
            subBurstsInBurst = parseInt(A, "NSubBursts=");

            int[] searchind = strFind(A, "Average=");
            if (searchind.length == 0) {
                average = 0;
            } else {
                average = parseInt(A, "Average=");
            }

            nAttenuators = parseInt(A, "nAttenuators=");
            attenuator1 = parseDoubleArray(A, "Attenuator1=");
            attenuator2 = parseDoubleArray(A, "AFGain=");
            TxAnt = parseIntArray(A, "TxAnt=");
            RxAnt = parseIntArray(A, "RxAnt=");

            if (TxAnt.size() != 8) {
                throw new InvalidBurstException("TxAnt has wrong number of values");
            }

            if (RxAnt.size() != 8) {
                throw new InvalidBurstException("RxAnt has wrong number of values");
            }

            while (TxAnt.contains(0)) {
                TxAnt.remove((Integer) 0);
            }

            while (RxAnt.contains(0)) {
                RxAnt.remove((Integer) 0);
            }

            if (average != 0) {
                chirpsInBurst = 1;
            } else {
                chirpsInBurst = subBurstsInBurst * TxAnt.size() * RxAnt.size() * nAttenuators;
            }

            String searchString = "*** End Header ***";
            searchind = strFind(A, searchString);

            burstpointer += searchind[0] + searchString.length();

            // Extract remaining information from header
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            ft.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateTime = ft.parse(parseString(A, "Time stamp="));

            temperature1 = parseDouble(A, "Temp1=");
            temperature2 = parseDouble(A, "Temp2=");
            batteryVoltage = parseDouble(A, "BatteryVoltage=");

            while ((burstCount <= totalNumberOfBursts)
                    && (burstpointer <= fileLength - MaxHeaderLen)) {
                wordsPerBurst = chirpsInBurst * WperChirpCycle;
                if (burstCount < totalNumberOfBursts && burstpointer <= fileLength - MaxHeaderLen) {
                    if (average != 0) {
                        burstpointer += (chirpsInBurst * WperChirpCycle * 4);
                    } else {
                        burstpointer += (chirpsInBurst * WperChirpCycle * 2);
                    }
                }
                burstCount++;
            }

            if (burstpointer != 0) {
                fc.position(burstpointer - 1);
            }

            if (burstCount == totalNumberOfBursts + 1) {
                int count = 0;
                if (average == 2) {
                    int readTotal = wordsPerBurst * 4;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        int x =
                                b[4 * i]
                                        + (256 * b[4 * i + 1])
                                        + (256 * 256 * b[4 * i + 2])
                                        + (256 * 256 * 256 * b[4 * i + 3]);
                        v.add((double) x);
                    }
                } else if (average == 1) {
                    fc.position(burstpointer + 1);

                    int readTotal = wordsPerBurst * 4;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        float x =
                                ByteBuffer.wrap(b, 4 * i, 4)
                                        .order(ByteOrder.LITTLE_ENDIAN)
                                        .getFloat();
                        v.add((double) x);
                    }
                } else {
                    int readTotal = wordsPerBurst * 2;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        int x = (b[2 * i + 1] & 0xFF) | ((b[2 * i] & 0xFF) << 8);
                        v.add((double) x);
                    }
                }

                for (int i = 0; i < v.size(); i++) {
                    if (v.get(i) < 0) {
                        v.set(i, v.get(i) + Math.pow(2, 16));
                    }
                    v.set(i, v.get(i) * 2.5 / Math.pow(2, 16));
                }

                if (average == 2) {
                    for (int i = 0; i < v.size(); i++) {
                        v.set(i, v.get(i) / (subBurstsInBurst * nAttenuators));
                    }
                }

                startInd = new ArrayList<>();
                endInd = new ArrayList<>();
                for (int i = 1; i <= WperChirpCycle * chirpsInBurst; i += WperChirpCycle) {
                    startInd.add(i);
                    endInd.add(i + WperChirpCycle - 1);
                }

                burst = totalNumberOfBursts;
            } else {
                // Too few bursts in file
                burst = burstCount - 1;
                code = -4;
            }
        } catch (InvalidBurstException e) {
            throw e;
        } catch (Exception e) {
            if (code == 0) code = 1;
            throw new InvalidBurstException(String.format("Failed to parse file, code %d", code));
        }
    }

    private int[] strFind(String original, String substring) {
        ArrayList<Integer> list = new ArrayList<>();
        int lastIndex = 0;
        while (lastIndex != -1) {

            lastIndex = original.indexOf(substring, lastIndex);

            if (lastIndex != -1) {
                list.add(lastIndex);
                lastIndex += 1;
            }
        }

        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }

        return ret;
    }

    private String parseString(String mainString, String searchString) {
        int[] searchInd = strFind(mainString, searchString);
        int[] searchCR = strFind(mainString.substring(searchInd[0]), "\r\n");
        return mainString.substring(
                searchInd[0] + searchString.length(), searchCR[0] + searchInd[0]);
    }

    private int parseInt(String mainString, String searchString) {
        return Integer.parseInt(parseString(mainString, searchString));
    }

    private double parseDouble(String mainString, String searchString) {
        return Double.parseDouble(parseString(mainString, searchString));
    }

    private List<Integer> parseIntArray(String mainString, String searchString) {
        ArrayList<Integer> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Integer.parseInt(s));
        }
        return ret;
    }

    private List<Double> parseDoubleArray(String mainString, String searchString) {
        ArrayList<Double> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Double.parseDouble(s));
        }
        return ret;
    }

    private void loadParametersRMB2() throws InvalidBurstException {
        try {
            Header header = new Header(filename);

            // Read from Reg01
            // noDwellHigh at bit 18
            boolean noDwellHigh =
                    ((Long.decode("0x" + header.getReg01().replace("\"", "")) >> 18) & 1) == 1;
            // noDwellLow at bit 17
            boolean noDwellLow =
                    ((Long.decode("0x" + header.getReg01().replace("\"", "")) >> 17) & 1) == 1;

            // Read from Reg0B
            double fsysclk = 1e9;
            double startFreq =
                    Long.decode("0x" + header.getReg0B().substring(9, 17))
                            * fsysclk
                            / Math.pow(2, 32);
            double stopFreq =
                    Long.decode("0x" + header.getReg0B().substring(1, 9))
                            * fsysclk
                            / Math.pow(2, 32);

            // Read from Reg0C
            double rampUpStep =
                    Long.decode("0x" + header.getReg0C().substring(9, 17))
                            * fsysclk
                            / Math.pow(2, 32);
            double rampDnStep =
                    Long.decode("0x" + header.getReg0C().substring(1, 9))
                            * fsysclk
                            / Math.pow(2, 32);

            // Read from Reg0D
            double tStepUp = Long.decode("0x" + header.getReg0D().substring(5, 9)) * 4 / fsysclk;
            double tStepDn = Long.decode("0x" + header.getReg0D().substring(1, 5)) * 4 / fsysclk;

            int nChirpSamples = header.getNADCSamples();
            long nStepsDDS = Math.round(Math.abs((stopFreq - startFreq) / rampUpStep));
            double chirpLength = nStepsDDS * tStepUp;

            if (chirpLength * fs > nChirpSamples) chirpLength = nChirpSamples / fs;

            double hk = 2 * Math.PI * (rampDnStep / tStepUp);
            String rampDir = (stopFreq > 400e6) ? "down" : "up";
            if (noDwellHigh && noDwellLow) {
                rampDir = "upDown";
                double nChirpsPerPeriod = Double.NaN; // blah
            }

            k = hk;
            f0 = startFreq;
            f1 = startFreq + chirpLength * hk / 2 / Math.PI;
            samplesPerChirp = chirpLength * fs;
            t = chirpLength;
            b = chirpLength * hk / 2 / Math.PI;
            fc = startFreq + b / 2;
            dt = 1 / fs;
            er = 3.18;
            ci = 3e8 / Math.sqrt(er);
            lambdac = ci / fc;
            for (int i = 0; i < vif.size(); i++) {
                vif.set(i, vif.get(i).subList(0, (int) samplesPerChirp));
            }
            nSamples = nChirpSamples;

            tList = new ArrayList<>();
            fList = new ArrayList<>();

            for (int i = 0; i < samplesPerChirp; i++) {
                double tempT = dt * i;
                tList.add(tempT);
                fList.add(f0 + tempT * (k / (2 * Math.PI)));
            }

        } catch (IOException e) {
            throw new InvalidBurstException(
                    "Caught: " + e.getClass() + " with message: " + e.getMessage());
        }
    }
}
