package android.database.sqlite;
import android.database.SQLException;
import android.database.SQLException;

import android.database.SQLException;

/**
 * A SQLite exception that indicates there was an error with SQL parsing or execution.
 */
public class SQLiteException extends SQLException {

    public SQLiteException() {
        super();
    }

    public SQLiteException(String error) {
        super(error);
    }

    public SQLiteException(String error, Throwable cause) {
        super(error, cause);
    }
}
