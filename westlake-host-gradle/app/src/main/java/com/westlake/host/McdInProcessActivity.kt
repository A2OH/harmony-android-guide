package com.westlake.host

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class McdInProcessActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val TAG = "McdInProcess"
        Log.i(TAG, "Starting in-process MCD loading")

        try {
            // Get MCD's context with code + resources
            val mcdCtx = createPackageContext("com.mcdonalds.app",
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            Log.i(TAG, "MCD context: ${mcdCtx.packageName}")

            // Get full resources including splits via PackageManager
            val fullRes = packageManager.getResourcesForApplication("com.mcdonalds.app")

            // Check the missing resource
            try {
                val name = fullRes.getResourceName(0x7f16008b)
                Log.i(TAG, "Resource 0x7f16008b = $name (type=${fullRes.getResourceTypeName(0x7f16008b)})")
            } catch (e: Exception) {
                Log.w(TAG, "0x7f16008b not found: ${e.message}")
            }

            // Apply MCD theme
            val themeId = fullRes.getIdentifier("AppTheme", "style", "com.mcdonalds.app")
            if (themeId != 0) theme.applyStyle(themeId, true)

            // Replace mcdCtx's Resources with fullRes via reflection so everything uses splits
            try {
                val mResField = android.content.ContextWrapper::class.java.getDeclaredField("mBase")
                mResField.isAccessible = true
                val baseCtx = mResField.get(mcdCtx)
                // ContextImpl has mResources field
                val resField = baseCtx.javaClass.getDeclaredField("mResources")
                resField.isAccessible = true
                resField.set(baseCtx, fullRes)
                Log.i(TAG, "Replaced mcdCtx resources with fullRes")
            } catch (e: Exception) {
                Log.w(TAG, "Resource replacement: ${e.message}")
            }

            val inflateCtx = android.view.ContextThemeWrapper(mcdCtx, themeId)
            val inflater = android.view.LayoutInflater.from(inflateCtx)

            // Try multiple layouts — skip splash (empty container), find ones with real content
            // Build a scrollable view with MCD's real drawables + any inflatable layouts
            val scroll = android.widget.ScrollView(inflateCtx)
            val root = android.widget.LinearLayout(inflateCtx)
            root.orientation = android.widget.LinearLayout.VERTICAL
            root.setBackgroundColor(0xFF27251F.toInt()) // MCD dark
            scroll.addView(root)

            // Add MCD header with arches
            val header = android.widget.LinearLayout(inflateCtx)
            header.orientation = android.widget.LinearLayout.VERTICAL
            header.setBackgroundColor(0xFFDA291C.toInt())
            header.setPadding(0, 60, 0, 60)
            header.gravity = android.view.Gravity.CENTER
            val archId = fullRes.getIdentifier("archus", "drawable", "com.mcdonalds.app")
            if (archId != 0) {
                val iv = android.widget.ImageView(inflateCtx)
                iv.setImageDrawable(fullRes.getDrawable(archId, theme))
                iv.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
                header.addView(iv, ViewGroup.LayoutParams(400, 200))
            }
            val title = android.widget.TextView(inflateCtx)
            title.text = "McDonald's — Westlake Engine"
            title.setTextColor(0xFFFFCC00.toInt())
            title.textSize = 20f
            title.gravity = android.view.Gravity.CENTER
            title.setPadding(0, 20, 0, 0)
            header.addView(title)
            root.addView(header)

            // Load real MCD drawables as cards
            val drawableNames = arrayOf(
                "splash_screen", "ic_home_selected", "ic_deals_selected",
                "ic_order_selected", "ic_rewards_selected", "ic_more_selected",
                "ic_home_unselected", "ic_deals_unselected", "ic_order_unselected",
                "back_chevron", "close", "ic_action_search", "ic_action_time",
                "ic_location", "ic_delivery", "ic_pickup", "ic_drive_thru"
            )
            val iconRow = android.widget.LinearLayout(inflateCtx)
            iconRow.orientation = android.widget.LinearLayout.HORIZONTAL
            iconRow.setPadding(20, 40, 20, 20)
            iconRow.gravity = android.view.Gravity.CENTER
            var iconsInRow = 0
            for (name in drawableNames) {
                val id = fullRes.getIdentifier(name, "drawable", "com.mcdonalds.app")
                if (id == 0) continue
                try {
                    val iv = android.widget.ImageView(inflateCtx)
                    iv.setImageDrawable(fullRes.getDrawable(id, theme))
                    iv.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                    iv.setPadding(16, 16, 16, 16)
                    val lp = android.widget.LinearLayout.LayoutParams(120, 120)
                    lp.setMargins(8, 8, 8, 8)
                    iconRow.addView(iv, lp)
                    iconsInRow++
                    if (iconsInRow >= 6) {
                        root.addView(iconRow)
                        val newRow = android.widget.LinearLayout(inflateCtx)
                        newRow.orientation = android.widget.LinearLayout.HORIZONTAL
                        newRow.setPadding(20, 0, 20, 0)
                        newRow.gravity = android.view.Gravity.CENTER
                        // Can't reassign iconRow, just add remaining to root
                        root.addView(newRow)
                        iconsInRow = 0
                    }
                } catch (e: Exception) { /* skip */ }
            }
            if (iconsInRow > 0) root.addView(iconRow)
            Log.i(TAG, "Added $iconsInRow MCD icons")

            // Try inflating real component layouts (not containers)
            var inflated = false
            val layoutNames = arrayOf(
                "item_menu_category", "menu_item_layout", "deal_card_layout",
                "offer_item", "restaurant_card", "order_item_layout",
                "navigation_bar", "mcd_toolbar", "bottom_nav"
            )
            for (name in layoutNames) {
                val id = fullRes.getIdentifier(name, "layout", "com.mcdonalds.app")
                if (id == 0) continue
                Log.i(TAG, "Trying '$name' (0x${Integer.toHexString(id)})...")
                try {
                    val v = inflater.inflate(id, null)
                    forceVisible(v)
                    if (v.background == null) v.setBackgroundColor(0xFFDA291C.toInt())
                    dumpView(v, 0, TAG)
                    setContentView(v)
                    inflated = true
                    Log.i(TAG, "INFLATED '$name' ON SCREEN!")
                    break
                } catch (e: Exception) {
                    Log.w(TAG, "  '$name': ${e.message?.take(100)}")
                }
            }

            if (!inflated) {
                // Try other MCD layouts that have real content
                val layouts = arrayOf(
                    "activity_home_dashboard", "activity_main", "activity_order",
                    "fragment_home_dashboard", "fragment_menu", "fragment_deals",
                    "fragment_account", "fragment_order", "bottom_navigation_layout",
                    "activity_deals", "fragment_restaurant_locator"
                )
                for (name in layouts) {
                    val id = fullRes.getIdentifier(name, "layout", "com.mcdonalds.app")
                    if (id != 0) {
                        Log.i(TAG, "Trying layout '$name' (0x${Integer.toHexString(id)})...")
                        try {
                            val v = inflater.inflate(id, null)
                            forceVisible(v)
                            setContentView(v)
                            inflated = true
                            Log.i(TAG, "INFLATED '$name'!")
                            dumpView(v, 0, TAG)
                            break
                        } catch (e: Exception) {
                            Log.w(TAG, "  '$name' failed: ${e.message?.take(80)}")
                        }
                    }
                }
            }

            // Now try the real MCD SplashActivity with full attach()
            // This works because am instrument gives us a registered process
            val label = android.widget.TextView(inflateCtx)
            label.setTextColor(0xAAFFFFFF.toInt())
            label.textSize = 12f
            label.setPadding(40, 20, 40, 20)

            try {
                val mcdCl = mcdCtx.classLoader
                val splashCls = mcdCl.loadClass(
                    "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity")
                val mcdActivity = splashCls.newInstance() as Activity

                // Use full Activity.attach() — this creates PhoneWindow, wires everything
                val atClass = Class.forName("android.app.ActivityThread")
                val sCurrentAT = atClass.getDeclaredField("sCurrentActivityThread")
                sCurrentAT.isAccessible = true
                val at = sCurrentAT.get(null)

                val attachMethod = Activity::class.java.declaredMethods.find {
                    it.name == "attach" && it.parameterCount >= 19
                }

                if (attachMethod != null && at != null) {
                    attachMethod.isAccessible = true
                    val ai = android.content.pm.ActivityInfo()
                    ai.packageName = "com.mcdonalds.app"
                    ai.name = "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity"
                    try {
                        val aiAppInfo = ai.javaClass.getField("applicationInfo")
                        aiAppInfo.set(ai, mcdCtx.applicationInfo)
                    } catch (_: Exception) {}
                    try { ai.javaClass.getField("theme").setInt(ai, themeId) } catch (_: Exception) {}

                    // Wrap context: MCD resources but OUR package name (for UID permission checks)
                    val attachCtx = object : android.content.ContextWrapper(inflateCtx) {
                        override fun getPackageName(): String = this@McdInProcessActivity.packageName
                        override fun getOpPackageName(): String = this@McdInProcessActivity.packageName
                        override fun getAttributionTag(): String? = null
                    }

                    val args = arrayOfNulls<Any>(attachMethod.parameterCount)
                    args[0] = attachCtx  // context with our package name but MCD resources
                    args[1] = at          // activityThread
                    args[2] = android.app.Instrumentation() // instrumentation
                    args[3] = android.os.Binder()  // token
                    args[4] = 0           // ident
                    args[5] = application // application
                    args[6] = intent      // intent
                    args[7] = ai          // activityInfo
                    args[8] = "McDonald's" // title

                    attachMethod.invoke(mcdActivity, *args)
                    Log.i(TAG, "MCD Activity.attach() OK! Window: ${mcdActivity.window}")

                    // Apply MCD theme
                    if (themeId != 0) mcdActivity.setTheme(themeId)

                    // Call onCreate
                    // Patch AppCompatDelegate to not query locales (SecurityException)
                    // Set the app's package name to OUR package so locale check passes
                    try {
                        val aiField = mcdActivity.javaClass.getMethod("getApplicationInfo")
                        val ai = aiField.invoke(mcdActivity) as android.content.pm.ApplicationInfo
                        ai.packageName = applicationInfo.packageName // use OUR package name for permission check
                        Log.i(TAG, "Patched ApplicationInfo.packageName to ${ai.packageName}")
                    } catch (e: Exception) {
                        Log.w(TAG, "Patch packageName: ${e.message}")
                    }

                    // Pre-set AppCompatDelegate's locale to avoid SecurityException
                    try {
                        val acdClass = Class.forName("androidx.appcompat.app.AppCompatDelegate")
                        // Set default locale so it doesn't query the system
                        val setLocales = acdClass.getDeclaredMethod("setApplicationLocales",
                            Class.forName("androidx.core.os.LocaleListCompat"))
                        val emptyLocales = Class.forName("androidx.core.os.LocaleListCompat")
                            .getDeclaredMethod("getEmptyLocaleList").invoke(null)
                        setLocales.invoke(null, emptyLocales)
                        Log.i(TAG, "Pre-set AppCompat locales to empty")
                    } catch (e: Exception) {
                        Log.w(TAG, "Locale pre-set: ${e.message}")
                    }

                    Log.i(TAG, "Calling MCD onCreate...")
                    val ocMethod = Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)
                    ocMethod.isAccessible = true
                    ocMethod.invoke(mcdActivity, null as Bundle?)
                    Log.i(TAG, "MCD onCreate returned!")

                    // Check content
                    val mcdWindow = mcdActivity.window
                    if (mcdWindow != null) {
                        val dv = mcdWindow.decorView
                        val content = dv.findViewById<ViewGroup>(android.R.id.content)
                        Log.i(TAG, "MCD content views: ${content?.childCount}")
                        if (content != null && content.childCount > 0) {
                            // MCD set content! Replace our view with MCD's
                            label.text = "MCD Activity running! Views: ${content.childCount}"
                            // Steal MCD's content view and put it in our activity
                            val mcdContent = content.getChildAt(0)
                            content.removeView(mcdContent)
                            root.addView(mcdContent, ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 800))
                            Log.i(TAG, "MCD content view transferred to host!")
                        } else {
                            label.text = "MCD attach+onCreate OK but no content view"
                        }
                    }
                } else {
                    label.text = "No attach method found (at=$at)"
                }
            } catch (e: Exception) {
                val root2 = if (e is java.lang.reflect.InvocationTargetException) e.cause ?: e else e
                Log.w(TAG, "MCD Activity error: ${root2.javaClass.simpleName}: ${root2.message}")
                label.text = "MCD: ${root2.javaClass.simpleName}\n${root2.message?.take(200)}"
            }

            root.addView(label)
            setContentView(scroll)
            Log.i(TAG, "Done")
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            val tv = android.widget.TextView(this)
            tv.text = "Error: ${e.message}"
            tv.textSize = 18f
            tv.setPadding(40, 40, 40, 40)
            setContentView(tv)
        }
    }

    private fun forceVisible(v: View) {
        v.visibility = View.VISIBLE
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) forceVisible(v.getChildAt(i))
        }
    }

    private fun dumpView(v: View, depth: Int, tag: String) {
        val indent = "  ".repeat(depth)
        Log.i(tag, "${indent}${v.javaClass.simpleName} bg=${v.background?.javaClass?.simpleName} vis=${v.visibility}")
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) dumpView(v.getChildAt(i), depth + 1, tag)
        }
    }
}
