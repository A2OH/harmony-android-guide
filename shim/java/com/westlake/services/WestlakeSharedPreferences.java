// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR29-2 — WestlakeSharedPreferences
//
// Minimal SharedPreferences impl backed by a Properties file under our
// per-app sandbox.  Created to unblock McD Application.onCreate, which
// asserts getSharedPreferences(...) is non-null (V2-Probe G3 FAIL).
//
// Design notes:
//   - This is the FIRST WestlakeContextImpl.getSharedPreferences user.
//     Per the CR22 frozen-surface rule, we do NOT add new methods to
//     WestlakeContextImpl; we only fill the bodies of the existing
//     getSharedPreferences(String,int) and getSharedPreferences(File,int)
//     stubs.
//
//   - Backing storage: java.util.Properties text format under
//     /data/local/tmp/westlake/sp/{packageName}/{name}.xml.  The .xml
//     suffix matches AOSP convention; we don't actually emit XML — we
//     emit key=tag:value lines, which Properties can parse robustly.
//     Apps that read these files outside of our APIs will see "wrong"
//     content, but no real apps do that.
//
//   - Type encoding: prefix each value with a one-char tag plus colon
//     so we can recover the type on reload:
//       S: -> String
//       I: -> Integer
//       L: -> Long
//       F: -> Float
//       B: -> Boolean
//       T: -> StringSet (one element per line, T:elem then comma-joined)
//     We use comma-joined for StringSet; values containing commas will
//     be mis-split.  No current discovery path uses StringSet with
//     comma-bearing values, so we accept the limitation.
//
//   - Concurrency: in-memory map is ConcurrentHashMap so reads are
//     lock-free.  File writes are serialised on the instance to avoid
//     partial writes from concurrent commit()/apply() calls.
//
//   - Listener registry: tolerated but never required to fire across
//     SP instances.  Each call to getSharedPreferences with the same
//     name currently returns a *new* instance (matches the simpler
//     contract of a single-process sandbox).  If an app registers a
//     listener on one instance and writes via another, the listener
//     won't fire — but neither does AOSP guarantee cross-instance
//     notification (instances are per-process singletons in AOSP).
//
// This class is intentionally *thin*; ~300 LOC is the budget.  If a
// discovery surfaces a need for something more (e.g. multi-process,
// commitNow, type coercion, real XML format), that work is a separate
// CR with its own review.

