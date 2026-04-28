package android.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * BinaryXmlParser — parses Android's AXML binary XML format.
 *
 * Android's AndroidManifest.xml inside APKs is stored in a binary format (AXML).
 * This parser extracts the essential information needed to launch an app:
 * package name, activities, services, permissions, SDK versions, and launcher activity.
 *
 * AXML format overview:
 * - Header: magic (0x00080003), file size
 * - String pool chunk (type 0x0001): all strings referenced by the XML
 * - Resource ID chunk (type 0x0180): Android resource IDs for attribute names
 * - XML tree: StartNamespace, StartElement, EndElement, EndNamespace chunks
 */
public class BinaryXmlParser {

    // Chunk types
    private static final int CHUNK_AXML_FILE   = 0x00080003;
    private static final int CHUNK_STRING_POOL  = 0x001C0001;
    private static final int CHUNK_RESOURCE_IDS = 0x00080180;
    private static final int CHUNK_START_NS     = 0x00100100;
    private static final int CHUNK_END_NS       = 0x00100101;
    private static final int CHUNK_START_ELEMENT= 0x00100102;
    private static final int CHUNK_END_ELEMENT  = 0x00100103;

    // Android resource IDs for common attributes
    private static final int ATTR_PACKAGE       = 0x01010003; // android:name used on <manifest> is "package"
    private static final int ATTR_NAME          = 0x01010003;
    private static final int ATTR_VERSION_CODE  = 0x0101021B;
    private static final int ATTR_VERSION_NAME  = 0x0101021C;
    private static final int ATTR_MIN_SDK       = 0x0101020C;
    private static final int ATTR_TARGET_SDK    = 0x01010270;
    private static final int ATTR_APP_COMPONENT_FACTORY = 0x0101057A;

    // Well-known action/category strings
    private static final String ACTION_MAIN     = "android.intent.action.MAIN";
    private static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";

    private String[] stringPool;
    private int[] resourceIds;

    /**
     * Parse binary XML from an InputStream and populate the ApkInfo.
     */
    public void parse(InputStream input, ApkInfo info) throws IOException {
        byte[] data = readAll(input);
        parse(data, info);
    }

