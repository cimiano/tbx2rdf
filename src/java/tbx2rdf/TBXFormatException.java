package tbx2rdf;

/**
 * Thrown if the input document is not a valid TBX document.
 * See sample files in the folder sample for some common errors in TBX files.
 * @author John P. McCrae  - Universitaet Bielfeld
 */
public class TBXFormatException extends RuntimeException {
    public TBXFormatException() {
    }

    public TBXFormatException(String message) {
        super(message);
    }

    public TBXFormatException(Throwable cause) {
        super(cause);
    }

    public TBXFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
