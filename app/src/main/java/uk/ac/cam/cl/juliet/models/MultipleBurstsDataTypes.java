package uk.ac.cam.cl.juliet.models;

public enum MultipleBurstsDataTypes {
    POWER("Power"),
    PHASE("Phase");

    private String displayableName;

    MultipleBurstsDataTypes(String name) {
        this.displayableName = name;
    }

    public String getDisplayableName() {
        return displayableName;
    }

    public static MultipleBurstsDataTypes fromString(String str) {
        for (MultipleBurstsDataTypes mbdt : MultipleBurstsDataTypes.values()) {
            if (mbdt.getDisplayableName().equalsIgnoreCase(str)) {
                return mbdt;
            }
        }
        throw new IllegalArgumentException("No such BurstDataType of " + str + " exists.");
    }
}
