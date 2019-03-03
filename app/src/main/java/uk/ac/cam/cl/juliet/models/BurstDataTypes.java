package uk.ac.cam.cl.juliet.models;

public enum BurstDataTypes {
    AMPLITUDE("Amplitude"),
    PHASE("Phase"),
    TIME("Time");

    private String displayableName;

    BurstDataTypes(String name) {
        this.displayableName = name;
    }

    public String getDisplayableName() {
        return displayableName;
    }

    public static BurstDataTypes fromString(String str) {
        for (BurstDataTypes bdt : BurstDataTypes.values()) {
            if (bdt.getDisplayableName().equalsIgnoreCase(str)) {
                return bdt;
            }
        }
        throw new IllegalArgumentException("No such BurstDataType of " + str + " exists.");
    }
}
