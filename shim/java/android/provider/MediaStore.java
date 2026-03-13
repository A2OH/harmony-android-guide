package android.provider;
import android.net.Uri;
import android.net.Uri;

import android.net.Uri;

/**
 * Android-compatible MediaStore shim. Constants and stub query classes.
 */
public class MediaStore {
    public static final String AUTHORITY = "media";
    public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";

    public static final String EXTRA_OUTPUT = "output";

    /** Column constants shared across all media types. */
    public interface MediaColumns {
        String _ID = "_id";
        String DATA = "_data";
        String DISPLAY_NAME = "_display_name";
        String MIME_TYPE = "mime_type";
        String SIZE = "_size";
        String DATE_ADDED = "date_added";
        String DATE_MODIFIED = "date_modified";
        String WIDTH = "width";
        String HEIGHT = "height";
        String TITLE = "title";
    }

    public static class Images {
        public static class Media implements MediaColumns {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/images/media");
            public static final Uri INTERNAL_CONTENT_URI =
                Uri.parse("content://media/internal/images/media");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
        }
    }

    public static class Video {
        public static class Media implements MediaColumns {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/video/media");
            public static final Uri INTERNAL_CONTENT_URI =
                Uri.parse("content://media/internal/video/media");
        }
    }

    public static class Audio {
        public static class Media implements MediaColumns {
            public static final Uri EXTERNAL_CONTENT_URI =
                Uri.parse("content://media/external/audio/media");
            public static final Uri INTERNAL_CONTENT_URI =
                Uri.parse("content://media/internal/audio/media");
        }
    }

    public static class Files implements MediaColumns {
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse("content://media/external/file");

        public static Uri getContentUri(String volumeName) {
            return Uri.parse("content://media/" + volumeName + "/file");
        }

        public static Uri getContentUri(String volumeName, long rowId) {
            return Uri.parse("content://media/" + volumeName + "/file/" + rowId);
        }
    }

    public static class Downloads implements MediaColumns {
        public static final Uri EXTERNAL_CONTENT_URI =
            Uri.parse("content://media/external/downloads");
    }
}