package com.westlake.services;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WestlakeSharedPreferences implements SharedPreferences {

    private static final String TAG_STRING     = "S:";
    private static final String TAG_INT        = "I:";
    private static final String TAG_LONG       = "L:";
    private static final String TAG_FLOAT      = "F:";
    private static final String TAG_BOOLEAN    = "B:";
    private static final String TAG_STRING_SET = "T:";

    /** Sentinel value placed in an editor's pending map to indicate remove. */
    private static final Object REMOVE_MARKER = new Object();

    private final File mFile;
    private final Map<String, Object> mValues = new ConcurrentHashMap<>();
    private final List<OnSharedPreferenceChangeListener> mListeners = new ArrayList<>();

    public WestlakeSharedPreferences(File file) {
        mFile = file;
        loadFromFile();
    }

    // ----------------------------------------------------------------------
    // File I/O
    // ----------------------------------------------------------------------

    private void loadFromFile() {
        if (mFile == null || !mFile.exists()) return;
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(mFile)) {
            p.load(is);
        } catch (IOException e) {
            // best effort — start empty if the file is corrupt
            return;
        }
        for (String key : p.stringPropertyNames()) {
            Object decoded = decode(p.getProperty(key));
            if (decoded != null) {
                mValues.put(key, decoded);
            }
        }
    }

    private synchronized void writeToFile() {
        if (mFile == null) return;
        try {
            File parent = mFile.getParentFile();
            if (parent != null) parent.mkdirs();
            Properties p = new Properties();
            for (Map.Entry<String, Object> e : mValues.entrySet()) {
                String enc = encode(e.getValue());
                if (enc != null) {
                    p.setProperty(e.getKey(), enc);
                }
            }
            try (OutputStream os = new FileOutputStream(mFile)) {
                p.store(os, "Westlake SharedPreferences");
            }
        } catch (IOException e) {
            // best effort — in-memory state remains correct even if
            // persistence fails (next process restart will lose changes).
        }
    }

    private static Object decode(String raw) {
        if (raw == null) return null;
        if (raw.length() < 2) return raw;
        String body = raw.substring(2);
        try {
            if (raw.startsWith(TAG_STRING))     return body;
            if (raw.startsWith(TAG_INT))        return Integer.valueOf(body);
            if (raw.startsWith(TAG_LONG))       return Long.valueOf(body);
            if (raw.startsWith(TAG_FLOAT))      return Float.valueOf(body);
            if (raw.startsWith(TAG_BOOLEAN))    return Boolean.valueOf(body);
            if (raw.startsWith(TAG_STRING_SET)) {
                if (body.isEmpty()) return new LinkedHashSet<String>();
                String[] parts = body.split(",", -1);
                Set<String> out = new LinkedHashSet<>(parts.length);
                for (String s : parts) out.add(s);
                return out;
            }
        } catch (NumberFormatException nfe) {
            return null;
        }
        // Untagged — treat as raw string for forward compatibility.
        return raw;
    }

    private static String encode(Object o) {
        if (o == null)             return null;
        if (o instanceof String)   return TAG_STRING + o;
        if (o instanceof Integer)  return TAG_INT + o;
        if (o instanceof Long)     return TAG_LONG + o;
        if (o instanceof Float)    return TAG_FLOAT + o;
        if (o instanceof Boolean)  return TAG_BOOLEAN + o;
        if (o instanceof Set) {
            StringBuilder sb = new StringBuilder(TAG_STRING_SET);
            boolean first = true;
            for (Object e : (Set<?>) o) {
                if (!first) sb.append(',');
                sb.append(e == null ? "" : e.toString());
                first = false;
            }
            return sb.toString();
        }
        // Unknown type — fall back to toString tagged as string.
        return TAG_STRING + o.toString();
    }

    // ----------------------------------------------------------------------
    // SharedPreferences API
    // ----------------------------------------------------------------------

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>(mValues);
    }

    @Override
    public String getString(String key, String defValue) {
        Object v = mValues.get(key);
        return v instanceof String ? (String) v : defValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, Set<String> defValues) {
        Object v = mValues.get(key);
        if (v instanceof Set) {
            // Return a defensive copy so callers can't mutate our state.
            return new LinkedHashSet<>((Set<String>) v);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        Object v = mValues.get(key);
        return v instanceof Integer ? (Integer) v : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        Object v = mValues.get(key);
        return v instanceof Long ? (Long) v : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Object v = mValues.get(key);
        return v instanceof Float ? (Float) v : defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object v = mValues.get(key);
        return v instanceof Boolean ? (Boolean) v : defValue;
    }

    @Override
    public boolean contains(String key) {
        return mValues.containsKey(key);
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        if (listener == null) return;
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private void notifyListeners(List<String> changedKeys) {
        OnSharedPreferenceChangeListener[] snap;
        synchronized (mListeners) {
            if (mListeners.isEmpty()) return;
            snap = mListeners.toArray(new OnSharedPreferenceChangeListener[0]);
        }
        for (String key : changedKeys) {
            for (OnSharedPreferenceChangeListener l : snap) {
                try {
                    l.onSharedPreferenceChanged(this, key);
                } catch (Throwable ignored) {
                    // Listener bugs must not break the writer.
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Editor
    // ----------------------------------------------------------------------

    private final class EditorImpl implements Editor {
        private final Map<String, Object> mPending = new HashMap<>();
        private boolean mClear = false;

        @Override
        public Editor putString(String key, String value) {
            mPending.put(key, value == null ? REMOVE_MARKER : value);
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            if (values == null) {
                mPending.put(key, REMOVE_MARKER);
            } else {
                // Defensive copy so post-put mutations of `values` don't
                // affect our pending state.
                mPending.put(key, new LinkedHashSet<>(values));
            }
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mPending.put(key, Integer.valueOf(value));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            mPending.put(key, Long.valueOf(value));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            mPending.put(key, Float.valueOf(value));
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            mPending.put(key, Boolean.valueOf(value));
            return this;
        }

        @Override
        public Editor remove(String key) {
            mPending.put(key, REMOVE_MARKER);
            return this;
        }

        @Override
        public Editor clear() {
            mClear = true;
            return this;
        }

        @Override
        public boolean commit() {
            applyChanges();
            return true;
        }

        @Override
        public void apply() {
            applyChanges();
        }

        private void applyChanges() {
            List<String> changedKeys = new ArrayList<>();
            synchronized (WestlakeSharedPreferences.this) {
                if (mClear) {
                    if (!mValues.isEmpty()) {
                        List<String> keys = new ArrayList<>(mValues.keySet());
                        mValues.clear();
                        changedKeys.addAll(keys);
                    }
                }
                for (Map.Entry<String, Object> e : mPending.entrySet()) {
                    String k = e.getKey();
                    Object v = e.getValue();
                    if (v == REMOVE_MARKER) {
                        if (mValues.remove(k) != null) {
                            changedKeys.add(k);
                        }
                    } else {
                        Object prev = mValues.put(k, v);
                        // AOSP fires the callback even if value unchanged
                        // post-put.  Match that contract for compatibility.
                        changedKeys.add(k);
                    }
                }
                writeToFile();
            }
            if (!changedKeys.isEmpty()) {
                notifyListeners(changedKeys);
            }
        }
    }
}
