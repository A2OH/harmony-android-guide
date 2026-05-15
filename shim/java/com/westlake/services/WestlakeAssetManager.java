// SPDX-License-Identifier: Apache-2.0
//
// V2-Step4 (2026-05-13) — WestlakeAssetManager
//
// Thin asset manager substitute. Reads from APK assets/ directory +
// resources.arsc via {@link ResourceArscParser}. Not a subclass of
// android.content.res.AssetManager (that class is final in framework
// API 30 and a different runtime type via framework_duplicates.txt
// stripping); this is the Westlake-owned counterpart that
// {@link WestlakeResources} composes over.
//
// The class is loosely API-compatible with android.content.res.AssetManager
// for the methods our test apps actually call (open, openXmlResourceParser,
// list, getString-equivalents) but NOT type-compatible — callers that
// need an android.content.res.AssetManager reference get null from
// {@link WestlakeResources#getAssets()} today (same behavior as the
// outgoing V1 plant path, which already returned a synthetic AM whose
// only usable surface was a sentinel mObject handle).
//
// Anti-patterns avoided per V2-Step4 brief:
//   - No Unsafe.allocateInstance.
//   - No Field.setAccessible.
//   - No per-app branches.

package com.westlake.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class WestlakeAssetManager {

    private final String mApkPath;
    private final ResourceArscParser.Parsed mParsed;

    public WestlakeAssetManager(String apkPath) throws IOException {
        if (apkPath == null || apkPath.isEmpty()) {
            throw new IOException("apkPath is null/empty");
        }
        this.mApkPath = apkPath;
        this.mParsed = ResourceArscParser.parseApk(apkPath);
    }

    public WestlakeAssetManager(ResourceArscParser.Parsed parsed, String apkPath) {
        this.mApkPath = apkPath;
        if (parsed == null) {
            // Build an empty stub so getString/getInteger etc. return null
            // defaults rather than NPE.
            try {
                this.mParsed = ResourceArscParser.parse(null);
            } catch (Throwable t) {
                throw new IllegalStateException("ResourceArscParser.parse(null) threw", t);
            }
        } else {
            this.mParsed = parsed;
        }
    }

    /** Underlying parsed arsc snapshot. */
    public ResourceArscParser.Parsed getParsed() { return mParsed; }

    /** APK file path on disk. */
    public String getApkPath() { return mApkPath; }

    // ─── assets/ access ─────────────────────────────────────────────

    /** Read an asset by relative path under assets/ in the APK. */
    public InputStream open(String fileName) throws IOException {
        if (fileName == null) throw new IOException("null filename");
        // Normalize: AssetManager.open("foo/bar.txt") reads "assets/foo/bar.txt".
        String key = fileName.startsWith("assets/")
                ? fileName.substring("assets/".length())
                : fileName;
        byte[] data = mParsed.assets.get(key);
        if (data == null) {
            throw new IOException("asset not found: " + key);
        }
        return new ByteArrayInputStream(data);
    }

    /**
     * List asset entries at a path. Returns the immediate children
     * (directories and files) of {@code path} relative to assets/.
     * An empty path lists the top of the assets tree.
     */
    public String[] list(String path) throws IOException {
        String prefix;
        if (path == null || path.isEmpty()) {
            prefix = "";
        } else {
            prefix = path.endsWith("/") ? path : path + "/";
        }
        HashSet<String> uniq = new HashSet<String>();
        for (Map.Entry<String, byte[]> e : mParsed.assets.entrySet()) {
            String name = e.getKey();
            if (!name.startsWith(prefix)) continue;
            String tail = name.substring(prefix.length());
            int slash = tail.indexOf('/');
            String child = (slash >= 0) ? tail.substring(0, slash) : tail;
            if (child.length() > 0) uniq.add(child);
        }
        return uniq.toArray(new String[uniq.size()]);
    }

    /**
     * Get an XML resource as an {@link InputStream} of binary AXML bytes.
     * Caller wraps with a parser (e.g. AndroidX LayoutInflater accepts
     * this via XmlResourceParser indirectly).
     */
    public InputStream openXmlResourceParser(int resId) throws IOException {
        byte[] blob = mParsed.xmlBlobs.get(Integer.valueOf(resId));
        if (blob == null) {
            throw new IOException("no xml for 0x" + Integer.toHexString(resId));
        }
        return new ByteArrayInputStream(blob);
    }

    /** Raw AXML bytes for an XML/layout resource (null if absent). */
    public byte[] getXmlBytes(int resId) {
        return mParsed.xmlBlobs.get(Integer.valueOf(resId));
    }

    // ─── resource value accessors ───────────────────────────────────

    public String getString(int resId) {
        return mParsed.getString(resId);
    }

    public CharSequence getText(int resId) {
        String s = mParsed.getString(resId);
        return s != null ? s : "";
    }

    public int getInteger(int resId, int defaultValue) {
        return mParsed.getInteger(resId, defaultValue);
    }

    public int getColor(int resId, int defaultValue) {
        return mParsed.getColor(resId, defaultValue);
    }

    public boolean getBoolean(int resId, boolean defaultValue) {
        return mParsed.getBoolean(resId, defaultValue);
    }

    public float getDimension(int resId, float density) {
        return mParsed.getDimension(resId, density);
    }

    public float getFloat(int resId, float defaultValue) {
        return mParsed.getFloat(resId, defaultValue);
    }

    public boolean hasValue(int resId) {
        return mParsed.hasValue(resId);
    }

    /** "type/name" → resource ID (or 0 if not found). */
    public int getIdentifier(String typeSlashName) {
        return mParsed.getIdentifier(typeSlashName);
    }

    /** Resource ID → "type/name". */
    public String getResourceName(int resId) {
        return mParsed.getResourceName(resId);
    }

    /** True if {@code resId} resolves to a layout/xml blob. */
    public boolean hasXml(int resId) {
        return mParsed.xmlBlobs.containsKey(Integer.valueOf(resId));
    }

    /** Total number of value entries (for diagnostics). */
    public int valueCount() {
        return mParsed.values.size();
    }

    /** Total number of xml blobs (for diagnostics). */
    public int xmlCount() {
        return mParsed.xmlBlobs.size();
    }

    /** Total number of asset entries (for diagnostics). */
    public int assetCount() {
        return mParsed.assets.size();
    }
}
