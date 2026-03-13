package android.view.translation;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Android-compatible TranslationManager stub (API 31+).
 * Provides on-device translation capability queries and translator creation.
 */
public class TranslationManager {

    public TranslationManager() {}

    /**
     * Returns supported on-device translation capabilities for the given source
     * and target format types. Stub returns an empty list.
     */
    public List<?> getOnDeviceTranslationCapabilities(int sourceFormat, int targetFormat) {
        return Collections.emptyList();
    }

    /**
     * Creates an on-device translator for the given spec. Stub returns null.
     */
    public Object createOnDeviceTranslator(Object translationSpec) {
        return null;
    }
}
