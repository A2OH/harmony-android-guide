import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

public final class IcuProbe {
  private static void log(String message) throws Exception {
    String line = message + "\n";
    byte[] bytes = new byte[line.length()];
    for (int i = 0; i < line.length(); i++) {
      bytes[i] = (byte) line.charAt(i);
    }
    System.err.write(bytes);
    System.err.flush();
  }

  public static void main(String[] args) throws Exception {
    log("probe.prop=" + System.getProperty("android.icu.impl.ICUBinary.dataPath"));

    Class<?> icuDataClass = Class.forName("android.icu.impl.ICUData");
    Field baseNameField = icuDataClass.getDeclaredField("ICU_BASE_NAME");
    baseNameField.setAccessible(true);
    String baseName = (String) baseNameField.get(null);
    log("probe.icu_base=" + baseName);

    Class<?> uLocaleClass = Class.forName("android.icu.util.ULocale");
    Method forLocale = uLocaleClass.getDeclaredMethod("forLocale", java.util.Locale.class);
    Object locale = forLocale.invoke(null, java.util.Locale.US);
    log("probe.ulocale=" + locale);

    Class<?> icuBinaryClass = Class.forName("android.icu.impl.ICUBinary");
    Field filesField = icuBinaryClass.getDeclaredField("icuDataFiles");
    filesField.setAccessible(true);
    List<?> files = (List<?>) filesField.get(null);
    log("probe.files.size=" + files.size());
    for (Object file : files) {
      log("probe.file=" + file);
    }

    Method getData = icuBinaryClass.getDeclaredMethod("getData", String.class);
    getData.setAccessible(true);
    ByteBuffer data = (ByteBuffer) getData.invoke(null, "en_US.res");
    log("probe.en_US=" + (data == null ? "null" : data.remaining()));

    Class<?> bundleClass = Class.forName("android.icu.util.UResourceBundle");
    Method getBundleInstance = bundleClass.getDeclaredMethod(
        "getBundleInstance", String.class, String.class);
    Object bundle = getBundleInstance.invoke(null, baseName, "en_US");
    Method getLocaleId = bundleClass.getDeclaredMethod("getLocaleID");
    log("probe.bundle=" + getLocaleId.invoke(bundle));
  }
}
