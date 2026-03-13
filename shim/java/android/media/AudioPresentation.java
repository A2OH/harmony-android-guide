package android.media;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Android-compatible AudioPresentation shim (API 28+).
 * Represents an audio presentation as defined by AC-4 and Dolby Digital Plus standards.
 */
public class AudioPresentation {

    /** Mastering indication constants. */
    public static final int MASTERING_NOT_INDICATED = -1;
    public static final int MASTERED_FOR_STEREO     = 0;
    public static final int MASTERED_FOR_SURROUND   = 1;
    public static final int MASTERED_FOR_3D         = 2;
    public static final int MASTERED_FOR_HEADPHONE  = 3;

    /** Content type constants. */
    public static final int CONTENT_UNKNOWN            = -1;
    public static final int CONTENT_MAIN               = 0;
    public static final int CONTENT_MUSIC_AND_EFFECTS  = 1;
    public static final int CONTENT_DIALOG             = 2;
    public static final int CONTENT_COMMENTARY         = 3;
    public static final int CONTENT_EMERGENCY          = 4;
    public static final int CONTENT_VOICEOVER          = 5;

    /**
     * Return the presentation ID.
     */
    public int getPresentationId() {
        return 0;
    }

    /**
     * Return the program ID.
     */
    public int getProgramId() {
        return 0;
    }

    /**
     * Return a map of locale-label pairs for this presentation.
     */
    public Map<Locale, String> getLabels() {
        return Collections.emptyMap();
    }

    /**
     * Return the locale corresponding to this presentation's language.
     */
    public Locale getLocale() {
        return Locale.ROOT;
    }

    /**
     * Return the mastering indication for this presentation.
     */
    public int getMasteringIndication() {
        return MASTERING_NOT_INDICATED;
    }

    /**
     * Return whether this presentation contains an audio description for the visually impaired.
     */
    public boolean hasAudioDescription() {
        return false;
    }

    /**
     * Return whether this presentation supports dialogue enhancement.
     */
    public boolean hasDialogueEnhancement() {
        return false;
    }

    /**
     * Return whether this presentation contains spoken subtitles.
     */
    public boolean hasSpokenSubtitles() {
        return false;
    }
}
