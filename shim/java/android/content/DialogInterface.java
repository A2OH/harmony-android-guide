package android.content;

/**
 * Android-compatible DialogInterface shim. Interface with standard button constants
 * and inner listener interfaces.
 */
public interface DialogInterface {

    int BUTTON_POSITIVE = -1;
    int BUTTON_NEGATIVE = -2;
    int BUTTON_NEUTRAL = -3;

    void dismiss();

    void cancel();

    /**
     * Listener for clicks on dialog buttons.
     */
    interface OnClickListener {
        void onClick(DialogInterface dialog, int which);
    }

    /**
     * Listener for dialog dismiss events.
     */
    interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

    /**
     * Listener for dialog cancel events.
     */
    interface OnCancelListener {
        void onCancel(DialogInterface dialog);
    }

    /**
     * Listener for dialog show events.
     */
    interface OnShowListener {
        void onShow(DialogInterface dialog);
    }

    /**
     * Listener for multi-choice click events in dialogs.
     */
    interface OnMultiChoiceClickListener {
        void onClick(DialogInterface dialog, int which, boolean isChecked);
    }

    /**
     * Listener for key events in dialogs.
     */
    interface OnKeyListener {
        boolean onKey(DialogInterface dialog, int keyCode, Object event);
    }
}
