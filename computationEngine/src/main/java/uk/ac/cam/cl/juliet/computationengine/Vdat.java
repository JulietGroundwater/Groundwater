package uk.ac.cam.cl.juliet.computationengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import jdk.nashorn.internal.parser.DateParser;

public class Vdat {
    public int subBurstsInBurst;
    public int average;
    public int nAttenuators;
    public int code = 0;
    public int nSamples = 0;
    public int chirpsInBurst = 0;
    public int burst = 0;
    public LocalDateTime dateTime;
    public double temperature1;
    public double temperature2;
    public double batteryVoltage;
    public double[] attenuator1;
    public double[] attenuator2;
    public double[] v;
    public int[] startInd;
    public int[] endInd;
    public int[] TxAnt;
    public int[] RxAnt;

    public Vdat(String FileName, int burst, int SamplesPerChirp) {
        /*
           function vdat = LoadBurstRMB3(Filename, Burst, SamplesPerChirp)

           % vdat = LoadBurstRMB3(Filename, Burst, SamplesPerChirp)
           %
           % Read FMCW data file from RMB-A? (Data from Jan 2013)

        */

        try {
            LoadBurstRMB5(FileName, burst, SamplesPerChirp);
        } catch (IOException e) {
            code = -1;
        }

        /*
            % Clean temperature record (wrong data type?)
            bti1 = find(vdat.Temperature_1>300); % bad temperature indices
            if ~isempty(bti1)
                %disp('Cleaning temperature over 300C')
            vdat.Temperature_1(bti1) = vdat.Temperature_1(bti1)-512;
            end
                    bti2 = find(vdat.Temperature_2>300); % bad temperature indices
            vdat.Temperature_2(bti2) = vdat.Temperature_2(bti2)-512;
        */
        if (temperature1 > 300) {
            temperature1 -= 512;
        }
        if (temperature2 > 300) {
            temperature2 -= 512;
        }
    }

