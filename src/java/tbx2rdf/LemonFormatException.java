package tbx2rdf;

/**
 * Indicates that a constraint in a lemon file was not satisfied as expected
 * @author John P. McCrae - Universität Bielefeld
 */
public class LemonFormatException extends Exception {

    /**
     * Creates a new instance of <code>LemonFormatException</code> without
     * detail message.
     */
    public LemonFormatException() {
    }

    /**
     * Constructs an instance of <code>LemonFormatException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LemonFormatException(String msg) {
        super(msg);
    }
}
