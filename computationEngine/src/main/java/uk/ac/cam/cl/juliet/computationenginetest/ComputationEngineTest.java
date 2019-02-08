package uk.ac.cam.cl.juliet.computationenginetest;

import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.ComputationEngine;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;

/**
 * Class used to allow the running of a {@code ComputationEngine} without requiring the android app.
 */
public class ComputationEngineTest {

    /**
     * The main function for the app.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) throws InvalidBurstException {
        ComputationEngine engine = new ComputationEngine();
        Burst b = new Burst("/home/chris/Downloads/DATA2018-04-17-1300.DAT", 1);

        System.out.format("Code: %d\n", b.getCode());
        System.out.format("nSamples: %d\n", b.getNSamples());
        System.out.format("subBurstsInBurst: %d\n", b.getSubBurstsInBurst());
        System.out.format("Average: %d\n", b.getAverage());
        System.out.format("NAttenuators: %d\n", b.getNAttenuators());
        System.out.format("Attentuator1: %s\n", b.getAttenuator1());
        System.out.format("Attentuator2: %s\n", b.getAttenuator2());
        System.out.format("TxAnt: %s\n", b.getTxAnt());
        System.out.format("RxAnt: %s\n", b.getRxAnt());
        System.out.format("ChirpsInBurst: %d\n", b.getChirpsInBurst());
        System.out.format("TimeStamp: %s\n", b.getTimeStamp());
        System.out.format("Temperature1: %s\n", b.getTemperature1());
        System.out.format("Temperature2: %s\n", b.getTemperature2());
        System.out.format("BatteryVoltage: %s\n", b.getBatteryVoltage());
        System.out.format("v: %s\n", b.getV());
        System.out.format("StartInt: %s\n", b.getStartInd());
        System.out.format("EndInd: %s\n", b.getEndInd());
        System.out.format("Burst: %s\n", b.getBurst());
        System.out.format("FileFormat: %s\n", b.getFileFormat());
        System.out.format("Vif: %s\n", b.getVif());
        System.out.format("ChirpNum: %s\n", b.getChirpNum());
        System.out.format("ChirpTime: %s\n", b.getChirpTime());
        System.out.format("FileName: %s\n", b.getFilename());
        System.out.format("SamplesPerChirp: %s\n", b.getSamplesPerChirp());
        System.out.format("%s\n", b.getFs());
        System.out.format("%s\n", b.getF0());
        System.out.format("%s\n", b.getK());
        System.out.format("%s\n", b.getProcessing());
        System.out.format("%s\n", b.getF1());
        System.out.format("%s\n", b.getT());
        System.out.format("%s\n", b.getB());
        System.out.format("%s\n", b.getFc());
        System.out.format("%s\n", b.getDt());
        System.out.format("%s\n", b.getEr());
        System.out.format("%s\n", b.getCi());
        System.out.format("%s\n", b.getLambdac());
        System.out.format("%s\n", b.getTList());
        System.out.format("%s\n", b.getFList());
    }
}
