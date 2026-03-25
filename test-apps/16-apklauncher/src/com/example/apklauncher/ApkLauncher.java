package com.example.apklauncher;

import android.app.Activity;
import android.content.Context;
// PackageManager used via reflection on real phone
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.lang.reflect.Method;

/**
 * Launches a real installed APK's Activity inside WestlakeActivity.
 *
 * Strategy:
 * 1. createPackageContext() to get the APK's Context (with its resources + classloader)
 * 2. Load the Activity class from the APK's classloader
 * 3. Instantiate it and attach the APK's context
 * 4. Call onCreate → onStart → onResume
 * 5. Get the Activity's content view and display it in WestlakeActivity
 */
public class ApkLauncher {

    static Context ctx;
    static Activity hostActivity;

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}
        MaterialIcons.init(context);
    }

    static int dp(int d) { return (int)(d * ctx.getResources().getDisplayMetrics().density); }

    static GradientDrawable roundRect(int color, int r) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(r));
        return d;
    }

    static void show(final View view) {
        if (hostActivity == null) return;
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);
                hostActivity.setContentView(view);
            }
        });
    }

    /**
     * App gallery home screen — shows all runnable apps (custom + real APKs).
     */
    public static void showHome() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1A1A2E);

        // Status bar area
        LinearLayout statusBar = new LinearLayout(ctx);
        statusBar.setPadding(dp(16), dp(8), dp(16), dp(8));
        statusBar.setGravity(Gravity.CENTER_VERTICAL);
        TextView clock = new TextView(ctx);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a");
        clock.setText(sdf.format(new java.util.Date()));
        clock.setTextSize(14);
        clock.setTextColor(0xCCFFFFFF);
        statusBar.addView(clock, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView battery = new TextView(ctx);
        battery.setText("\uD83D\uDD0B 87%");
        battery.setTextSize(12);
        battery.setTextColor(0xCCFFFFFF);
        statusBar.addView(battery);
        root.addView(statusBar);

        // Title
        TextView title = new TextView(ctx);
        title.setText("Westlake Apps");
        title.setTextSize(26);
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setPadding(dp(20), dp(12), dp(20), dp(4));
        title.setLetterSpacing(0.02f);
        root.addView(title);

        TextView subtitle = new TextView(ctx);
        subtitle.setText("Android apps running on Westlake engine");
        subtitle.setTextSize(13);
        subtitle.setTextColor(0x88FFFFFF);
        subtitle.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        subtitle.setPadding(dp(20), 0, dp(20), dp(12));
        root.addView(subtitle);

        ScrollView scroll = new ScrollView(ctx);
        LinearLayout grid = new LinearLayout(ctx);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setPadding(dp(12), 0, dp(12), dp(16));

        // Custom apps section
        grid.addView(sectionLabel("Custom Apps"));

        LinearLayout row1 = new LinearLayout(ctx);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(appTile(MaterialIcons.RESTAURANT, "MockDonalds", 0xFFDA291C, true, new View.OnClickListener() {
            public void onClick(View v) { callApp("com.example.mockdonalds.MockApp", "init", "showMenu"); }
        }));
        row1.addView(appTile(MaterialIcons.PHONE, "Dialer", 0xFF1565C0, true, new View.OnClickListener() {
            public void onClick(View v) { callApp("com.example.dialer.DialerEntry", null, "launch"); }
        }));
        row1.addView(appTile(MaterialIcons.GROUP, "Social Feed", 0xFF1877F2, true, new View.OnClickListener() {
            public void onClick(View v) { callApp("com.example.socialfeed.SocialFeedApp", "init", "showFeed"); }
        }));
        grid.addView(row1);

        LinearLayout row2 = new LinearLayout(ctx);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(appTile(MaterialIcons.CALCULATE, "Huawei Calc", 0xFF6A1B9A, true, new View.OnClickListener() {
            public void onClick(View v) { callShimMethod("loadHuaweiCalculator"); }
        }));
        row2.addView(appTile(MaterialIcons.APPS, "Compose Test", 0xFFE91E63, true, new View.OnClickListener() {
            public void onClick(View v) { callApp("com.example.composetest.ComposeTestApp", "init", "testCompose"); }
        }));
        View sp1 = new View(ctx); sp1.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1));
        row2.addView(sp1);
        grid.addView(row2);

        // Real APKs section
        grid.addView(sectionLabel("Installed Apps (Real APKs)"));

        // Real APK entries: {package, activity, name, materialIcon, color, isMaterialIcon}
        Object[][] realApps = {
            {"com.huawei.calculator", "com.huawei.calculator.Calculator", "Calculator", MaterialIcons.CALCULATE, 0xFF4CAF50},
            {"com.android.deskclock", "com.android.deskclock.AlarmsMainActivity", "Clock", MaterialIcons.ALARM, 0xFF2196F3},
            {"com.huawei.filemanager", "com.huawei.filemanager.activities.MainActivity", "Files", MaterialIcons.FOLDER, 0xFFFF9800},
            {"com.android.settings", "com.android.settings.HWSettings", "Settings", MaterialIcons.SETTINGS, 0xFF607D8B},
            {"com.android.calendar", "com.android.calendar.AllInOneActivity", "Calendar", MaterialIcons.CALENDAR, 0xFFE91E63},
            {"com.brave.browser", "com.brave.browser.BraveActivity", "Brave", MaterialIcons.WEB, 0xFFFF5722},
        };

        for (int i = 0; i < realApps.length; i += 3) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = i; j < Math.min(i + 3, realApps.length); j++) {
                final String pkg = (String) realApps[j][0];
                final String act = (String) realApps[j][1];
                final String name = (String) realApps[j][2];
                String icon = (String) realApps[j][3];
                int color = (Integer) realApps[j][4];
                row.addView(appTile(icon, name, color, true, new View.OnClickListener() {
                    public void onClick(View v) { launchRealApp(pkg, act, name); }
                }));
            }
            for (int j = Math.min(i + 3, realApps.length) - i; j < 3; j++) {
                View spacer = new View(ctx);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1));
                row.addView(spacer);
            }
            grid.addView(row);
        }

        scroll.addView(grid);
        root.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // Bottom dock
        LinearLayout dock = new LinearLayout(ctx);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setBackgroundColor(0x33FFFFFF);
        dock.setPadding(dp(16), dp(8), dp(16), dp(8));
        dock.setGravity(Gravity.CENTER);

        dock.addView(dockIcon(MaterialIcons.PHONE, true, new View.OnClickListener() {
            public void onClick(View v) { callApp("com.example.dialer.DialerEntry", null, "launch"); }
        }));
        dock.addView(dockIcon(MaterialIcons.CHAT, true, null));
        dock.addView(dockIcon(MaterialIcons.CAMERA, true, null));
        dock.addView(dockIcon(MaterialIcons.SETTINGS, true, new View.OnClickListener() {
            public void onClick(View v) { launchRealApp("com.android.settings", "com.android.settings.HWSettings", "Settings"); }
        }));

        root.addView(dock);

        show(root);
    }

    static View sectionLabel(String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(0x99FFFFFF);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(dp(8), dp(16), dp(8), dp(8));
        tv.setLetterSpacing(0.05f);
        return tv;
    }

    static View appTile(String icon, String name, int color, boolean material, View.OnClickListener listener) {
        LinearLayout tile = new LinearLayout(ctx);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(8), dp(12), dp(8), dp(12));

        LinearLayout iconBg = new LinearLayout(ctx);
        iconBg.setGravity(Gravity.CENTER);
        GradientDrawable iconShape = new GradientDrawable();
        iconShape.setColor(color);
        iconShape.setCornerRadius(dp(16));
        iconBg.setBackground(iconShape);
        iconBg.setElevation(dp(4));

        TextView iconTv = new TextView(ctx);
        iconTv.setText(icon);
        iconTv.setTextSize(material ? 30 : 28);
        iconTv.setTextColor(Color.WHITE);
        iconTv.setGravity(Gravity.CENTER);
        if (material) iconTv.setTypeface(MaterialIcons.getFont());
        iconBg.addView(iconTv);
        iconBg.setLayoutParams(new LinearLayout.LayoutParams(dp(60), dp(60)));
        tile.addView(iconBg);

        TextView nameTv = new TextView(ctx);
        nameTv.setText(name);
        nameTv.setTextSize(11);
        nameTv.setTextColor(0xDDFFFFFF);
        nameTv.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        nameTv.setGravity(Gravity.CENTER);
        nameTv.setPadding(0, dp(6), 0, 0);
        nameTv.setMaxLines(1);
        tile.addView(nameTv);

        tile.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        if (listener != null) tile.setOnClickListener(listener);

        return tile;
    }

    static View dockIcon(String icon, boolean material, View.OnClickListener listener) {
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x44FFFFFF);
        bg.setCornerRadius(dp(14));
        wrapper.setBackground(bg);
        TextView tv = new TextView(ctx);
        tv.setText(icon);
        tv.setTextSize(material ? 26 : 24);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        if (material) tv.setTypeface(MaterialIcons.getFont());
        wrapper.addView(tv);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(52), dp(52));
        lp.setMargins(dp(12), dp(4), dp(12), dp(4));
        wrapper.setLayoutParams(lp);
        if (listener != null) wrapper.setOnClickListener(listener);
        return wrapper;
    }

    /**
     * Show list of installed apps that can be launched.
     */
    public static void showAppList() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(0xFF3F51B5);
        header.setPadding(dp(8), dp(10), dp(16), dp(10));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        Button back = new Button(ctx);
        back.setText("\u2190");
        back.setTextSize(20);
        back.setTextColor(Color.WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                com.example.mockdonalds.MockApp.showMenu();
            }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));

        TextView title = new TextView(ctx);
        title.setText("APK Launcher — Real Apps");
        title.setTextSize(18);
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(title);
        root.addView(header);

        // Info
        TextView info = new TextView(ctx);
        info.setText("Launch real installed APKs inside Westlake engine.\nTap an app to run its main Activity.");
        info.setTextSize(13);
        info.setTextColor(0xFF757575);
        info.setPadding(dp(16), dp(12), dp(16), dp(8));
        info.setBackgroundColor(Color.WHITE);
        root.addView(info);

        // App list
        ScrollView scroll = new ScrollView(ctx);
        LinearLayout list = new LinearLayout(ctx);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(8), dp(8), dp(8), dp(8));

        // Apps we know are on the phone
        String[][] apps = {
            {"com.huawei.calculator", "com.huawei.calculator.Calculator", "Calculator", "\uD83E\uDDEE", "#4CAF50"},
            {"com.android.deskclock", "com.android.deskclock.AlarmsMainActivity", "Clock", "\u23F0", "#2196F3"},
            {"com.huawei.filemanager", "com.huawei.filemanager.activities.MainActivity", "Files", "\uD83D\uDCC1", "#FF9800"},
            {"com.android.settings", "com.android.settings.HWSettings", "Settings", "\u2699\uFE0F", "#607D8B"},
            {"com.android.calendar", "com.android.calendar.AllInOneActivity", "Calendar", "\uD83D\uDCC5", "#E91E63"},
        };

        for (final String[] app : apps) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dp(14), dp(14), dp(14), dp(14));
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundColor(Color.WHITE);
            row.setElevation(dp(1));
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.setMargins(dp(4), dp(3), dp(4), dp(3));
            row.setLayoutParams(rlp);

            // Icon
            int appColor = (int)Long.parseLong(app[4].substring(1), 16) | 0xFF000000;
            LinearLayout icon = new LinearLayout(ctx);
            icon.setGravity(Gravity.CENTER);
            GradientDrawable iconBg = new GradientDrawable();
            iconBg.setShape(GradientDrawable.OVAL);
            iconBg.setColor(appColor);
            icon.setBackground(iconBg);
            TextView iconEmoji = new TextView(ctx);
            iconEmoji.setText(app[3]);
            iconEmoji.setTextSize(22);
            iconEmoji.setGravity(Gravity.CENTER);
            icon.addView(iconEmoji);
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(dp(48), dp(48));
            ilp.rightMargin = dp(14);
            icon.setLayoutParams(ilp);
            row.addView(icon);

            // Name + package
            LinearLayout nameCol = new LinearLayout(ctx);
            nameCol.setOrientation(LinearLayout.VERTICAL);
            TextView nameTv = new TextView(ctx);
            nameTv.setText(app[2]);
            nameTv.setTextSize(16);
            nameTv.setTextColor(0xFF212121);
            nameTv.setTypeface(Typeface.DEFAULT_BOLD);
            nameCol.addView(nameTv);
            TextView pkgTv = new TextView(ctx);
            pkgTv.setText(app[0]);
            pkgTv.setTextSize(12);
            pkgTv.setTextColor(0xFF757575);
            nameCol.addView(pkgTv);
            row.addView(nameCol, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // Launch arrow
            TextView arrow = new TextView(ctx);
            arrow.setText("\u25B6");
            arrow.setTextSize(18);
            arrow.setTextColor(appColor);
            row.addView(arrow);

            final String pkgName = app[0];
            final String activityName = app[1];
            final String appName = app[2];

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchRealApp(pkgName, activityName, appName);
                }
            });

            list.addView(row);
        }

        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        show(root);
    }

    /**
     * Launch a real APK's Activity by:
     * 1. Creating a package Context for the APK
     * 2. Loading the Activity class from the APK's classloader
     * 3. Creating an instance and calling lifecycle methods
     * 4. Grabbing its content view
     */
    static void launchRealApp(final String packageName, final String activityClass, final String appName) {
        System.out.println("[ApkLauncher] Launching: " + packageName + "/" + activityClass);

        LinearLayout status = new LinearLayout(ctx);
        status.setOrientation(LinearLayout.VERTICAL);
        status.setBackgroundColor(Color.WHITE);
        status.setPadding(dp(20), dp(40), dp(20), dp(20));

        // Header with back button
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(16));

        Button back = new Button(ctx);
        back.setText("\u2190 Back");
        back.setTextSize(14);
        back.setTextColor(0xFF3F51B5);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showAppList(); }
        });
        header.addView(back);
        status.addView(header);

        TextView titleTv = new TextView(ctx);
        titleTv.setText("Launching " + appName + "...");
        titleTv.setTextSize(20);
        titleTv.setTextColor(0xFF212121);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        status.addView(titleTv);

        show(status);

        // Do the actual launch on the UI thread after a short delay
        hostActivity.getWindow().getDecorView().postDelayed(new Runnable() {
            public void run() {
                doLaunch(packageName, activityClass, appName);
            }
        }, 500);
    }

    static void doLaunch(String packageName, String activityClass, String appName) {
        final LinearLayout result = new LinearLayout(ctx);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setBackgroundColor(Color.WHITE);
        result.setPadding(dp(16), dp(16), dp(16), dp(16));

        // Back button
        Button back = new Button(ctx);
        back.setText("\u2190 Back to App List");
        back.setTextSize(14);
        back.setTextColor(Color.WHITE);
        back.setBackground(roundRect(0xFF3F51B5, 8));
        back.setPadding(dp(16), dp(10), dp(16), dp(10));
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showAppList(); }
        });
        result.addView(back);

        TextView titleTv = new TextView(ctx);
        titleTv.setText(appName + " — Real APK");
        titleTv.setTextSize(18);
        titleTv.setTextColor(0xFF212121);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setPadding(0, dp(12), 0, dp(8));
        result.addView(titleTv);

        try {
            // Step 1: Get the APK's Context
            addStatus(result, "\u2705", "Creating package context for " + packageName);
            Context apkContext = ctx.createPackageContext(packageName,
                Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            addStatus(result, "\u2705", "Package context created");

            // Step 2: Get the APK's ClassLoader
            ClassLoader apkClassLoader = apkContext.getClassLoader();
            addStatus(result, "\u2705", "ClassLoader: " + apkClassLoader.getClass().getSimpleName());

            // Step 3: Load the Activity class
            addStatus(result, "\u23F3", "Loading class: " + activityClass);
            Class<?> actClass = apkClassLoader.loadClass(activityClass);
            addStatus(result, "\u2705", "Class loaded: " + actClass.getSimpleName());
            addStatus(result, "\u2139\uFE0F", "Superclass: " + actClass.getSuperclass().getName());

            // Step 4: Get resources info
            Resources apkRes = apkContext.getResources();
            addStatus(result, "\u2705", "Resources loaded (density=" +
                apkRes.getDisplayMetrics().density + ")");

            // Step 5: Launch via Intent
            addStatus(result, "\u23F3", "Launching Activity...");

            // We need to use the real phone Activity framework for this
            // The cleanest way: use an Intent to start it properly
            addStatus(result, "\u2139\uFE0F", "Starting via Intent...");

            android.content.Intent intent = new android.content.Intent();
            intent.setClassName(packageName, activityClass);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);

            addStatus(result, "\u2705", "Activity started via startActivity()!");
            addStatus(result, "\u2705", appName + " is running in its own window");

            // Add note
            TextView note = new TextView(ctx);
            note.setText("\n\u2139\uFE0F The app is running as a real Android Activity.\n" +
                "Press the phone's Back button to return here.");
            note.setTextSize(14);
            note.setTextColor(0xFF1565C0);
            note.setPadding(0, dp(12), 0, 0);
            result.addView(note);

        } catch (Exception e) {
            System.out.println("[ApkLauncher] Error: " + e);
            e.printStackTrace();
            addStatus(result, "\u274C", "Error: " + e.getClass().getSimpleName());
            addStatus(result, "\u274C", "" + e.getMessage());

            // If startActivity failed, try direct instantiation as fallback
            addStatus(result, "\u23F3", "Trying direct instantiation fallback...");
            try {
                Context apkContext = ctx.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                ClassLoader apkCL = apkContext.getClassLoader();
                Class<?> actClass = apkCL.loadClass(activityClass);

                // Create instance
                Object activity = actClass.newInstance();
                addStatus(result, "\u2705", "Instance created: " + activity.getClass().getName());

                // Try calling onCreate
                Method onCreate = findMethod(actClass, "onCreate", Bundle.class);
                if (onCreate != null) {
                    onCreate.setAccessible(true);
                    onCreate.invoke(activity, (Bundle) null);
                    addStatus(result, "\u2705", "onCreate() called");
                }

                // Try getting the content view
                Method getWindow = findMethod(actClass, "getWindow");
                if (getWindow != null) {
                    getWindow.setAccessible(true);
                    Object window = getWindow.invoke(activity);
                    if (window != null) {
                        Method getDecorView = window.getClass().getMethod("getDecorView");
                        View decorView = (View) getDecorView.invoke(window);
                        if (decorView != null) {
                            addStatus(result, "\u2705", "DecorView: " + decorView.getClass().getSimpleName());
                            // Try to add it
                            if (decorView.getParent() != null)
                                ((ViewGroup) decorView.getParent()).removeView(decorView);
                            result.addView(decorView, new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                        }
                    }
                }
            } catch (Exception e2) {
                addStatus(result, "\u274C", "Fallback error: " + e2.getClass().getSimpleName());
                addStatus(result, "\u274C", "" + e2.getMessage());
            }
        }

        show(result);
    }

    /**
     * Call XmlTestHelper methods via reflection to cross the classloader boundary.
     * The engine thread loads classes via child-first DexClassLoader, but UI clicks
     * run on the main thread with the phone's default classloader.
     */
    /**
     * Launch a custom app via reflection to cross classloader boundary.
     * @param className fully qualified class name
     * @param initMethod method to call with Context param (or null to skip)
     * @param showMethod method to call with Context param (or no-arg)
     */
    static void callApp(String className, String initMethod, String showMethod) {
        try {
            ClassLoader cl = ApkLauncher.class.getClassLoader();
            Class<?> appClass = cl.loadClass(className);
            if (initMethod != null) {
                try {
                    Method init = appClass.getMethod(initMethod, android.content.Context.class);
                    init.invoke(null, ctx);
                } catch (NoSuchMethodException e) {}
            }
            try {
                Method show = appClass.getMethod(showMethod, android.content.Context.class);
                show.invoke(null, ctx);
            } catch (NoSuchMethodException e) {
                Method show = appClass.getMethod(showMethod);
                show.invoke(null);
            }
        } catch (Exception e) {
            System.out.println("[ApkLauncher] callApp " + className + " error: " + e);
            e.printStackTrace();
        }
    }

    static void callShimMethod(String methodName) {
        try {
            // Get the engine's classloader from WestlakeActivity
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            // The child-first classloader is set as context classloader on engine thread
            // But we can find XmlTestHelper via the DexClassLoader that loaded our app.dex
            ClassLoader cl = ApkLauncher.class.getClassLoader();
            Class<?> xmlHelper = cl.loadClass("com.example.mockdonalds.XmlTestHelper");
            Method m = xmlHelper.getMethod(methodName, android.content.Context.class);
            View result = (View) m.invoke(null, ctx);
            if (result != null) show(result);
        } catch (Exception e) {
            System.out.println("[ApkLauncher] callShimMethod " + methodName + " error: " + e);
            e.printStackTrace();
            // Show error in UI
            TextView err = new TextView(ctx);
            err.setText("Error loading: " + e.getMessage());
            err.setTextSize(14);
            err.setTextColor(0xFFFF0000);
            err.setPadding(dp(16), dp(32), dp(16), dp(16));
            show(err);
        }
    }

    static Method findMethod(Class<?> clazz, String name, Class<?>... params) {
        try { return clazz.getDeclaredMethod(name, params); }
        catch (NoSuchMethodException e) {
            try { return clazz.getMethod(name, params); }
            catch (NoSuchMethodException e2) { return null; }
        }
    }

    static void addStatus(LinearLayout parent, String icon, String text) {
        System.out.println("[ApkLauncher] " + icon + " " + text);
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(4), 0, dp(4));

        TextView iconTv = new TextView(parent.getContext());
        iconTv.setText(icon + " ");
        iconTv.setTextSize(14);
        row.addView(iconTv);

        TextView textTv = new TextView(parent.getContext());
        textTv.setText(text);
        textTv.setTextSize(13);
        textTv.setTextColor(0xFF424242);
        row.addView(textTv);

        parent.addView(row);
    }
}
