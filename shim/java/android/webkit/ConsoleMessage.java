package android.webkit;

/**
 * Shim: android.webkit.ConsoleMessage
 * OH mapping: @ohos.web.webview — console message parameter in onConsoleMessage
 *
 * Encapsulates a JavaScript console message received from the web page.
 * Delivered to {@link WebChromeClient#onConsoleMessage(ConsoleMessage)}.
 *
 * On OpenHarmony the equivalent is the JsResult / console-message object
 * provided by the ArkUI Web component's {@code onConsoleMessage} event.
 */
public class ConsoleMessage {

    // ── MessageLevel enum ──

    /**
     * Severity level of the console message.
     * OH equivalent: JsMessageLevel in ArkUI Web event callbacks.
     */
    public enum MessageLevel {
        /** Verbose hint, lower priority than LOG. */
        TIP,
        /** Informational message ({@code console.log}). */
        LOG,
        /** Warning message ({@code console.warn}). */
        WARNING,
        /** Error message ({@code console.error}). */
        ERROR,
        /** Debug message ({@code console.debug}). */
        DEBUG
    }

    // ── Fields ──

    private final String       message;
    private final String       sourceId;
    private final int          lineNumber;
    private final MessageLevel messageLevel;

    // ── Constructor ──

    /**
     * Constructs a ConsoleMessage.
     *
     * @param message       text of the console message
     * @param sourceId      source file URL or identifier
     * @param lineNumber    line number in the source file
     * @param messageLevel  severity level
     */
    public ConsoleMessage(String message, String sourceId,
                          int lineNumber, MessageLevel messageLevel) {
        this.message      = message;
        this.sourceId     = sourceId;
        this.lineNumber   = lineNumber;
        this.messageLevel = messageLevel;
    }

    // ── Accessors ──

    /** Returns the console message text. */
    public String message() {
        return message;
    }

    /** Returns the source file identifier (URL or filename). */
    public String sourceId() {
        return sourceId;
    }

    /** Returns the line number in the source file where the message originated. */
    public int lineNumber() {
        return lineNumber;
    }

    /** Returns the severity level of the message. */
    public MessageLevel messageLevel() {
        return messageLevel;
    }
}
