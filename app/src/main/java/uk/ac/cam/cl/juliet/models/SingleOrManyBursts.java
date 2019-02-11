package uk.ac.cam.cl.juliet.models;

import java.util.List;

import uk.ac.cam.cl.juliet.computationengine.Burst;

/**
 * Encapsulates both single Bursts and lists of Bursts as a single data type to be displayed
 * in a list.
 *
 * @author Ben Cole
 */
public class SingleOrManyBursts {

    private enum Type {
        SINGLE,
        MANY
    }

    /**
     * Thrown when a single burst is accessed as if it were a collection of bursts.
     */
    public class AccessSingleBurstAsManyException extends Exception {}

    /**
     * Thrown when a collection of bursts are accessed as if they were a single burst.
     */
    public class AccessManyBurstsAsSingleException extends Exception {}

    private Type type;
    private List<SingleOrManyBursts> listOfBursts;
    private Burst singleBurst;

    /**
     * Creates an instance for a single Burst object.
     *
     * @param burst The Burst to be contained
     */
    public SingleOrManyBursts(Burst burst) {
        this.singleBurst = burst;
        type = Type.SINGLE;
    }

    /**
     * Creates an instance for many Burst objects.
     *
     * @param listOfBursts The List of SingleOrManyBurst instances to be contained
     */
    public SingleOrManyBursts(List<SingleOrManyBursts> listOfBursts) {
        this.listOfBursts = listOfBursts;
        type = Type.MANY;
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
     * @throws AccessManyBurstsAsSingleException when called on an instance containing many Bursts rather than one
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
     * @throws AccessSingleBurstAsManyException when called on an instance containing only a single Burst object
     */
    public List<SingleOrManyBursts> getListOfBursts() throws AccessSingleBurstAsManyException {
        if (getIsManyBursts()) {
            return listOfBursts;
        } else {
            throw new AccessSingleBurstAsManyException();
        }
    }

}