    private int[] strFind(String original, String substring) {
        ArrayList<Integer> list = new ArrayList<>();
        int lastIndex = 0;
        while (lastIndex != -1) {

            lastIndex = original.indexOf("Samples:", lastIndex);

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

    private void LoadBurstRMB3(String FileName, int totalNumberOfBursts, int SamplesPerChirp)
            throws IOException {
        /*
        WperChirpHdr = 0;
        MaxHeaderLen = 1024;
        burstpointer = 0;
         */
        int WperChirpHdr = 0;
        int MaxHeaderLen = 1024;
        int burstpointer = 0;
        long fileLength;

        InputStream f;
        /*
        fid = fopen(Filename,'r');
        fseek(fid,0,'eof');
        filelength = ftell(fid);
        if fid >= 0
        else
        % Unknown file
            vdat.Code = -1;
        end

         */
        try {
            File file = new File(FileName);
            fileLength = file.length();
            f = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            code = -1;
            return;
        }

        /*
            BurstCount = 1;
        */
        int burstCount = 1;
        /*
            while BurstCount <= Burst && burstpointer <= filelength - MaxHeaderLen
        */
        long current_stream_pos = 0;
        String A = "";
        int wordsPerBurst = 0;
        int WperChirpCycle = 0;

        while ((burstCount <= totalNumberOfBursts) && (burstpointer <= fileLength - MaxHeaderLen)) {
            /*
                fseek(fid,burstpointer,'bof');
            */
            if (burstpointer < current_stream_pos) {
                f.reset();
                current_stream_pos = 0;
            }
            current_stream_pos += f.skip(burstpointer - current_stream_pos);

            /*
                A = fread(fid,MaxHeaderLen,'*char');
                A = A'; // Complex Conjugate Transpose. For non-numerical data it's just a normal transpose.
                // The combination of the 2 creates a row vector (array) or length MaxHeaderLen where
                // each byte is an ascii char.
                // This is then used as a string.
            */

            byte b[] = new byte[MaxHeaderLen];
            f.read(b);
            char c[] = new char[MaxHeaderLen];
            for (int i = 0; i < MaxHeaderLen; i++) {
                c[i] = (char) b[i];
            }
            A = new String(c);

            /*
               searchind = strfind(A, 'Samples:');
               // This returns an array of all of the matches.
            */
            int[] searchind = strFind(A, "Samples:");

            /*
                if ~isempty(searchind)
            */
            if (searchind.length != 0) {
                /*
                   try
                */
                try {
                    /*
                        searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                        vdat.Nsamples = sscanf(A(searchind(1)+8:searchCR(1)+searchind(1)),'%d');
                        WperChirpCycle = vdat.Nsamples + WperChirpHdr;
                        searchind = strfind(A, 'Chirps in burst:');
                        searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                        vdat.ChirpsInBurst = sscanf(A(searchind(1)+16:searchCR(1)+searchind(1)),'%d');

                        searchind = strfind(A, '*** End Header ***');

                        burstpointer = burstpointer + searchind(1) + 20;
                    */
                    int[] searchCR = strFind(A.substring(searchind[0]), "\13\10");
                    nSamples =
                            Integer.getInteger(
                                    A.substring(searchind[0] + 8, searchCR[0] + searchind[0]));
                    WperChirpCycle = nSamples + WperChirpHdr;
                    searchind = strFind(A, "Chirps in burst:");
                    searchCR = strFind(A.substring(searchind[0]), "\13\10");
                    chirpsInBurst =
                            Integer.getInteger(
                                    A.substring(searchind[0] + 16, searchCR[0] + searchind[0]));

                    searchind = strFind(A, "*** End Header ***");
                    burstpointer += searchind[0] + 20;

                } catch (Exception e) {
                    /*
                    catch
                        vdat.Code = -2;
                        vdat.Burst = BurstCount;
                        return
                    */
                    code = -2;
                    burst = burstCount;
                }
            }
            /*
                    WordsPerBurst = vdat.ChirpsInBurst * WperChirpCycle;
                    if BurstCount < Burst && burstpointer <= filelength - MaxHeaderLen
                        burstpointer = burstpointer + vdat.ChirpsInBurst * WperChirpCycle*2;
                    end
                    BurstCount = BurstCount + 1;
                end
            */
            wordsPerBurst = chirpsInBurst * WperChirpCycle;
            if (burstCount < totalNumberOfBursts && burstpointer <= fileLength - MaxHeaderLen) {
                burstpointer += (chirpsInBurst * WperChirpCycle * 2);
            }
            burstCount++;
        }

        /*
            % Extract remaining information from header
            searchind = strfind(A, 'Time stamp:');
            searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
            try
                td = sscanf(A(searchind(1)+11:searchCR(1)+searchind(1)),...
                    '%d-%d-%d %d:%d:%d');
                vdat.TimeStamp = datenum(td(1),td(2),td(3),td(4),td(5),td(6));
            catch err
                vdat.Code = 1;
            end
        */
        int[] searchind = strFind(A, "Time stamp:");
        int[] searchCR = strFind(A, "\13\10");

        try {

            DateParser dp =
                    new DateParser(A.substring(searchind[0] + 11, searchCR[0] + searchind[0]));
            Integer[] dt = dp.getDateFields();
            dateTime = LocalDateTime.of(dt[0], dt[1], dt[2], dt[3], dt[4], dt[5]);

        } catch (Exception e) {
            code = 1;
        }

        /*
            searchind = strfind(A, 'Temperature 1:');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Temperature_1 = sscanf(A(searchind(1)+14:searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchind = strFind(A, "Temperature 1:");
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            temperature1 =
                    Double.parseDouble(A.substring(searchind[0] + 14, searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            searchind = strfind(A, 'Temperature 2:');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Temperature_2 = sscanf(A(searchind(1)+14:searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchind = strFind(A, "Temperature 2:");
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            temperature2 =
                    Double.parseDouble(A.substring(searchind[0] + 14, searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            searchind = strfind(A, 'Battery voltage:');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.BatteryVoltage = sscanf(A(searchind(1)+16:searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchind = strFind(A, "Battery voltage:");
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            batteryVoltage =
                    Double.parseDouble(A.substring(searchind[0] + 14, searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            searchind = strfind(A, 'Attenuator 1:');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Attenuator_1 = sscanf(A(searchind(1)+13:searchCR(1)+searchind(1)),'%f',4);
            catch err
                vdat.Code = 1;
            end

        */
        searchind = strFind(A, "Attenuator 1:");
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            String[] a_split =
                    A.substring(searchind[0] + 14, searchCR[0] + searchind[0]).split(",");
            attenuator1 = new double[a_split.length];
            for (int i = 0; i < a_split.length; i++) {
                attenuator1[i] = Double.parseDouble(a_split[i]);
            }

        } catch (Exception e) {
            code = 1;
        }

        /*   searchind = strfind(A, 'Attenuator 2:');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Attenuator_2 = sscanf(A(searchind(1)+13:searchCR(1)+searchind(1)),'%f',4);
            catch err
                vdat.Code = 1;
            end
        */
        searchind = strFind(A, "Attenuator 2:");
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            String[] a_split =
                    A.substring(searchind[0] + 14, searchCR[0] + searchind[0]).split(",");
            attenuator2 = new double[a_split.length];
            for (int i = 0; i < a_split.length; i++) {
                attenuator2[i] = Double.parseDouble(a_split[i]);
            }

        } catch (Exception e) {
            code = 1;
        }

        /*
            fseek(fid,burstpointer-1,'bof');
        */
        if (burstpointer - 1 < current_stream_pos) {
            f.reset();
            current_stream_pos = 0;
        }
        current_stream_pos += f.skip(burstpointer - 1 - current_stream_pos);

        /*
           if BurstCount == Burst+1
        */
        if (burstCount == totalNumberOfBursts + 1) {
            /*
                [vdat.v count] = fread(fid,WordsPerBurst,'*int16','ieee-le');
                vdat.v = double(vdat.v);
                vdat.v = vdat.v * 2.5 / 2^16 + 1.25;

            */
            byte b[] = new byte[wordsPerBurst * 2];
            int count = f.read(b);
            count /= 2; // Measures bytes not int16s;
            v = new double[count];

            for (int i = 0; i < count; i++) {
                // Little Endian.
                int x = b[2 * i] + (256 * b[2 * i + 1]);
                double d = x;
                v[i] = d * (2.5 / Math.pow(2, 16)) + 1.25;
            }

            /*
                if count < WordsPerBurst
                    vdat.Code = 2;
                end
            */
            if (count < wordsPerBurst) {
                code = 2;
            }

            /*
                vdat.Startind = ((WperChirpHdr+1):WperChirpCycle:WperChirpCycle*vdat.ChirpsInBurst)';
                vdat.Endind = vdat.Startind + SamplesPerChirp - 1;
            */
            ArrayList<Integer> startIndList = new ArrayList<>();
            for (int i = WperChirpHdr + 1;
                    i <= WperChirpCycle * chirpsInBurst;
                    i += WperChirpCycle) {
                startIndList.add(i);
            }
            startInd = new int[startIndList.size()];
            for (int i = 0; i < startIndList.size(); i++) {
                startInd[i] = startIndList.get(i);
                endInd[i] = startInd[i] + SamplesPerChirp - 1;
            }

            /*
                vdat.Burst = Burst;
            */
            burst = totalNumberOfBursts;
        } else {
            /*
            % Too few bursts in file
                vdat.Burst = BurstCount - 1;
                vdat.Code = -4;

             */
            burst = burstCount - 1;
            code = -4;
        }

        /*
            fclose(fid);
        */
        f.close();
    }

