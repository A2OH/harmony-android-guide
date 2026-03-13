package android.database;

/**
 * An exception that indicates there was an error with SQL parsing or execution.
 */
public class SQLException extends RuntimeException {

    public SQLException() {
        super();
    }

    public SQLException(String error) {
        super(error);
    }

    public SQLException(String error, Throwable cause) {
        super(error, cause);
    }
}
