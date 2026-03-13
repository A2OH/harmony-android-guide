package android.content;
import android.graphics.Path;
import android.net.Uri;
import android.graphics.Path;
import android.net.Uri;
import java.net.URI;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible UriMatcher shim.
 *
 * Utility class to aid in matching URIs in content providers. URIs are
 * matched against a trie of path patterns registered with {@link #addURI}.
 *
 * Path segments can be:
 * <ul>
 *   <li>A literal string (e.g. "items")</li>
 *   <li>{@code #} — matches a single segment containing only digits</li>
 *   <li>{@code *} — matches any single path segment</li>
 * </ul>
 *
 * Pure Java implementation — no OH bridge calls required.
 */
public class UriMatcher {

    /** Special code returned by {@link #match} when no URI matches. */
    public static final int NO_MATCH = -1;

    // ── Internal trie node ──

    private static final class Node {
        final String token;           // the path segment token this node represents
        int code = NO_MATCH;          // match code, or NO_MATCH if intermediate
        List<Node> children = new ArrayList<>();

        Node(String token) {
            this.token = token;
        }
    }

    // ── Root nodes keyed by authority ──

    // We store one virtual root node; its children are authority nodes.
    private final Node mRoot;
    private final int mNoMatchCode;

    // ── Constructor ──

    /**
     * Creates the root node of the URI matcher.
     * @param code the code to match for the root URI ("*")
     */
    public UriMatcher(int code) {
        mNoMatchCode = code;
        mRoot = new Node("root");
        mRoot.code = code;
    }

    // ── Registration ──

    /**
     * Add a URI to match, and the code to return when this URI is matched.
     * URI nodes may be exact matches (e.g. "foo") or contain wildcards:
     * <ul>
     *   <li>{@code #} — matches a segment of digits only</li>
     *   <li>{@code *} — matches any segment</li>
     * </ul>
     *
     * @param authority the authority to match (e.g. "com.example.provider")
     * @param path      the path to match (e.g. "items/#")
     * @param code      the code that is returned when a URI is matched against
     *                  the given components. Must be positive.
     */
    public void addURI(String authority, String path, int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code " + code + " is invalid: must be >= 0");
        }

        String[] tokens;
        if (path == null || path.isEmpty()) {
            tokens = new String[0];
        } else {
            tokens = path.split("/", -1);
        }

        // Navigate / build the trie path: root -> authority -> path segments
        Node node = mRoot;
        node = addChild(node, authority);
        for (String token : tokens) {
            node = addChild(node, token);
        }
        node.code = code;
    }

    /** Find or create a child node with the given token. */
    private Node addChild(Node parent, String token) {
        for (Node child : parent.children) {
            if (child.token.equals(token)) {
                return child;
            }
        }
        Node child = new Node(token);
        parent.children.add(child);
        return child;
    }

    // ── Matching ──

    /**
     * Try to match against the path in a collection of URIs. If the path
     * matches a URI registered with {@link #addURI}, the corresponding code
     * is returned.
     *
     * @param uri the URI to match
     * @return the code for the matched node, or the value supplied to the
     *         constructor if there is no matched node
     */
    public int match(Uri uri) {
        if (uri == null) return mNoMatchCode;

        String authority = uri.getAuthority();
        String path      = uri.getPath();

        // Strip leading '/' from path before splitting
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }

        String[] pathSegments = (path == null || path.isEmpty())
                ? new String[0]
                : path.split("/", -1);

        // Walk trie: root -> authority -> path segments
        Node node = matchChild(mRoot, authority);
        if (node == null) return mNoMatchCode;

        for (String segment : pathSegments) {
            node = matchChild(node, segment);
            if (node == null) return mNoMatchCode;
        }

        return node.code;
    }

    /**
     * Find the best child of {@code parent} matching {@code segment}.
     * Precedence: exact match > '#' digit wildcard > '*' wildcard.
     */
    private Node matchChild(Node parent, String segment) {
        Node digitWild = null;
        Node anyWild   = null;

        for (Node child : parent.children) {
            if (child.token.equals(segment)) {
                return child;  // exact match wins immediately
            } else if ("#".equals(child.token)) {
                if (isDigits(segment)) {
                    digitWild = child;
                }
            } else if ("*".equals(child.token)) {
                anyWild = child;
            }
        }

        if (digitWild != null) return digitWild;
        if (anyWild   != null) return anyWild;
        return null;
    }

    /** @return true if the string consists entirely of ASCII digit characters */
    private boolean isDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
}