    /**
     * Parse binary XML from a byte array and populate the ApkInfo.
     */
    public void parse(byte[] data, ApkInfo info) {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        int magic = buf.getInt();
        if (magic != CHUNK_AXML_FILE) {
            throw new IllegalArgumentException(
                    "Not an AXML file: magic=0x" + Integer.toHexString(magic));
        }
        int fileSize = buf.getInt();

        // Track element context for intent-filter parsing
        String currentElement = null;
        String currentActivityName = null;
        String currentAliasTarget = null;  // for <activity-alias targetActivity="...">
        String currentPackageName = null;
        boolean inIntentFilter = false;
        boolean hasMainAction = false;
        boolean hasLauncherCategory = false;

        while (buf.position() < data.length) {
            if (buf.remaining() < 8) break;
            int chunkType = buf.getInt();
            int chunkSize = buf.getInt();
            int chunkStart = buf.position() - 8;

            switch (chunkType) {
                case CHUNK_STRING_POOL:
                    parseStringPool(buf, chunkStart, chunkSize);
                    break;

                case CHUNK_RESOURCE_IDS:
                    int count = (chunkSize - 8) / 4;
                    resourceIds = new int[count];
                    for (int i = 0; i < count; i++) {
                        resourceIds[i] = buf.getInt();
                    }
                    break;

                case CHUNK_START_ELEMENT: {
                    // line, comment, ns, name, attrStart, attrSize, attrCount, idIndex, classIndex, styleIndex
                    int line = buf.getInt();
                    int comment = buf.getInt();
                    int ns = buf.getInt();
                    int name = buf.getInt();
                    int attrStart = buf.getShort() & 0xFFFF;
                    int attrSize = buf.getShort() & 0xFFFF;
                    int attrCount = buf.getShort() & 0xFFFF;
                    int idIndex = buf.getShort() & 0xFFFF;
                    int classIndex = buf.getShort() & 0xFFFF;
                    int styleIndex = buf.getShort() & 0xFFFF;

                    String elemName = getString(name);
                    currentElement = elemName;

                    if ("intent-filter".equals(elemName)) {
                        inIntentFilter = true;
                        hasMainAction = false;
                        hasLauncherCategory = false;
                    }

                    if ("activity-alias".equals(elemName)) {
                        currentAliasTarget = null;
                    }

                    // Parse attributes
                    for (int i = 0; i < attrCount; i++) {
                        int attrNs = buf.getInt();
                        int attrName = buf.getInt();
                        int attrRawValue = buf.getInt();
                        int attrTypedValueSize = buf.getShort() & 0xFFFF;
                        int attrTypedValueRes0 = buf.get() & 0xFF;
                        int attrType = buf.get() & 0xFF;
                        int attrData = buf.getInt();

                        String attrNameStr = getString(attrName);
                        int resId = getResourceId(attrName);
                        String attrValueStr = (attrRawValue >= 0) ? getString(attrRawValue) : null;

                        processAttribute(info, elemName, attrNameStr, resId,
                                attrValueStr, attrType, attrData);

                        // Track for launcher detection
                        if ("manifest".equals(elemName) && "package".equals(attrNameStr)) {
                            currentPackageName = attrValueStr;
                        }
                        if ("activity".equals(elemName) && "name".equals(attrNameStr)) {
                            currentActivityName = attrValueStr;
                        }
                        // Track activity-alias: use targetActivity as the activity name
                        // and also track the alias name via "name" attribute
                        if ("activity-alias".equals(elemName)) {
                            if ("targetActivity".equals(attrNameStr) && attrValueStr != null) {
                                currentAliasTarget = attrValueStr;
                            }
                            if ("name".equals(attrNameStr) && attrValueStr != null) {
                                currentActivityName = attrValueStr;
                            }
                        }
                        if (inIntentFilter && "action".equals(elemName)
                                && "name".equals(attrNameStr)
                                && ACTION_MAIN.equals(attrValueStr)) {
                            hasMainAction = true;
                        }
                        if (inIntentFilter && "category".equals(elemName)
                                && "name".equals(attrNameStr)
                                && CATEGORY_LAUNCHER.equals(attrValueStr)) {
                            hasLauncherCategory = true;
                        }
                    }
                    break;
                }

                case CHUNK_END_ELEMENT: {
                    int line2 = buf.getInt();
                    int comment2 = buf.getInt();
                    int ns2 = buf.getInt();
                    int name2 = buf.getInt();

                    String endName = getString(name2);

                    if ("intent-filter".equals(endName)) {
                        if (hasMainAction && hasLauncherCategory) {
                            // For activity-alias, prefer the targetActivity as the launcher
                            // since that's the actual Activity class to instantiate
                            String launcherName = currentAliasTarget != null
                                    ? currentAliasTarget : currentActivityName;
                            if (launcherName != null) {
                                String fullName = resolveClassName(launcherName, currentPackageName);
                                info.launcherActivity = fullName;
                            }
                        }
                        inIntentFilter = false;
                    }
                    if ("activity".equals(endName)) {
                        currentActivityName = null;
                    }
                    if ("activity-alias".equals(endName)) {
                        currentActivityName = null;
                        currentAliasTarget = null;
                    }
                    break;
                }

                case CHUNK_START_NS:
                case CHUNK_END_NS:
                    // Skip namespace chunks — 4 ints after header
                    buf.position(chunkStart + chunkSize);
                    break;

                default:
                    // Unknown chunk — skip
                    buf.position(chunkStart + chunkSize);
                    break;
            }
        }
    }

