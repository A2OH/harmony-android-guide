package android.app;

import android.os.Bundle;

/**
 * Android-compatible VoiceInteractor shim.
 * Provides the interface for an Activity to interact with the user through
 * voice. Contains Request base class and concrete request types.
 * Stub implementation — no actual voice interaction in shim layer.
 */
public class VoiceInteractor {

    /**
     * Base class for voice interaction requests.
     */
    public static abstract class Request {
        private boolean mComplete = false;

        /** Cancel this active request. */
        public void cancel() {
            mComplete = true;
        }

        /** Returns whether this request is still active. */
        public boolean isComplete() {
            return mComplete;
        }

        /** Called when the request completes. Override in subclasses. */
        public void onCancel() {
            // stub
        }

        /** Called when the request is attached to the voice interactor. */
        public void onAttached(Object activity) {
            // stub
        }

        /** Called when the request is detached from the voice interactor. */
        public void onDetached() {
            // stub
        }
    }

    /**
     * A request for the user to confirm something via voice.
     */
    public static class ConfirmationRequest extends Request {
        private final Object mPrompt;
        private final Bundle mExtras;

        /**
         * Create a confirmation request.
         * @param prompt  the prompt to present (Object to avoid Prompt dependency)
         * @param extras  additional data for the request
         */
        public ConfirmationRequest(Object prompt, Bundle extras) {
            mPrompt = prompt;
            mExtras = extras;
        }

        /**
         * Called when the user confirms.
         * @param result additional result data
         */
        public void onConfirmationResult(boolean confirmed, Bundle result) {
            // stub — override in subclass
        }
    }

    /**
     * A request for the user to pick from a set of options via voice.
     */
    public static class PickOptionRequest extends Request {

        /**
         * An option the user can pick from.
         */
        public static class Option {
            private final CharSequence mLabel;
            private final int mIndex;

            public Option(CharSequence label, int index) {
                mLabel = label;
                mIndex = index;
            }

            public CharSequence getLabel() {
                return mLabel;
            }

            public int getIndex() {
                return mIndex;
            }
        }

        private final Object mPrompt;
        private final Option[] mOptions;
        private final Bundle mExtras;

        /**
         * Create a pick-option request.
         * @param prompt  the prompt to present (Object to avoid Prompt dependency)
         * @param options the available options
         * @param extras  additional data for the request
         */
        public PickOptionRequest(Object prompt, Option[] options, Bundle extras) {
            mPrompt = prompt;
            mOptions = options;
            mExtras = extras;
        }

        /**
         * Called when the user picks one or more options.
         * @param finished true if the interaction is complete
         * @param selections the selected options
         * @param result additional result data
         */
        public void onPickOptionResult(boolean finished, Option[] selections,
                Bundle result) {
            // stub — override in subclass
        }
    }

    /**
     * A request to complete the voice interaction session.
     */
    public static class CompleteVoiceRequest extends Request {
        private final Object mPrompt;
        private final Bundle mExtras;

        /**
         * Create a complete-voice request.
         * @param prompt the final message (Object to avoid Prompt dependency)
         * @param extras additional data
         */
        public CompleteVoiceRequest(Object prompt, Bundle extras) {
            mPrompt = prompt;
            mExtras = extras;
        }

        /**
         * Called when the completion is processed.
         * @param result additional result data
         */
        public void onCompleteResult(Bundle result) {
            // stub — override in subclass
        }
    }

    /**
     * Submit a request to the voice interactor.
     * @param request the request to submit
     * @return false in shim — no voice interactor available
     */
    public boolean submitRequest(Request request) {
        return false;
    }

    /**
     * Submit a request with a name tag.
     * @param request the request to submit
     * @param name    a name to identify this request
     * @return false in shim — no voice interactor available
     */
    public boolean submitRequest(Request request, String name) {
        return false;
    }

    /**
     * Returns the active requests, or an empty array.
     */
    public Request[] getActiveRequests() {
        return new Request[0];
    }

    /**
     * Returns whether the voice interactor supports the given command.
     */
    public boolean supportsCommands(String[] commands) {
        return false;
    }
}
