package uk.ac.cam.cl.juliet.models;

import java.io.Serializable;
import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.Burst;

/**
 * Encapsulates both single Bursts and lists of Bursts as a single data type to be displayed in a
 * list.
 *
 * @author Ben Cole
 */
public class SingleOrManyBursts implements Serializable {

    /** Internally used to specify which type an instance contains. */
    private enum Type {
        SINGLE,
        MANY;
    }
    /** Thrown when a single burst is accessed as if it were a collection of bursts. */
    public class AccessSingleBurstAsManyException extends Exception {}

    /** Thrown when a collection of bursts are accessed as if they were a single burst. */
    public class AccessManyBurstsAsSingleException extends Exception {}

    private Type type;
    private List<SingleOrManyBursts> listOfBursts;
    private Burst singleBurst;
    private boolean syncedToOneDrive;
    private String directoryName;

    /**
     * Creates an instance for a single Burst object.
     *
     * @param burst The Burst to be contained
     */
    public SingleOrManyBursts(Burst burst, boolean isSyncedToOneDrive) {
        this.singleBurst = burst;
        type = Type.SINGLE;
        syncedToOneDrive = isSyncedToOneDrive;
    }

    /**
     * Creates an instance for many Burst objects.
     *
     * @param listOfBursts The List of SingleOrManyBurst instances to be contained
     */
    public SingleOrManyBursts(
            List<SingleOrManyBursts> listOfBursts, boolean isSyncedToOneDrive, String dirName) {
        this.listOfBursts = listOfBursts;
        type = Type.MANY;
        syncedToOneDrive = isSyncedToOneDrive;
        this.directoryName = dirName;
    }

    /**
     * Returns whether this is a single Burst, as opposed to a collection of them.
     *
     * @return true if this is a single burst; false if this is a collection of bursts
     */
    public boolean getIsSingleBurst() {
        return (type == Type.SINGLE);
    }

    /**
     * Returns whether this is a collection of Bursts, as opposed to a single one.
     *
     * @return true if this is many bursts; false if this is a single burst
     */
    public boolean getIsManyBursts() {
        return (type == Type.MANY);
    }

    /**
     * Returns the contained single Burst.
     *
     * @return the Burst object contained in this instance
     * @throws AccessManyBurstsAsSingleException when called on an instance containing many Bursts
     *     rather than one
     */
    public Burst getSingleBurst() throws AccessManyBurstsAsSingleException {
        if (getIsSingleBurst()) {
            return singleBurst;
        } else {
            throw new AccessManyBurstsAsSingleException();
        }
    }

    /**
     * Returns the contained List of SingleOrManyBursts.
     *
     * @return the List of Burst objects contained in this instance
     * @throws AccessSingleBurstAsManyException when called on an instance containing only a single
     *     Burst object
     */
    public List<SingleOrManyBursts> getListOfBursts() throws AccessSingleBurstAsManyException {
        if (getIsManyBursts()) {
            return listOfBursts;
        } else {
            throw new AccessSingleBurstAsManyException();
        }
    }

    /**
     * Returns a name for a burst or collection of bursts suitable for displaying in the UI.
     *
     * @return the name of this file or collection of files
     */
    public String getNameToDisplay() {
        switch (type) {
            case SINGLE:
                return singleBurst.getFilename();
            case MANY:
                return this.directoryName;
            default:
                return "Something went wrong lol";
        }
    }

    /**
     * Returns a human-readable representation of the GPS location of this burst or collection of
     * bursts.
     *
     * @return A human-readable representation of the GPS location
     */
    public String getGPSToDisplay() {
        // TODO: Generate something meaningful here
        return "GPS coordinates or something here...";
    }

    /**
     * Sets whether this burst or collection of bursts has been synced with OneDrive.
     *
     * @param isSyncedToOneDrive true if this has been uploaded to OneDrive; false if not
     */
    public void setSyncStatus(boolean isSyncedToOneDrive) {
        syncedToOneDrive = isSyncedToOneDrive;
    }

    /**
     * Returns whether this burst or collection of bursts has been synced with OneDrive.
     *
     * @return true if this has been uploaded to OneDrive; false otherwise
     */
    public boolean getSyncStatus() {
        return syncedToOneDrive;
    }
}
