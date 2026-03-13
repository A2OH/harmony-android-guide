package android.content;
import android.net.Uri;
import android.net.Uri;
import java.net.URI;

import android.net.Uri;

/**
 * Android-compatible ContentUris shim.
 *
 * Utility class for appending and parsing IDs in content URIs.
 * Pure static utility — no state, no OH bridge calls required.
 *
 * Example usage:
 * <pre>
 *   Uri base   = Uri.parse("content://com.example/items");
 *   Uri withId = ContentUris.withAppendedId(base);
 *   // withId.toString() == "content://com.example/items/42"
 *
 *   long id = ContentUris.parseId(withId); // 42
 * </pre>
 */
public final class ContentUris {

    // Utility class — do not instantiate.
    private ContentUris() {}

    /**
     * Appends the given ID to the end of the path component of the given base URI.
     * This is a convenience method for:
     * <pre>
     *   Uri.parse(base.toString() + "/" + id)
     * </pre>
     *
     * @param contentUri the base URI to append the ID to
     * @param id         the ID to append
     * @return a new URI with the ID appended to the path
     */
    public static Uri withAppendedId(Uri contentUri, long id) {
        String base = contentUri.toString();
        // Remove trailing slash if present to avoid double-slash
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return Uri.parse(base + "/" + id);
    }

    /**
     * Extracts the ID from a URI that was created by {@link #withAppendedId}.
     * The last path segment of the URI must be a numeric string.
     *
     * @param contentUri a URI that ends with a numeric ID segment
     * @return the parsed ID
     * @throws NumberFormatException if the last path segment is not a valid long
     */
    public static long parseId(Uri contentUri) {
        String last = contentUri.getLastPathSegment();
        if (last == null) {
            throw new NumberFormatException("No path segments in URI: " + contentUri);
        }
        return Long.parseLong(last);
    }
}