    private void LoadBurstRMB5(String FileName, int totalNumberOfBursts, int SamplingFrequency)
            throws IOException {
        /*
            MaxHeaderLen = 1500;
            burstpointer = 0;
            vdat.Code = 0;
        */
        int MaxHeaderLen = 1500;
        int burstpointer = 0;
        code = 0;

        InputStream f;
        /*
            fid = fopen(Filename,'r');
            if fid >= 0
                fseek(fid,0,'eof');
                filelength = ftell(fid);
            else
            % Unknown file
                vdat.Code = -1;
            end

        */
        long fileLength;
        try {
            File file = new File(FileName);
            fileLength = file.length();
            f = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            code = -1;
            return;
        }

        /*
            BurstCount = 1;
        */
        int burstCount = 1;
        /*
            while BurstCount <= Burst && burstpointer <= filelength - MaxHeaderLen
        */
        long current_stream_pos = 0;
        String A = "";
        int wordsPerBurst = 0;
        int WperChirpCycle = 0;

        while ((burstCount <= totalNumberOfBursts) && (burstpointer <= fileLength - MaxHeaderLen)) {
            /*
                fseek(fid,burstpointer,'bof');
            */
            if (burstpointer < current_stream_pos) {
                f.reset();
                current_stream_pos = 0;
            }
            current_stream_pos += f.skip(burstpointer - current_stream_pos);

            /*
                A = fread(fid,MaxHeaderLen,'*char');
                A = A'; // Complex Conjugate Transpose. For non-numerical data it's just a normal transpose.
                // The combination of the 2 creates a row vector (array) or length MaxHeaderLen where
                // each byte is an ascii char.
                // This is then used as a string.
            */

            byte b[] = new byte[MaxHeaderLen];
            f.read(b);
            char c[] = new char[MaxHeaderLen];
            for (int i = 0; i < MaxHeaderLen; i++) {
                c[i] = (char) b[i];
            }
            A = new String(c);

            /*
               SearchString = 'N_ADC_SAMPLES=';
                searchind = strfind(A,SearchString);
            */
            String searchString = "N_ADC_SAMPLES=";
            int[] searchind = strFind(A, searchString);

            /*
                if ~isempty(searchind)
            */
            if (searchind.length != 0) {
                /*
                   try
                */
                try {
                    /*
                        searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                        vdat.Nsamples = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d');
                        WperChirpCycle = vdat.Nsamples;
                        SearchString = 'NSubBursts=';
                        searchind = strfind(A,SearchString);
                        searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                        vdat.SubBurstsInBurst = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d');
                    */
                    int[] searchCR = strFind(A.substring(searchind[0]), "\13\10");
                    nSamples =
                            Integer.getInteger(
                                    A.substring(
                                            searchind[0] + searchString.length(),
                                            searchCR[0] + searchind[0]));
                    WperChirpCycle = nSamples;
                    searchString = "NSubBursts=";
                    searchind = strFind(A, searchString);
                    searchCR = strFind(A.substring(searchind[0]), "\13\10");
                    subBurstsInBurst =
                            Integer.getInteger(
                                    A.substring(
                                            searchind[0] + searchString.length(),
                                            searchCR[0] + searchind[0]));

                    /*
                       SearchString = 'Average=';
                       searchind = strfind(A, SearchString);
                       if isempty(searchind)
                           vdat.Average = 0; %cls 9/jan/14 -average not included in mooring deploy
                       else
                           searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                           vdat.Average = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d');
                       end
                    */
                    searchString = "Average=";
                    searchind = strFind(A, searchString);
                    if (searchind.length == 0) average = 0;
                    else {
                        searchCR = strFind(A.substring(searchind[0]), "\13\10");
                        average =
                                Integer.getInteger(
                                        A.substring(
                                                searchind[0] + searchString.length(),
                                                searchCR[0] + searchind[0]));
                    }

                    /*
                       SearchString = 'nAttenuators=';
                       searchind = strfind(A, SearchString);
                       searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                       vdat.NAttenuators = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d',1);
                    */
                    searchString = "nAttenuators=";
                    searchind = strFind(A, searchString);
                    searchCR = strFind(A.substring(searchind[0]), "\13\10");
                    nAttenuators =
                            Integer.getInteger(
                                    A.substring(
                                            searchind[0] + searchString.length(),
                                            searchCR[0] + searchind[0]));

                    /*
                        SearchString = 'Attenuator1=';
                        searchind = strfind(A, SearchString);
                        searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                        vdat.Attenuator_1 = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%f',vdat.NAttenuators);
                    */
                    searchString = "Attenuator1=";
                    searchind = strFind(A, searchString);
                    try {
                        String[] a_split =
                                A.substring(
                                                searchind[0] + searchString.length(),
                                                searchCR[0] + searchind[0])
                                        .split(",");
                        attenuator1 = new double[a_split.length];
                        for (int i = 0; i < a_split.length; i++) {
                            attenuator1[i] = Double.parseDouble(a_split[i]);
                        }
                    } catch (Exception e) {
                        code = 1;
                    }

                    /*
                       SearchString = 'AFGain=';
                       searchind = strfind(A, SearchString);
                       searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                       vdat.Attenuator_2 = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%f',vdat.NAttenuators);
                    */
                    searchString = "AFGain=";
                    searchind = strFind(A, searchString);
                    try {
                        String[] a_split =
                                A.substring(
                                                searchind[0] + searchString.length(),
                                                searchCR[0] + searchind[0])
                                        .split(",");
                        attenuator2 = new double[a_split.length];
                        for (int i = 0; i < a_split.length; i++) {
                            attenuator2[i] = Double.parseDouble(a_split[i]);
                        }
                    } catch (Exception e) {
                        code = 1;
                    }

                    /*
                       SearchString = 'TxAnt=';
                       searchind = strfind(A, SearchString);
                       searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                       vdat.TxAnt = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d',8);
                    */
                    searchString = "TxAnt=";
                    searchind = strFind(A, searchString);
                    try {
                        String[] a_split =
                                A.substring(
                                                searchind[0] + searchString.length(),
                                                searchCR[0] + searchind[0])
                                        .split(",");
                        if (a_split.length != 8) {
                            throw new AssertionError("TxAnt has wrong number of values");
                        }
                        TxAnt = new int[8];
                        for (int i = 0; i < 8; i++) {
                            TxAnt[i] = Integer.parseInt(a_split[i]);
                        }
                    } catch (Exception e) {
                        code = 1;
                    }

                    /*
                       SearchString = 'RxAnt=';
                       searchind = strfind(A, SearchString);
                       searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                       vdat.RxAnt = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%d',8);
                    */
                    searchString = "RxAnt=";
                    searchind = strFind(A, searchString);
                    try {
                        String[] a_split =
                                A.substring(
                                                searchind[0] + searchString.length(),
                                                searchCR[0] + searchind[0])
                                        .split(",");
                        if (a_split.length != 8) {
                            throw new AssertionError("RxAnt has wrong number of values");
                        }
                        RxAnt = new int[8];
                        for (int i = 0; i < 8; i++) {
                            RxAnt[i] = Integer.parseInt(a_split[i]);
                        }
                    } catch (Exception e) {
                        code = 1;
                    }

                    /*
                       ind = find(vdat.TxAnt~=1);
                       vdat.TxAnt(ind) = [];
                       ind = find(vdat.RxAnt~=1);
                       vdat.RxAnt(ind) = [];
                    */
                    // Remove any elements that are 0.
                    ArrayList<Integer> txList = new ArrayList<>();
                    ArrayList<Integer> rxList = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        if (TxAnt[i] != 0) {
                            txList.add(TxAnt[i]);
                        }
                        if (RxAnt[i] != 0) {
                            rxList.add(RxAnt[i]);
                        }
                    }
                    TxAnt = new int[txList.size()];
                    for (int i = 0; i < txList.size(); i++) {
                        TxAnt[i] = txList.get(i);
                    }
                    RxAnt = new int[rxList.size()];
                    for (int i = 0; i < rxList.size(); i++) {
                        RxAnt[i] = rxList.get(i);
                    }

                    /*
                       if vdat.Average
                           vdat.ChirpsInBurst = 1;
                       else
                           vdat.ChirpsInBurst = vdat.SubBurstsInBurst * length(vdat.TxAnt) * ...
                           length(vdat.RxAnt) * vdat.NAttenuators;
                       end
                    */
                    if (average != 0) {
                        chirpsInBurst = 1;
                    } else {
                        chirpsInBurst =
                                subBurstsInBurst * TxAnt.length * RxAnt.length * nAttenuators;
                    }

                    /*
                        SearchString = '*** End Header ***';
                        searchind = strfind(A, SearchString);

                        burstpointer = burstpointer + searchind(1) + length(SearchString);

                    */
                    searchString = "*** End Header ***";
                    searchind = strFind(A, searchString);

                    burstpointer += searchind[0] + searchString.length();
                } catch (Exception e) {
                    /*
                    catch
                        vdat.Code = -2;
                        vdat.Burst = BurstCount;
                        return
                    */
                    code = -2;
                    burst = burstCount;
                }
            }
            /*
                    WordsPerBurst = vdat.ChirpsInBurst * WperChirpCycle;
                    if BurstCount < Burst && burstpointer <= filelength - MaxHeaderLen
                        if vdat.Average
                            burstpointer = burstpointer + vdat.ChirpsInBurst * WperChirpCycle*4;
                        else
                            burstpointer = burstpointer + vdat.ChirpsInBurst * WperChirpCycle*2;
                        end
                    end
                    BurstCount = BurstCount + 1;
                end
            */
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

        int[] searchCR;

        /*
            % Extract remaining information from header
            SearchString = 'Time stamp=';
            searchind = strfind(A, SearchString);
            if isempty(searchind)
                vdat.Code = -4;
                return
            end
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                td = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),...
                    '%d-%d-%d %d:%d:%d');
                vdat.TimeStamp = datenum(td(1),td(2),td(3),td(4),td(5),td(6));
            catch err
                vdat.Code = 1;
            end
        */
        String searchString = "Time stamp=";
        int[] searchind = strFind(A, searchString);
        if (searchind.length == 0) {
            code = -4;
            return;
        }

        try {
            searchCR = strFind(A, "\13\10");
            DateParser dp =
                    new DateParser(
                            A.substring(
                                    searchind[0] + searchString.length(),
                                    searchCR[0] + searchind[0]));
            Integer[] dt = dp.getDateFields();
            dateTime = LocalDateTime.of(dt[0], dt[1], dt[2], dt[3], dt[4], dt[5]);

        } catch (Exception e) {
            code = 1;
        }

        /*
            searchind = strfind(A, 'Temp1=');
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Temperature_1 = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchString = "Temp1=";
        searchind = strFind(A, searchString);
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            temperature1 =
                    Double.parseDouble(
                            A.substring(
                                    searchind[0] + searchString.length(),
                                    searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            SearchString = 'Temp2=';
            searchind = strfind(A, SearchString);
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.Temperature_2 = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchString = "Temp2=";
        searchind = strFind(A, searchString);
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            temperature2 =
                    Double.parseDouble(
                            A.substring(
                                    searchind[0] + searchString.length(),
                                    searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            SearchString = 'BatteryVoltage=';
            searchind = strfind(A, SearchString);
            try
                searchCR = strfind(A(searchind(1):end),[char(13),char(10)]);
                vdat.BatteryVoltage = sscanf(A(searchind(1)+length(SearchString):searchCR(1)+searchind(1)),'%f');
            catch err
                vdat.Code = 1;
            end
        */
        searchString = "BatteryVoltage=";
        searchind = strFind(A, searchString);
        searchCR = strFind(A.substring(searchind[0]), "\13\10");
        try {
            batteryVoltage =
                    Double.parseDouble(
                            A.substring(
                                    searchind[0] + searchString.length(),
                                    searchCR[0] + searchind[0]));
        } catch (Exception e) {
            code = 1;
        }

        /*
            fseek(fid,burstpointer-1,'bof');
        */
        if (burstpointer - 1 < current_stream_pos) {
            f.reset();
            current_stream_pos = 0;
        }
        current_stream_pos += f.skip(burstpointer - 1 - current_stream_pos);

        /*
           if BurstCount == Burst+1
        */
        if (burstCount == totalNumberOfBursts + 1) {
            int count;
            /*
                if vdat.Average == 2
                    [vdat.v count] = fread(fid,WordsPerBurst,'*int32','ieee-le');
                elseif vdat.Average == 1
                    fseek(fid, burstpointer+1,'bof');
                    [vdat.v count] = fread(fid,WordsPerBurst,'*int16','ieee-le');
                else
                    [vdat.v count] = fread(fid,WordsPerBurst,'*int16','ieee-le');
                end
                if count < WordsPerBurst
                    vdat.Code = 2;
                end
            */
            if (average == 2) {
                byte b[] = new byte[wordsPerBurst * 4];
                count = f.read(b);
                count /= 2; // Measures bytes not int32s;
                v = new double[count];

                for (int i = 0; i < count; i++) {
                    // Little Endian.
                    int x =
                            b[4 * i]
                                    + (256 * b[4 * i + 1])
                                    + (256 * 256 * b[4 * i + 2])
                                    + (256 * 256 * 256 * b[4 * i + 3]);
                    v[i] = x;
                }
            } else if (average == 1) {
                if (burstpointer + 1 < current_stream_pos) {
                    f.reset();
                    current_stream_pos = 0;
                }
                current_stream_pos += f.skip(burstpointer + 1 - current_stream_pos);

                byte b[] = new byte[wordsPerBurst * 4];
                count = f.read(b);
                count /= 4; // Measures bytes not floats;
                v = new double[count];

                for (int i = 0; i < count; i++) {
                    // Little Endian.
                    float x =
                            ByteBuffer.wrap(b, 4 * i, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    v[i] = x;
                }
            } else {
                byte b[] = new byte[wordsPerBurst * 2];
                count = f.read(b);
                count /= 2; // Measures bytes not int16s;
                v = new double[count];

                for (int i = 0; i < count; i++) {
                    // Little Endian.
                    int x = b[2 * i] + (256 * b[2 * i + 1]);
                    v[i] = x;
                }
            }

            /*
                if count < WordsPerBurst
                    vdat.Code = 2;
                end
            */
            if (count < wordsPerBurst) {
                code = 2;
            }

            /*
                vdat.v(vdat.v<0) = vdat.v(vdat.v<0) + 2^16;
            */
            for (int i = 0; i < v.length; i++) {
                if (v[i] < 0) {
                    v[i] = v[i] + Math.pow(2, 16);
                }
            }

            /*
                vdat.v = vdat.v * 2.5 / 2^16;
            */
            for (int i = 0; i < v.length; i++) {
                v[i] = v[i] * 2.5 / Math.pow(2, 16);
            }

            /*
               if vdat.Average == 2
                   vdat.v = vdat.v / (vdat.SubBurstsInBurst * vdat.NAttenuators);
               end

            */
            if (average == 2) {
                for (int i = 0; i < v.length; i++) {
                    v[i] = v[i] / (subBurstsInBurst * nAttenuators);
                }
            }

            /*
                vdat.Startind = (1:WperChirpCycle:WperChirpCycle*vdat.ChirpsInBurst)';
                vdat.Endind = vdat.Startind + SamplesPerChirp - 1;
            */
            ArrayList<Integer> startIndList = new ArrayList<>();
            for (int i = 1; i <= WperChirpCycle * chirpsInBurst; i += WperChirpCycle) {
                startIndList.add(i);
            }
            startInd = new int[startIndList.size()];
            for (int i = 0; i < startIndList.size(); i++) {
                startInd[i] = startIndList.get(i);
                endInd[i] = startInd[i] + WperChirpCycle - 1;
            }

            /*
                vdat.Burst = Burst;
            */
            burst = totalNumberOfBursts;
        } else {
            /*
            % Too few bursts in file
                vdat.Burst = BurstCount - 1;
                vdat.Code = -4;

             */
            burst = burstCount - 1;
            code = -4;
        }

        /*
            fclose(fid);
        */
        f.close();
    }
}
