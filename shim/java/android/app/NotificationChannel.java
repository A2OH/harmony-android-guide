package android.app;

/**
 * Shim: android.app.NotificationChannel → OH notification slot config
 * Tier 1 — data holder, mapped when passed to NotificationManager.createNotificationChannel()
 */
public class NotificationChannel {
    private final String id;
    private CharSequence name;
    private int importance;
    private String description;
    private boolean enableLights;
    private boolean enableVibration;

    public NotificationChannel(String id, CharSequence name, int importance) {
        this.id = id;
        this.name = name;
        this.importance = importance;
    }

    public String getId() { return id; }
    public CharSequence getName() { return name; }
    public int getImportance() { return importance; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void enableLights(boolean lights) { this.enableLights = lights; }
    public void enableVibration(boolean vibration) { this.enableVibration = vibration; }

    public void setImportance(int importance) { this.importance = importance; }
}
