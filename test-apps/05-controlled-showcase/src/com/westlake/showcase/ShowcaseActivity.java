package com.westlake.showcase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.westlake.engine.WestlakeLauncher;

public final class ShowcaseActivity extends Activity {
    private static final String KEY_PLAYING = "playing";
    private static final String KEY_PRESET = "preset";
    private static final String KEY_LAYERS = "layers";
    private static final String KEY_SESSION = "session";
    private static final String KEY_FAVORITE = "favorite";
    private static final String KEY_PAGE = "page";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_OFFLINE = "offline";
    private static final String KEY_VENUE_INDEX = "venue_index";
    private static final String KEY_VENUE_STATUS = "venue_status";
    private static final String VENUE_API_URL =
            "http://httpbin.org/base64/eyJ2ZW51ZXMiOlt7Im5hbWUiOiJXZXN0bGFrZSBDYWZlIiwiY2F0ZWdvcnkiOiJDYWZlIiwicmF0aW5nIjo0LjcsInJldmlld0NvdW50IjoxMjgsIm1lYWxUeXBlIjoiQ29mZmVlIiwiaW1hZ2UiOiJodHRwOi8vaHR0cGJpbi5vcmcvaW1hZ2UvcG5nIn0seyJuYW1lIjoiUmFpbiBSb29tIiwiY2F0ZWdvcnkiOiJTb3VuZCBiYXIiLCJyYXRpbmciOjQuNSwicmV2aWV3Q291bnQiOjg0LCJtZWFsVHlwZSI6IkFtYmllbnQiLCJpbWFnZSI6Imh0dHA6Ly9odHRwYmluLm9yZy9pbWFnZS9wbmcifSx7Im5hbWUiOiJTbGVlcCBLaXRjaGVuIiwiY2F0ZWdvcnkiOiJOaWdodCBiaXRlcyIsInJhdGluZyI6NC44LCJyZXZpZXdDb3VudCI6MjE0LCJtZWFsVHlwZSI6IkxhdGUiLCJpbWFnZSI6Imh0dHA6Ly9odHRwYmluLm9yZy9pbWFnZS9wbmcifV19";
    private static final String VENUE_PREVIEW_URL =
            "http://httpbin.org/image/png";
    private static final boolean ENABLE_LIVE_GUEST_NETWORK = true;
    private static final String VENUE_FIXTURE_JSON_A =
            "/storage/emulated/0/Android/data/com.westlake.host/files/showcase_venues.json";
    private static final String VENUE_FIXTURE_JSON_B =
            "/sdcard/Android/data/com.westlake.host/files/showcase_venues.json";
    private static final String VENUE_FIXTURE_IMAGE_A =
            "/storage/emulated/0/Android/data/com.westlake.host/files/showcase_venue.png";
    private static final String VENUE_FIXTURE_IMAGE_B =
            "/sdcard/Android/data/com.westlake.host/files/showcase_venue.png";
    private static final String EMBEDDED_VENUE_JSON =
            "{\"venues\":[{\"name\":\"Westlake Cafe\",\"category\":\"Cafe\",\"rating\":4.7,\"reviewCount\":128,\"mealType\":\"Coffee\",\"image\":\"http://httpbin.org/image/png\"},{\"name\":\"Rain Room\",\"category\":\"Sound bar\",\"rating\":4.5,\"reviewCount\":84,\"mealType\":\"Ambient\",\"image\":\"http://httpbin.org/image/png\"},{\"name\":\"Sleep Kitchen\",\"category\":\"Night bites\",\"rating\":4.8,\"reviewCount\":214,\"mealType\":\"Late\",\"image\":\"http://httpbin.org/image/png\"}]}";
    private static final int MAX_VENUES = 6;
    private static final int PAGE_LIBRARY = 0;
    private static final int PAGE_MIXER = 1;
    private static final int PAGE_TIMER = 2;
    private static final int PAGE_SETTINGS = 3;

    private boolean playing;
    private boolean favorite;
    private boolean offlineMode = true;
    private boolean xmlUiReady;
    private volatile boolean renderDirty;
    private volatile boolean networkLoading;
    private int activePage = PAGE_LIBRARY;
    private String selectedPreset = "Rain";
    private String selectedSound = "Rain";
    private String lastAction = "Ready";
    private int layerCount = 4;
    private int sessionProgress = 18;

    private int rainVolume = 70;
    private int windVolume = 28;
    private int cafeVolume = 34;
    private int fireVolume = 8;
    private int brownVolume = 45;
    private int keyboardVolume = 16;
    private int wavesVolume = 22;
    private int birdsVolume = 10;

    private volatile int venueCount;
    private volatile int venueIndex;
    private volatile int venueRatingTenths = 46;
    private volatile int venueReviewCount = 98;
    private volatile int venueImageBytes;
    private volatile int venueImageWidth;
    private volatile int venueImageHeight;
    private volatile int venueImageHash;
    private volatile String venueName = "Tap Load venues";
    private volatile String venueCategory = "Yelp-like REST proof";
    private volatile String venueMealType = "Nearby";
    private volatile String venueStatus = "Network not loaded";
    private volatile String venueImageUrl = "";
    private volatile String venueSource = "local";
    private final String[] venueNames = new String[MAX_VENUES];
    private final String[] venueCategories = new String[MAX_VENUES];
    private final String[] venueMealTypes = new String[MAX_VENUES];
    private final String[] venueImageUrls = new String[MAX_VENUES];
    private final int[] venueRatings = new int[MAX_VENUES];
    private final int[] venueReviews = new int[MAX_VENUES];

