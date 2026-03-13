package android.view.inspector;

import java.util.function.IntFunction;

/**
 * A2OH shim: android.view.inspector.PropertyMapper
 *
 * Maps view properties for the inspector framework.
 * All methods return 0 as stub defaults.
 */
public interface PropertyMapper {
    int mapBoolean(String name, int attributeId);
    int mapByte(String name, int attributeId);
    int mapChar(String name, int attributeId);
    int mapDouble(String name, int attributeId);
    int mapFloat(String name, int attributeId);
    int mapInt(String name, int attributeId);
    int mapLong(String name, int attributeId);
    int mapShort(String name, int attributeId);
    int mapObject(String name, int attributeId);
    int mapColor(String name, int attributeId);
    int mapGravity(String name, int attributeId);
    int mapIntEnum(String name, int attributeId, IntFunction<String> mapping);
    int mapIntFlag(String name, int attributeId, IntFunction<String> mapping);
    int mapResourceId(String name, int attributeId);
}