    private void processAttribute(ApkInfo info, String element, String attrName,
                                   int resId, String rawValue, int type, int data) {
        if ("manifest".equals(element)) {
            if ("package".equals(attrName)) {
                info.packageName = rawValue;
            } else if (resId == ATTR_VERSION_CODE || "versionCode".equals(attrName)) {
                info.versionCode = data;
            } else if ("versionName".equals(attrName)) {
                info.versionName = rawValue;
            }
        } else if ("uses-sdk".equals(element)) {
            if (resId == ATTR_MIN_SDK || "minSdkVersion".equals(attrName)) {
                info.minSdkVersion = data;
            } else if (resId == ATTR_TARGET_SDK || "targetSdkVersion".equals(attrName)) {
                info.targetSdkVersion = data;
            }
        } else if ("activity".equals(element) && "name".equals(attrName)) {
            String fullName = resolveClassName(rawValue, info.packageName);
            if (!info.activities.contains(fullName)) {
                info.activities.add(fullName);
            }
        } else if ("service".equals(element) && "name".equals(attrName)) {
            String fullName = resolveClassName(rawValue, info.packageName);
            if (!info.services.contains(fullName)) {
                info.services.add(fullName);
            }
        } else if ("application".equals(element) && "name".equals(attrName)) {
            if (rawValue != null) {
                info.applicationClassName = resolveClassName(rawValue, info.packageName);
            }
        } else if ("application".equals(element)
                && (resId == ATTR_APP_COMPONENT_FACTORY
                        || "appComponentFactory".equals(attrName))) {
            if (rawValue != null) {
                info.appComponentFactoryClassName =
                        resolveClassName(rawValue, info.packageName);
            }
        } else if ("uses-permission".equals(element) && "name".equals(attrName)) {
            if (rawValue != null && !info.permissions.contains(rawValue)) {
                info.permissions.add(rawValue);
            }
        }
    }

    private String resolveClassName(String name, String packageName) {
        if (name == null) return null;
        if (name.startsWith(".") && packageName != null) {
            return packageName + name;
        }
        if (!name.contains(".") && packageName != null) {
            return packageName + "." + name;
        }
        return name;
    }

    private void parseStringPool(ByteBuffer buf, int chunkStart, int chunkSize) {
        int stringCount = buf.getInt();
        int styleCount = buf.getInt();
        int flags = buf.getInt();
        int stringsStart = buf.getInt();
        int stylesStart = buf.getInt();

        boolean isUtf8 = (flags & (1 << 8)) != 0;

        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = buf.getInt();
        }
        // Skip style offsets
        for (int i = 0; i < styleCount; i++) {
            buf.getInt();
        }

        // stringsStart is relative to the chunk start (already includes the
        // 8-byte chunk header), so do NOT add 8 again.
        int dataStart = chunkStart + stringsStart;
        stringPool = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int pos = dataStart + offsets[i];
            buf.position(pos);
            if (isUtf8) {
                int charLen = buf.get() & 0xFF;
                if ((charLen & 0x80) != 0) {
                    charLen = ((charLen & 0x7F) << 8) | (buf.get() & 0xFF);
                }
                int byteLen = buf.get() & 0xFF;
                if ((byteLen & 0x80) != 0) {
                    byteLen = ((byteLen & 0x7F) << 8) | (buf.get() & 0xFF);
                }
                byte[] strBytes = new byte[byteLen];
                buf.get(strBytes);
                try {
                    stringPool[i] = new String(strBytes, "UTF-8");
                } catch (Exception e) {
                    stringPool[i] = "";
                }
            } else {
                int charLen = buf.getShort() & 0xFFFF;
                if ((charLen & 0x8000) != 0) {
                    charLen = ((charLen & 0x7FFF) << 16) | (buf.getShort() & 0xFFFF);
                }
                char[] chars = new char[charLen];
                for (int j = 0; j < charLen; j++) {
                    chars[j] = (char) (buf.getShort() & 0xFFFF);
                }
                stringPool[i] = new String(chars);
            }
        }

        // Position past the entire chunk
        buf.position(chunkStart + chunkSize);
    }

    private String getString(int index) {
        if (stringPool == null || index < 0 || index >= stringPool.length) return null;
        return stringPool[index];
    }

    private int getResourceId(int index) {
        if (resourceIds == null || index < 0 || index >= resourceIds.length) return 0;
        return resourceIds[index];
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        byte[] tmp = new byte[4096];
        int n;
        while ((n = in.read(tmp)) != -1) bos.write(tmp, 0, n);
        return bos.toByteArray();
    }
}