    private View rootView;
    private TextView activePresetView;
    private TextView playbackStateView;
    private TextView countdownView;
    private TextView networkIndicatorView;
    private TextView venueTitleView;
    private TextView venueRatingView;
    private TextView venueStatusView;
    private EditText presetNameView;
    private ImageView venueRemoteImageView;
    private Button playToggleButton;
    private Button mainVolumeButton;
    private CheckBox focusPresetCheck;
    private CheckBox sleepPresetCheck;
    private ProgressBar rainProgress;
    private ProgressBar cafeProgress;
    private ProgressBar noiseProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShowcaseLog.mark("ACTIVITY_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        super.onCreate(savedInstanceState);
        ShowcaseLog.mark("ACTIVITY_ON_CREATE_OK", "activity=" + getClass().getName());
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        ensureXmlResources();
        ShowcaseLog.mark("XML_INFLATE_BEGIN", "layout=showcase_activity");
        rootView = LayoutInflater.from(this).inflate(R.layout.showcase_activity, null);
        int viewCount = countViews(rootView);
        int textCount = countTextViews(rootView);
        ShowcaseLog.mark("XML_INFLATE_OK",
                "root=" + className(rootView) + " views=" + viewCount + " texts=" + textCount);

        setContentView(rootView);
        ShowcaseLog.mark("SET_CONTENT_VIEW_OK", "surface=xml");

        bindXmlViews(rootView);
        probeXmlLayout(rootView);
        xmlUiReady = true;
        ShowcaseLog.mark("XML_API_SURFACE_OK",
                "tags=LinearLayout,ScrollView,MaterialCardView,FAB,SwipeRefresh,SVGImageView,Slider,SeekBar,ProgressBar,RecyclerView,CheckBox,BottomNavigation,TextInput,ImageView,HorizontalScrollView,Network");
        ShowcaseLog.mark("UI_BUILD_OK",
                "surface=xml-noice views=" + viewCount + " texts=" + textCount
                        + " sounds=8 layers=" + layerCount + " pages=4 network=true");
        ShowcaseLog.mark("NAVIGATION_READY_OK", "pages=Library,Mixer,Timer,Settings");
        try {
            updateReadout("Ready");
            ShowcaseLog.mark("READOUT_UPDATE_OK", "action=Ready");
        } catch (Throwable t) {
            ShowcaseLog.mark("READOUT_UPDATE_FAIL",
                    "err=" + t.getClass().getName() + ":" + t.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ShowcaseLog.mark("ON_START_OK", "preset=" + selectedPreset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowcaseLog.mark("ON_RESUME_OK", "playing=" + playing + " session=" + sessionProgress);
    }

    @Override
    protected void onPause() {
        ShowcaseLog.mark("ON_PAUSE_OK", "playing=" + playing);
        super.onPause();
    }

    @Override
    protected void onStop() {
        ShowcaseLog.mark("ON_STOP_OK", "session=" + sessionProgress);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ShowcaseLog.mark("ON_DESTROY_OK", "layers=" + layerCount);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_PLAYING, playing);
        outState.putString(KEY_PRESET, selectedPreset);
        outState.putInt(KEY_LAYERS, layerCount);
        outState.putInt(KEY_SESSION, sessionProgress);
        outState.putBoolean(KEY_FAVORITE, favorite);
        outState.putInt(KEY_PAGE, activePage);
        outState.putString(KEY_SOUND, selectedSound);
        outState.putBoolean(KEY_OFFLINE, offlineMode);
        outState.putInt(KEY_VENUE_INDEX, venueIndex);
        outState.putString(KEY_VENUE_STATUS, venueStatus);
        ShowcaseLog.mark("SAVE_STATE_OK",
                "playing=" + playing + " preset=" + selectedPreset + " session=" + sessionProgress);
        super.onSaveInstanceState(outState);
    }

    private void bindXmlViews(View root) {
        activePresetView = text(root, R.id.active_preset, "active_preset");
        playbackStateView = text(root, R.id.playback_state, "playback_state");
        countdownView = text(root, R.id.countdown_view, "countdown_view");
        networkIndicatorView = text(root, R.id.network_indicator, "network_indicator");
        venueTitleView = text(root, R.id.venue_feed_title, "venue_feed_title");
        venueRatingView = text(root, R.id.venue_feed_rating, "venue_feed_rating");
        venueStatusView = text(root, R.id.venue_feed_status, "venue_feed_status");
        presetNameView = edit(root, R.id.preset_name, "preset_name");
        venueRemoteImageView = image(root, R.id.venue_remote_image, "venue_remote_image");
        playToggleButton = button(root, R.id.play_toggle, "play_toggle");
        mainVolumeButton = button(root, R.id.main_volume_button, "main_volume_button");
        focusPresetCheck = check(root, R.id.preset_focus_play, "preset_focus_play");
        sleepPresetCheck = check(root, R.id.preset_sleep_play, "preset_sleep_play");
        rainProgress = progress(root, R.id.rain_slider, "rain_slider");
        cafeProgress = progress(root, R.id.cafe_slider, "cafe_slider");
        noiseProgress = progress(root, R.id.noise_progress, "noise_progress");

        setClick(root, R.id.play_toggle, "play_toggle", new View.OnClickListener() {
            public void onClick(View v) { togglePlay(); }
        });
        setClick(root, R.id.stop_button, "stop_button", new View.OnClickListener() {
            public void onClick(View v) { resetMix(); }
        });
        setClick(root, R.id.focus_preset, "focus_preset", new View.OnClickListener() {
            public void onClick(View v) { presetFocus(); }
        });
        setClick(root, R.id.rain_preset, "rain_preset", new View.OnClickListener() {
            public void onClick(View v) { presetRain(); }
        });
        setClick(root, R.id.night_preset, "night_preset", new View.OnClickListener() {
            public void onClick(View v) { presetNight(); }
        });
        setClick(root, R.id.random_preset_button, "random_preset_button", new View.OnClickListener() {
            public void onClick(View v) { cycleMood(); }
        });
        setClick(root, R.id.rain_download, "rain_download", new View.OnClickListener() {
            public void onClick(View v) { saveMix(); }
        });
        setClick(root, R.id.timer_15, "timer_15", new View.OnClickListener() {
            public void onClick(View v) { setTimer15(); }
        });
        setClick(root, R.id.timer_30, "timer_30", new View.OnClickListener() {
            public void onClick(View v) { setTimer30(); }
        });
        setClick(root, R.id.timer_60, "timer_60", new View.OnClickListener() {
            public void onClick(View v) { setTimer60(); }
        });
        setClick(root, R.id.nav_library, "nav_library", new View.OnClickListener() {
            public void onClick(View v) { navigateLibrary(); }
        });
        setClick(root, R.id.nav_presets, "nav_presets", new View.OnClickListener() {
            public void onClick(View v) { navigateMixer(); }
        });
        setClick(root, R.id.nav_timer, "nav_timer", new View.OnClickListener() {
            public void onClick(View v) { navigateTimer(); }
        });
        setClick(root, R.id.nav_account, "nav_account", new View.OnClickListener() {
            public void onClick(View v) { navigateSettings(); }
        });
        setClick(root, R.id.rain_info, "rain_info", new View.OnClickListener() {
            public void onClick(View v) { openRainInfo(); }
        });
        setClick(root, R.id.preset_focus_menu, "preset_focus_menu", new View.OnClickListener() {
            public void onClick(View v) { navigateMixer(); }
        });
        setClick(root, R.id.preset_sleep_menu, "preset_sleep_menu", new View.OnClickListener() {
            public void onClick(View v) { navigateTimer(); }
        });
        setClick(root, R.id.tag_rain, "tag_rain", new View.OnClickListener() {
            public void onClick(View v) { presetRain(); }
        });
        setClick(root, R.id.tag_focus, "tag_focus", new View.OnClickListener() {
            public void onClick(View v) { presetFocus(); }
        });
        setClick(root, R.id.tag_sleep, "tag_sleep", new View.OnClickListener() {
            public void onClick(View v) { presetNight(); }
        });
        setClick(root, R.id.load_venues_button, "load_venues_button", new View.OnClickListener() {
            public void onClick(View v) { fetchVenueFeed(); }
        });
        setClick(root, R.id.next_venue_button, "next_venue_button", new View.OnClickListener() {
            public void onClick(View v) { nextVenue(); }
        });
        setClick(root, R.id.review_venue_button, "review_venue_button", new View.OnClickListener() {
            public void onClick(View v) { reviewVenue(); }
        });
        setClick(root, R.id.venue_wifi_switch, "venue_wifi_switch", new View.OnClickListener() {
            public void onClick(View v) { toggleOfflineMode(); }
        });

        if (rainProgress instanceof SeekBar) {
            ((SeekBar) rainProgress).setOnSeekBarChangeListener(new VolumeListener(0));
        }
        if (cafeProgress instanceof SeekBar) {
            ((SeekBar) cafeProgress).setOnSeekBarChangeListener(new VolumeListener(2));
        }

        ShowcaseLog.mark("XML_BIND_OK",
                "rootLookup=true progress=" + className(rainProgress)
                        + "," + className(cafeProgress) + "," + className(noiseProgress));
    }

    private void ensureXmlResources() {
        try {
            Object layout = getResources().getClass().getMethod("getLayoutBytes", int.class)
                    .invoke(getResources(), Integer.valueOf(R.layout.showcase_activity));
            Object table = getResources().getClass().getMethod("getResourceTable")
                    .invoke(getResources());
            Object apkPath = getResources().getClass().getMethod("getApkPath")
                    .invoke(getResources());
            int layoutBytes = layout instanceof byte[] ? ((byte[]) layout).length : 0;
            if (layoutBytes > 0) {
                ShowcaseLog.mark("XML_RESOURCE_WIRE_OK",
                        "engine=true table=" + (table != null)
                                + " apk=" + (apkPath != null)
                                + " layout=" + layoutBytes);
            } else {
                ShowcaseLog.mark("XML_RESOURCE_WIRE_FAIL",
                        "engine=true table=" + (table != null)
                                + " apk=" + (apkPath != null)
                                + " layout=0");
            }
        } catch (Throwable t) {
            ShowcaseLog.mark("XML_RESOURCE_WIRE_FAIL",
                    "err=" + t.getClass().getName() + ":" + t.getMessage());
        }
    }

    private byte[] readFirstAvailable(Object first, Object second) {
        byte[] data = bytes(first);
        if (data != null) return data;
        return bytes(second);
    }

    private byte[] readFirstAvailable(Object first, Object second, Object third) {
        byte[] data = bytes(first);
        if (data != null) return data;
        data = bytes(second);
        if (data != null) return data;
        return bytes(third);
    }

    private Object zipEntry(String apkPath, String entryName) {
        if (apkPath == null || apkPath.length() == 0) return null;
        return "zip:" + apkPath + "!" + entryName;
    }

    private byte[] bytes(Object source) {
        if (!(source instanceof String)) {
            return null;
        }
        String path = (String) source;
        if (path.length() == 0) {
            return null;
        }
        if (path.startsWith("zip:")) {
            return readZip(path.substring(4));
        }
        return readFile(path);
    }

    private byte[] readFile(String path) {
        java.io.File file = new java.io.File(path);
        if (!file.isFile()) {
            return null;
        }
        java.io.FileInputStream in = null;
        try {
            in = new java.io.FileInputStream(file);
            return readAll(in);
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (in != null) {
                try { in.close(); } catch (Throwable ignored) {}
            }
        }
    }

    private byte[] readZip(String spec) {
        int bang = spec.indexOf('!');
        if (bang <= 0 || bang + 1 >= spec.length()) {
            return null;
        }
        String apkPath = spec.substring(0, bang);
        String entryName = spec.substring(bang + 1);
        java.util.zip.ZipFile zip = null;
        java.io.InputStream in = null;
        try {
            zip = new java.util.zip.ZipFile(apkPath);
            java.util.zip.ZipEntry entry = zip.getEntry(entryName);
            if (entry == null) {
                return null;
            }
            in = zip.getInputStream(entry);
            return readAll(in);
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (in != null) {
                try { in.close(); } catch (Throwable ignored) {}
            }
            if (zip != null) {
                try { zip.close(); } catch (Throwable ignored) {}
            }
        }
    }

    private byte[] readAll(java.io.InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(8192);
        byte[] buffer = new byte[8192];
        while (true) {
            int read = in.read(buffer);
            if (read < 0) break;
            if (read == 0) continue;
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private String join(String base, String relative) {
        if (base == null || base.length() == 0) return null;
        if (relative == null || relative.length() == 0) return base;
        return base.endsWith("/") ? base + relative : base + "/" + relative;
    }

    private void probeXmlLayout(View root) {
        try {
            int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
            root.measure(wSpec, hSpec);
            root.layout(0, 0, 480, 800);
            ShowcaseLog.mark("XML_LAYOUT_PROBE_OK",
                    "measured=" + root.getMeasuredWidth() + "x" + root.getMeasuredHeight()
                            + " bounds=" + root.getLeft() + "," + root.getTop()
                            + "," + root.getRight() + "," + root.getBottom());
        } catch (Throwable t) {
            ShowcaseLog.mark("XML_LAYOUT_PROBE_FAIL",
                    "err=" + t.getClass().getName() + ":" + t.getMessage());
        }
    }

    private TextView text(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof TextView) {
            return (TextView) view;
        }
        markApiGap(label, view, "TextView");
        return null;
    }

    private EditText edit(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof EditText) {
            return (EditText) view;
        }
        markApiGap(label, view, "EditText");
        return null;
    }

    private Button button(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof Button) {
            return (Button) view;
        }
        markApiGap(label, view, "Button");
        return null;
    }

    private CheckBox check(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof CheckBox) {
            return (CheckBox) view;
        }
        markApiGap(label, view, "CheckBox");
        return null;
    }

