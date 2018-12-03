package datasettester;

/**
 * Contains the 4 possible evaluation states
 * @author Matti J. Frind
 */
public enum Classification {

    /**
     * Represents a correct detection.
     */
    TRUE_POSITIVE, 

    /**
     * Represents a false detection, which was classified as true.
     */
    FALSE_POSITIVE, 

    /**
     * Represents a false detection, which was classified as false.
     */
    FALSE_NEGATIVE, 

    /**
     * Default state.
     */
    DEFAULT; 
}