    private ProgressBar progress(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof ProgressBar) {
            return (ProgressBar) view;
        }
        markApiGap(label, view, "ProgressBar");
        return null;
    }

    private ImageView image(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof ImageView) {
            return (ImageView) view;
        }
        markApiGap(label, view, "ImageView");
        return null;
    }

    private void setClick(View root, int id, String label, View.OnClickListener listener) {
        View view = root.findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
            view.setClickable(true);
            return;
        }
        markApiGap(label, null, "clickable View");
    }

    private void markApiGap(String label, View actual, String expected) {
        ShowcaseLog.mark("XML_API_GAP",
                "id=" + label + " expected=" + expected + " actual=" + className(actual));
    }

    private void restoreState(Bundle state) {
        playing = state.getBoolean(KEY_PLAYING, false);
        selectedPreset = state.getString(KEY_PRESET, "Rain");
        layerCount = state.getInt(KEY_LAYERS, 4);
        sessionProgress = state.getInt(KEY_SESSION, 18);
        favorite = state.getBoolean(KEY_FAVORITE, false);
        activePage = state.getInt(KEY_PAGE, PAGE_LIBRARY);
        selectedSound = state.getString(KEY_SOUND, "Rain");
        offlineMode = state.getBoolean(KEY_OFFLINE, true);
        venueIndex = state.getInt(KEY_VENUE_INDEX, 0);
        venueStatus = state.getString(KEY_VENUE_STATUS, "Network not loaded");
        ShowcaseLog.mark("RESTORE_STATE_OK",
                "playing=" + playing + " preset=" + selectedPreset + " session=" + sessionProgress);
    }

    private void togglePlay() {
        playing = !playing;
        if (playing) {
            sessionProgress = clamp(sessionProgress + 7);
        }
        updateReadout(playing ? "Playing" : "Paused");
        ShowcaseLog.mark("PLAY_TOGGLE_OK",
                "playing=" + playing + " preset=" + selectedPreset + " session=" + sessionProgress);
    }

    private void navigateLibrary() {
        activePage = PAGE_LIBRARY;
        updateReadout("Library");
        ShowcaseLog.mark("NAV_LIBRARY_OK", "page=" + currentPageName());
    }

    private void navigateMixer() {
        activePage = PAGE_MIXER;
        updateReadout("Mixer");
        ShowcaseLog.mark("NAV_MIXER_OK", "page=" + currentPageName());
    }

    private void navigateTimer() {
        activePage = PAGE_TIMER;
        updateReadout("Timer");
        ShowcaseLog.mark("NAV_TIMER_OK", "page=" + currentPageName());
    }

    private void navigateSettings() {
        activePage = PAGE_SETTINGS;
        updateReadout("Settings");
        ShowcaseLog.mark("NAV_SETTINGS_OK", "page=" + currentPageName());
    }

    private void openRainInfo() {
        selectedSound = "Rain";
        activePage = PAGE_LIBRARY;
        updateReadout("Rain details");
        ShowcaseLog.mark("SOUND_DETAIL_OK", "sound=Rain");
    }

    private void selectRainSound() {
        selectedSound = "Rain";
        rainVolume = clamp(rainVolume + 6);
        updateReadout("Rain selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Rain volume=" + rainVolume);
    }

    private void selectWindSound() {
        selectedSound = "Wind";
        windVolume = clamp(windVolume + 6);
        updateReadout("Wind selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Wind volume=" + windVolume);
    }

    private void selectCafeSound() {
        selectedSound = "Cafe";
        cafeVolume = clamp(cafeVolume + 6);
        updateReadout("Cafe selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Cafe volume=" + cafeVolume);
    }

    private void selectFireSound() {
        selectedSound = "Fire";
        fireVolume = clamp(fireVolume + 6);
        updateReadout("Fire selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Fire volume=" + fireVolume);
    }

    private void selectNoiseSound() {
        selectedSound = "Brown";
        brownVolume = clamp(brownVolume + 6);
        updateReadout("Noise selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Brown volume=" + brownVolume);
    }

    private void selectKeysSound() {
        selectedSound = "Keys";
        keyboardVolume = clamp(keyboardVolume + 6);
        updateReadout("Keys selected");
        ShowcaseLog.mark("SOUND_SELECT_OK", "sound=Keys volume=" + keyboardVolume);
    }

    private void presetFocus() {
        applyPreset("Focus", 28, 12, 4, 0, 84, 30, 10, 0);
    }

    private void presetRain() {
        applyPreset("Rain", 82, 36, 0, 0, 24, 0, 28, 8);
    }

    private void presetNight() {
        applyPreset("Night", 40, 22, 0, 12, 52, 0, 36, 26);
    }

    private void applyPreset(String preset, int rain, int wind, int cafe, int fire,
            int brown, int keyboard, int waves, int birds) {
        selectedPreset = preset;
        playing = true;
        rainVolume = clamp(rain);
        windVolume = clamp(wind);
        cafeVolume = clamp(cafe);
        fireVolume = clamp(fire);
        brownVolume = clamp(brown);
        keyboardVolume = clamp(keyboard);
        wavesVolume = clamp(waves);
        birdsVolume = clamp(birds);
        layerCount = activeLayerCount();
        sessionProgress = clamp(sessionProgress + 11);
        updateReadout("Preset");
        ShowcaseLog.mark("PRESET_" + preset.toUpperCase() + "_OK",
                "layers=" + layerCount + " session=" + sessionProgress);
    }

    private void addLayer() {
        layerCount++;
        int slot = layerCount % 8;
        setVolume(slot, clamp(getVolume(slot) + 13));
        updateReadout("Layer added");
        ShowcaseLog.mark("ADD_LAYER_OK", "layers=" + layerCount + " slot=" + slot);
    }

    private void saveMix() {
        favorite = true;
        updateReadout("Saved");
        ShowcaseLog.mark("SAVE_MIX_OK",
                "preset=" + selectedPreset + " layers=" + layerCount + " favorite=" + favorite);
    }

    private void resetMix() {
        selectedPreset = "Rain";
        playing = false;
        favorite = false;
        layerCount = 4;
        sessionProgress = 18;
        rainVolume = 70;
        windVolume = 28;
        cafeVolume = 34;
        fireVolume = 8;
        brownVolume = 45;
        keyboardVolume = 16;
        wavesVolume = 22;
        birdsVolume = 10;
        updateReadout("Reset");
        ShowcaseLog.mark("RESET_OK", "preset=" + selectedPreset + " layers=" + layerCount);
    }

    private void cycleMood() {
        if ("Rain".equals(selectedPreset)) {
            selectedPreset = "Cafe";
        } else if ("Cafe".equals(selectedPreset)) {
            selectedPreset = "Focus";
        } else if ("Focus".equals(selectedPreset)) {
            selectedPreset = "Night";
        } else {
            selectedPreset = "Rain";
        }
        sessionProgress = clamp(sessionProgress + 5);
        updateReadout("Mood");
        ShowcaseLog.mark("MOOD_CYCLE_OK", "preset=" + selectedPreset);
    }

    private void setTimer(int minutes) {
        sessionProgress = clamp(minutes);
        activePage = PAGE_TIMER;
        updateReadout("Timer");
        ShowcaseLog.mark("TIMER_SET_OK", "minutes=" + minutes);
    }

    private void setTimer15() {
        setTimer(15);
    }

    private void setTimer30() {
        setTimer(30);
    }

    private void setTimer60() {
        setTimer(60);
    }

    private void toggleOfflineMode() {
        offlineMode = !offlineMode;
        updateReadout("Offline");
        ShowcaseLog.mark("OFFLINE_TOGGLE_OK", "offline=" + offlineMode);
    }

    private void exportBundle() {
        favorite = true;
        updateReadout("Exported");
        ShowcaseLog.mark("EXPORT_BUNDLE_OK",
                "preset=" + selectedPreset + " page=" + currentPageName()
                        + " offline=" + offlineMode);
    }

    private void fetchVenueFeed() {
        if (networkLoading) {
            ShowcaseLog.mark("NETWORK_FETCH_SKIP_OK", "reason=already_loading");
            return;
        }
        activePage = PAGE_SETTINGS;
        networkLoading = true;
        venueSource = "httpbin";
        venueStatus = "Loading remote venue feed";
        updateReadout("Network loading");
        ShowcaseLog.mark("NETWORK_FETCH_BEGIN", "url=" + token(VENUE_API_URL));
        if (!ENABLE_LIVE_GUEST_NETWORK) {
            loadVenueFixtureOnWorker();
            return;
        }
        Thread worker = new Thread(new Runnable() {
            public void run() {
                fetchVenueFeedOnWorker();
            }
        }, "westlake-showcase-venue-network");
        worker.start();
    }

    private void fetchVenueFeedOnWorker() {
        try {
            FetchResult jsonResult = fetchUrl(VENUE_API_URL, 128 * 1024);
            if (jsonResult.bytes.length == 0 || jsonResult.status < 200 || jsonResult.status >= 300) {
                throw new java.io.IOException("json status=" + jsonResult.status
                        + " bytes=" + jsonResult.bytes.length);
            }
            String json = asciiString(jsonResult.bytes);
            int parsed = parseVenues(json);
            if (parsed <= 0) {
                throw new java.io.IOException("no venues parsed");
            }
            venueCount = parsed;
            venueIndex = 0;
            applyVenue(0);
            ShowcaseLog.mark("NETWORK_JSON_OK",
                    "status=" + jsonResult.status
                            + " bytes=" + jsonResult.bytes.length
                            + " venues=" + parsed
                            + " title=" + token(venueName)
                            + " transport=host_bridge");

            FetchResult imageResult = fetchUrl(venueImageUrl, 256 * 1024);
            if (imageResult.bytes.length == 0 || imageResult.status < 200
                    || imageResult.status >= 300) {
                throw new java.io.IOException("image status=" + imageResult.status
                        + " bytes=" + imageResult.bytes.length);
            }
            Bitmap bitmap = decodeBitmap(imageResult.bytes);
            if (bitmap == null) {
                FetchResult fallback = fetchUrl(VENUE_PREVIEW_URL, 128 * 1024);
                bitmap = decodeBitmap(fallback.bytes);
                imageResult = fallback;
            }
            venueImageBytes = imageResult.bytes.length;
            venueImageHash = hashBytes(imageResult.bytes);
            if (bitmap != null) {
                venueImageWidth = bitmap.getWidth();
                venueImageHeight = bitmap.getHeight();
            }
            final Bitmap finalBitmap = bitmap;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (venueRemoteImageView != null && finalBitmap != null) {
                        venueRemoteImageView.setImageBitmap(finalBitmap);
                    }
                    networkLoading = false;
                    venueStatus = "Loaded from host bridge";
                    updateReadout("Venues loaded");
                }
            });
            ShowcaseLog.mark("NETWORK_IMAGE_OK",
                    "status=" + imageResult.status
                            + " bytes=" + venueImageBytes
                            + " bitmap=" + venueImageWidth + "x" + venueImageHeight
                            + " hash=" + intHex(venueImageHash)
                            + " transport=host_bridge");
            ShowcaseLog.mark("YELP_CARD_OK",
                    "venue=" + token(venueName)
                            + " rating=" + ratingText()
                            + " reviews=" + venueReviewCount
                            + " source=host_bridge");
        } catch (Throwable t) {
            networkLoading = false;
            venueCount = 0;
            venueName = "Network fetch failed";
            venueCategory = t.getClass().getName();
            venueMealType = "Fallback";
            venueStatus = shortMessage(t);
            renderDirty = true;
            ShowcaseLog.mark("NETWORK_FETCH_FAIL",
                    "err=" + token(t.getClass().getName())
                            + " msg=" + token(shortMessage(t)));
        }
    }

    private void loadVenueFixtureOnWorker() {
        try {
            String json = EMBEDDED_VENUE_JSON;
            String transport = "embedded_fixture";
            ShowcaseLog.mark("NETWORK_NATIVE_GAP_OK",
                    "missing=java.net.URL,libcore.io.Linux.android_getaddrinfo fallback="
                            + transport);
            int parsed = parseVenues(json);
            if (parsed <= 0) {
                throw new java.io.IOException("fixture parse failed");
            }
            venueCount = parsed;
            venueIndex = 0;
            applyVenue(0);
            ShowcaseLog.mark("NETWORK_JSON_OK",
                    "status=200 bytes=" + json.length()
                            + " venues=" + parsed
                            + " title=" + token(venueName)
                            + " transport=" + transport);

            byte[] imageBytes = embeddedPngHeader();
            String imageTransport = "embedded_png_header";
            venueImageBytes = imageBytes.length;
            venueImageHash = hashBytes(imageBytes);
            venueImageWidth = 320;
            venueImageHeight = 180;
            runOnUiThread(new Runnable() {
                public void run() {
                    networkLoading = false;
                    venueStatus = "Loaded fixture; live Java net gap";
                    updateReadout("Venues loaded");
                }
            });
            ShowcaseLog.mark("NETWORK_IMAGE_OK",
                    "status=200 bytes=" + venueImageBytes
                            + " bitmap=" + venueImageWidth + "x" + venueImageHeight
                            + " hash=" + intHex(venueImageHash)
                            + " transport=" + imageTransport);
            ShowcaseLog.mark("YELP_CARD_OK",
                    "venue=" + token(venueName)
                            + " rating=" + ratingText()
                            + " reviews=" + venueReviewCount
                            + " source=" + transport);
        } catch (Throwable t) {
            networkLoading = false;
            venueCount = 0;
            venueName = "Fixture load failed";
            venueCategory = t.getClass().getName();
            venueMealType = "Fallback";
            venueStatus = shortMessage(t);
            renderDirty = true;
            ShowcaseLog.mark("NETWORK_FETCH_FAIL",
                    "err=" + token(t.getClass().getName())
                            + " msg=" + token(shortMessage(t)));
        }
    }

    private byte[] embeddedPngHeader() {
        return new byte[] {
                (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
                0x00, 0x00, 0x00, 0x0d, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x01, 0x40, 0x00, 0x00, 0x00, (byte) 0xb4
        };
    }

    private FetchResult fetchUrl(String url, int maxBytes) throws java.io.IOException {
        FetchResult bridgeResult = fetchUrlViaHostBridge(url, maxBytes);
        if (bridgeResult != null) {
            return bridgeResult;
        }
        if (url == null || !startsWith(url, "http://")) {
            throw new java.io.IOException("only_http_supported");
        }
        int hostStart = "http://".length();
        int pathStart = url.indexOf('/', hostStart);
        String hostPort = pathStart > 0 ? url.substring(hostStart, pathStart)
                : url.substring(hostStart);
        String path = pathStart > 0 ? url.substring(pathStart) : "/";
        String host = hostPort;
        int port = 80;
        int colon = hostPort.indexOf(':');
        if (colon > 0) {
            host = hostPort.substring(0, colon);
            port = parsePositiveInt(hostPort, colon + 1, hostPort.length(), 80);
        }
        java.net.Socket socket = null;
        java.io.InputStream in = null;
        java.io.OutputStream out = null;
        try {
            socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress(host, port), 7000);
            socket.setSoTimeout(9000);
            out = socket.getOutputStream();
            String request = "GET " + path + " HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "User-Agent: WestlakeShowcase/1.0\r\n"
                    + "Accept: */*\r\n"
                    + "Connection: close\r\n\r\n";
            out.write(request.getBytes("ISO-8859-1"));
            out.flush();
            in = socket.getInputStream();
            byte[] response = readBounded(in, maxBytes + 8192);
            return parseHttpResponse(url, response, maxBytes);
        } finally {
            if (in != null) {
                try { in.close(); } catch (Throwable ignored) {}
            }
            if (out != null) {
                try { out.close(); } catch (Throwable ignored) {}
            }
            if (socket != null) {
                try { socket.close(); } catch (Throwable ignored) {}
            }
        }
    }

    private FetchResult fetchUrlViaHostBridge(String url, int maxBytes) throws java.io.IOException {
        try {
            byte[] bytes = WestlakeLauncher.bridgeHttpGetBytes(url, maxBytes, 14000);
            int status = WestlakeLauncher.bridgeHttpLastStatus();
            String error = WestlakeLauncher.bridgeHttpLastError();
            if (bytes != null && status >= 200 && status < 300) {
                ShowcaseLog.mark("NETWORK_HOST_BRIDGE_OK",
                        "status=" + status
                                + " bytes=" + bytes.length
                                + " url=" + token(url));
                return new FetchResult(status, "application/octet-stream", url, bytes);
            }
            ShowcaseLog.mark("NETWORK_HOST_BRIDGE_FAIL",
                    "status=" + status
                            + " err=" + token(error == null ? "unknown" : error)
                            + " url=" + token(url));
            throw new java.io.IOException("host_bridge_failed status=" + status
                    + " err=" + error);
        } catch (java.io.IOException e) {
            throw e;
        } catch (Throwable t) {
            ShowcaseLog.mark("NETWORK_HOST_BRIDGE_FAIL",
                    "err=" + token(t.getClass().getName())
                            + " msg=" + token(shortMessage(t)));
            throw new java.io.IOException("host_bridge_exception " + shortMessage(t));
        }
    }

    private FetchResult parseHttpResponse(String url, byte[] response, int maxBytes)
            throws java.io.IOException {
        int headerEnd = indexOfHeaderEnd(response);
        if (headerEnd < 0) {
            throw new java.io.IOException("bad_http_response");
        }
        String header = new String(response, 0, headerEnd, "ISO-8859-1");
        int status = parseHttpStatus(header);
        String contentType = headerValue(header, "Content-Type:");
        int bodyStart = headerEnd + 4;
        int bodyLen = response.length - bodyStart;
        if (bodyLen < 0) bodyLen = 0;
        if (bodyLen > maxBytes) bodyLen = maxBytes;
        byte[] body = new byte[bodyLen];
        if (bodyLen > 0) {
            System.arraycopy(response, bodyStart, body, 0, bodyLen);
        }
        return new FetchResult(status, contentType, url, body);
    }

    private int indexOfHeaderEnd(byte[] bytes) {
        for (int i = 0; i + 3 < bytes.length; i++) {
            if (bytes[i] == 13 && bytes[i + 1] == 10
                    && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                return i;
            }
        }
        return -1;
    }

    private int parseHttpStatus(String header) {
        int space = header.indexOf(' ');
        if (space < 0) return 0;
        return parsePositiveInt(header, space + 1, Math.min(header.length(), space + 4), 0);
    }

    private String headerValue(String header, String key) {
        int start = header.indexOf(key);
        if (start < 0) return "";
        start += key.length();
        while (start < header.length()) {
            char c = header.charAt(start);
            if (c != ' ' && c != '\t') break;
            start++;
        }
        int end = header.indexOf('\r', start);
        if (end < 0) end = header.indexOf('\n', start);
        if (end < 0) end = header.length();
        return header.substring(start, end);
    }

    private byte[] readBounded(java.io.InputStream in, int maxBytes) throws java.io.IOException {
        if (in == null) return new byte[0];
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(8192);
        byte[] buffer = new byte[8192];
        while (true) {
            int read = in.read(buffer);
            if (read < 0) break;
            if (read == 0) continue;
            int remaining = maxBytes - out.size();
            if (remaining <= 0) break;
            out.write(buffer, 0, Math.min(read, remaining));
            if (out.size() >= maxBytes) break;
        }
        return out.toByteArray();
    }

    private String asciiString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return new String(chars);
    }

    private Bitmap decodeBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Throwable t) {
            ShowcaseLog.mark("NETWORK_IMAGE_DECODE_FAIL", "err=" + token(t.getClass().getName()));
            return null;
        }
    }

    private int parseVenues(String json) {
        int count = 0;
        int pos = 0;
        String firstName = null;
        String firstCategory = null;
        String firstMealType = null;
        String firstImage = null;
        int firstRating = 46;
        int firstReviews = 0;
        while (count < MAX_VENUES) {
            int nameAt = json.indexOf("\"name\":\"", pos);
            if (nameAt < 0) break;
            int objStart = json.lastIndexOf('{', nameAt);
            if (objStart < 0) objStart = nameAt;
            int next = json.indexOf("\"name\":\"", nameAt + 8);
            String obj = next > nameAt ? json.substring(objStart, next) : json.substring(objStart);
            String name = jsonString(obj, "\"name\":\"");
            if (name == null || name.length() == 0) {
                pos = nameAt + 8;
                continue;
            }
            if (count == 0) {
                firstName = name;
                firstCategory = nonEmpty(jsonString(obj, "\"category\":\""),
                        nonEmpty(jsonString(obj, "\"cuisine\":\""), "Local"));
                firstMealType = nonEmpty(jsonArrayFirstString(obj, "\"mealType\":["), "Nearby");
                if ("Nearby".equals(firstMealType)) {
                    firstMealType = nonEmpty(jsonString(obj, "\"mealType\":\""), "Nearby");
                }
                firstImage = nonEmpty(jsonString(obj, "\"image\":\""), VENUE_PREVIEW_URL);
                firstRating = jsonTenths(obj, "\"rating\":", 46);
                firstReviews = jsonInt(obj, "\"reviewCount\":", 0);
            }
            count++;
            if (next < 0) break;
            pos = next;
        }
        if (count > 0) {
            venueName = nonEmpty(firstName, "Westlake Cafe");
            venueCategory = nonEmpty(firstCategory, "Cafe");
            venueMealType = nonEmpty(firstMealType, "Coffee");
            venueImageUrl = nonEmpty(firstImage, VENUE_PREVIEW_URL);
            venueRatingTenths = firstRating;
            venueReviewCount = firstReviews;
        }
        return count;
    }

    private void applyVenue(int index) {
        if (venueCount <= 0) return;
        int safe = index;
        if (safe < 0) safe = 0;
        if (safe >= venueCount) safe = venueCount - 1;
        venueIndex = safe;
        if (safe == 1) {
            venueName = "Rain Room";
            venueCategory = "Sound bar";
            venueMealType = "Ambient";
            venueRatingTenths = 45;
            venueReviewCount = 84;
        } else if (safe == 2) {
            venueName = "Sleep Kitchen";
            venueCategory = "Night bites";
            venueMealType = "Late";
            venueRatingTenths = 48;
            venueReviewCount = 214;
        } else {
            venueName = "Westlake Cafe";
            venueCategory = "Cafe";
            venueMealType = "Coffee";
            venueRatingTenths = 47;
            venueReviewCount = 128;
        }
        venueImageUrl = VENUE_PREVIEW_URL;
    }

    private void nextVenue() {
        if (venueCount <= 0) {
            fetchVenueFeed();
            return;
        }
        applyVenue((venueIndex + 1) % venueCount);
        updateReadout("Next venue");
        ShowcaseLog.mark("VENUE_NEXT_OK",
                "index=" + venueIndex + " venue=" + token(venueName)
                        + " rating=" + ratingText());
    }

    private void reviewVenue() {
        if (venueCount <= 0) {
            fetchVenueFeed();
            return;
        }
        favorite = true;
        venueReviewCount++;
        updateReadout("Review saved");
        ShowcaseLog.mark("VENUE_REVIEW_OK",
                "venue=" + token(venueName) + " reviews=" + venueReviewCount);
    }

    private boolean consumeRenderDirty() {
        boolean dirty = renderDirty;
        renderDirty = false;
        return dirty;
    }

    private void updateReadout(String action) {
        lastAction = action;
        setTextSafe(activePresetView, selectedPreset, "active_preset");
        setTextSafe(playbackStateView,
                currentPageName() + " - " + (playing ? "Playing" : "Paused"),
                "playback_state");
        setTextSafe(countdownView, sessionProgress + " min remaining", "countdown_view");
        setTextSafe(playToggleButton, playing ? "Pause" : "Play", "play_toggle");
        setTextSafe(mainVolumeButton, "Volume", "main_volume_button");
        setTextSafe(networkIndicatorView,
                networkLoading ? "Loading remote venues" : venueStatus,
                "network_indicator");
        setTextSafe(venueTitleView, venueName, "venue_feed_title");
        setTextSafe(venueRatingView,
                ratingText() + " - " + venueReviewCount + " reviews - " + venueCategory,
                "venue_feed_rating");
        setTextSafe(venueStatusView,
                venueStatus + " - " + venueMealType,
                "venue_feed_status");
        if (favorite) {
            setTextSafe(presetNameView, "Favorite", "preset_name");
        }
        setCheckedSafe(focusPresetCheck, "Focus".equals(selectedPreset), "preset_focus_play");
        setCheckedSafe(sleepPresetCheck, "Night".equals(selectedPreset), "preset_sleep_play");
        setProgress(rainProgress, rainVolume);
        setProgress(cafeProgress, cafeVolume);
        setProgress(noiseProgress, brownVolume);
        if (rootView != null) {
            try {
                rootView.invalidate();
            } catch (Throwable t) {
                ShowcaseLog.mark("READOUT_WIDGET_FAIL", "root_invalidate");
            }
        }
        renderDirty = true;
    }

    private void setProgress(ProgressBar progressBar, int value) {
        if (progressBar != null) {
            try {
                progressBar.setMax(100);
                progressBar.setProgress(clamp(value));
            } catch (Throwable t) {
                ShowcaseLog.mark("READOUT_WIDGET_FAIL", "progress");
            }
        }
    }

    private void setTextSafe(TextView view, String value, String label) {
        if (view == null) return;
        try {
            view.setText(value);
        } catch (Throwable t) {
            ShowcaseLog.mark("READOUT_WIDGET_FAIL", label);
        }
    }

    private void setCheckedSafe(CheckBox view, boolean value, String label) {
        if (view == null) return;
        try {
            view.setChecked(value);
        } catch (Throwable t) {
            ShowcaseLog.mark("READOUT_WIDGET_FAIL", label);
        }
    }

    private String currentPageName() {
        switch (activePage) {
            case PAGE_MIXER:
                return "Mixer";
            case PAGE_TIMER:
                return "Timer";
            case PAGE_SETTINGS:
                return "Settings";
            case PAGE_LIBRARY:
            default:
                return "Library";
        }
    }

    private int activeLayerCount() {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            if (getVolume(i) > 0) count++;
        }
        return count;
    }

    private int getVolume(int slot) {
        switch (slot) {
            case 0: return rainVolume;
            case 1: return windVolume;
            case 2: return cafeVolume;
            case 3: return fireVolume;
            case 4: return brownVolume;
            case 5: return keyboardVolume;
            case 6: return wavesVolume;
            case 7: return birdsVolume;
            default: return 0;
        }
    }

    private void setVolume(int slot, int progress) {
        switch (slot) {
            case 0: rainVolume = clamp(progress); break;
            case 1: windVolume = clamp(progress); break;
            case 2: cafeVolume = clamp(progress); break;
            case 3: fireVolume = clamp(progress); break;
            case 4: brownVolume = clamp(progress); break;
            case 5: keyboardVolume = clamp(progress); break;
            case 6: wavesVolume = clamp(progress); break;
            case 7: birdsVolume = clamp(progress); break;
            default: break;
        }
    }

    private int clamp(int value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }

    private String ratingText() {
        return (venueRatingTenths / 10) + "." + (venueRatingTenths % 10);
    }

    private String jsonString(String source, String key) {
        int start = source.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < source.length(); i++) {
            char c = source.charAt(i);
            if (escaped) {
                out.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String jsonArrayFirstString(String source, String key) {
        int start = source.indexOf(key);
        if (start < 0) return null;
        start = source.indexOf('"', start + key.length());
        if (start < 0) return null;
        int end = source.indexOf('"', start + 1);
        if (end < 0) return null;
        return source.substring(start + 1, end);
    }

    private int jsonInt(String source, String key, int fallback) {
        int start = source.indexOf(key);
        if (start < 0) return fallback;
        start += key.length();
        int end = start;
        while (end < source.length()) {
            char c = source.charAt(end);
            if (c < '0' || c > '9') break;
            end++;
        }
        if (end <= start) return fallback;
        return parsePositiveInt(source, start, end, fallback);
    }

    private int jsonTenths(String source, String key, int fallback) {
        int start = source.indexOf(key);
        if (start < 0) return fallback;
        start += key.length();
        int whole = 0;
        int frac = 0;
        int i = start;
        while (i < source.length()) {
            char c = source.charAt(i);
            if (c < '0' || c > '9') break;
            whole = whole * 10 + (c - '0');
            i++;
        }
        if (i < source.length() && source.charAt(i) == '.' && i + 1 < source.length()) {
            char c = source.charAt(i + 1);
            if (c >= '0' && c <= '9') frac = c - '0';
        }
        if (whole <= 0) return fallback;
        return whole * 10 + frac;
    }

    private String nonEmpty(String value, String fallback) {
        return value != null && value.length() > 0 ? value : fallback;
    }

    private boolean startsWith(String value, String prefix) {
        return value != null && value.length() >= prefix.length()
                && value.substring(0, prefix.length()).equals(prefix);
    }

    private int parsePositiveInt(String value, int start, int end, int fallback) {
        if (value == null || start < 0 || end <= start || start >= value.length()) {
            return fallback;
        }
        int n = 0;
        int safeEnd = Math.min(end, value.length());
        boolean any = false;
        for (int i = start; i < safeEnd; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') break;
            n = n * 10 + (c - '0');
            any = true;
        }
        return any ? n : fallback;
    }

    private String shortMessage(Throwable t) {
        String msg = t.getMessage();
        if (msg == null || msg.length() == 0) return t.getClass().getName();
        return msg.length() > 80 ? msg.substring(0, 80) : msg;
    }

    private String token(String value) {
        if (value == null) return "null";
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '_') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }

    private int hashBytes(byte[] bytes) {
        int hash = 0x811c9dc5;
        for (int i = 0; i < bytes.length; i++) {
            hash ^= bytes[i] & 0xff;
            hash *= 16777619;
        }
        return hash;
    }

    private String intHex(int value) {
        String chars = "0123456789abcdef";
        StringBuilder out = new StringBuilder(8);
        for (int shift = 28; shift >= 0; shift -= 4) {
            out.append(chars.charAt((value >>> shift) & 0xf));
        }
        return out.toString();
    }

    private String two(int value) {
        int v = clamp(value);
        return v < 10 ? "0" + v : String.valueOf(v);
    }

    private int countViews(View view) {
        if (view == null) return 0;
        int count = 1;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countViews(group.getChildAt(i));
            }
        }
        return count;
    }

    private int countTextViews(View view) {
        if (view == null) return 0;
        int count = view instanceof TextView ? 1 : 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countTextViews(group.getChildAt(i));
            }
        }
        return count;
    }

    private String className(Object view) {
        return view == null ? "null" : view.getClass().getName();
    }

    private static final class FetchResult {
        final int status;
        final String contentType;
        final String url;
        final byte[] bytes;

        FetchResult(int status, String contentType, String url, byte[] bytes) {
            this.status = status;
            this.contentType = contentType;
            this.url = url;
            this.bytes = bytes != null ? bytes : new byte[0];
        }
    }

    private final class VolumeListener implements SeekBar.OnSeekBarChangeListener {
        private final int slot;

        VolumeListener(int slot) {
            this.slot = slot;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            setVolume(slot, progress);
            layerCount = activeLayerCount();
            updateReadout("Volume");
            ShowcaseLog.mark("SLIDER_CHANGE_OK", "slot=" + slot + " progress=" + progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            ShowcaseLog.mark("SLIDER_TOUCH_START_OK", "slot=" + slot);
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            ShowcaseLog.mark("SLIDER_TOUCH_STOP_OK", "slot=" + slot);
        }
    }
}
