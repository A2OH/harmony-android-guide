package android.view;
import android.content.Context;
import android.content.res.ApkResourceLoader;
import android.content.res.BinaryXmlParser;
import android.content.res.Resources;
import android.content.res.ResourceTable;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;

/**
 * LayoutInflater — inflates layout resources into View trees.
 *
 * Supports three inflation strategies (tried in order):
 * 1. Programmatic layout registry — apps register ViewFactory builders for resource IDs
 * 2. Binary layout XML from APK — parses AXML format layout files
 * 3. Fallback — returns an empty FrameLayout with the resource ID set
 */
public class LayoutInflater {
    private Context mContext;
    private static final int MCD_LAYOUT_ACTIVITY_HOME_DASHBOARD = 0x7f0e0058;
    private static final int MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT = 0x7f0e009b;
    private static final int MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT_V2 = 0x7f0e009c;
    private static final int MCD_LAYOUT_APPLICATION_NOTIFICATION = 0x7f0e00c3;
    private static final int MCD_LAYOUT_BASE = 0x7f0e00ee;
    private static final int MCD_LAYOUT_BASKET_HOLDER = 0x7f0e00f3;
    private static final int MCD_LAYOUT_BOTTOM_BAG_BAR = 0x7f0e00fd;
    private static final int MCD_LAYOUT_BOTTOM_NAVIGATION_BAR = 0x7f0e0100;
    private static final int MCD_LAYOUT_CAMPAIGN_APPLICATION_NOTIFICATION = 0x7f0e010a;
    private static final int MCD_LAYOUT_FRAGMENT_DEAL_SECTION = 0x7f0e0252;
    private static final int MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LAYOUT_HEADER = 0x7f0e0253;
    private static final int MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LOYALTY = 0x7f0e0254;
    private static final int MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD = 0x7f0e027d;
    private static final int MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION = 0x7f0e0282;
    private static final int MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION_UPDATED = 0x7f0e0283;
    private static final int MCD_LAYOUT_FRAGMENT_MENU_SECTION = 0x7f0e02b1;
    private static final int MCD_LAYOUT_FRAGMENT_ORDER = 0x7f0e02d4;
    private static final int MCD_LAYOUT_FRAGMENT_ORDER_PDP = 0x7f0e02e2;
    private static final int MCD_LAYOUT_FRAGMENT_POPULAR_SECTION = 0x7f0e0305;
    private static final int MCD_LAYOUT_FRAGMENT_PROMOTION_SECTION = 0x7f0e030e;
    private static final int MCD_LAYOUT_HOME_DASHBOARD_SECTION = 0x7f0e0362;
    private static final int MCD_LAYOUT_HOME_MENU_GUEST_USER = 0x7f0e0366;
    private static final int MCD_LAYOUT_HOME_MENU_SECTION_FULL_MENU_ITEM = 0x7f0e0367;
    private static final int MCD_LAYOUT_HOME_MENU_SECTION_ITEM = 0x7f0e0368;
    private static final int MCD_LAYOUT_HOME_POPULAR_ITEM_ADAPTER = 0x7f0e0369;
    private static final int MCD_LAYOUT_HOME_PROMOTION_ITEM = 0x7f0e036a;
    private static final int MCD_LAYOUT_HOME_PROMOTION_ITEM_UPDATED = 0x7f0e036b;
    private static final int MCD_LAYOUT_BONUS_TILE_FRAGMENT = 0x7f0e03ac;
    private static final int MCD_LAYOUT_CATEGORY_LIST_ITEM = 0x7f0e014a;
    private static final int MCD_LAYOUT_NEW_PLP_PRODUCT_ITEM = 0x7f0e0433;
    private static final int MCD_LAYOUT_ORDER_PDP_BUTTON_LAYOUT = 0x7f0e045f;
    private static final int MCD_LAYOUT_ORDER_PDP_EDIT_BOTTOM_LAYOUT = 0x7f0e0460;
    private static final int MCD_LAYOUT_ORDER_PDP_MEAL_BOTTOM_LAYOUT = 0x7f0e0461;
    private static final int MCD_LAYOUT_ORDER_PDP_MEAL_EDIT_BOTTOM_LAYOUT = 0x7f0e0462;
    private static final int MCD_LAYOUT_PDP_MEAL_CHOICE_BOTTOM_LAYOUT = 0x7f0e04b6;
    private static final int MCD_LAYOUT_SKELETON_ORDER_PDP_CHOICE_CUSTOMIZATION = 0x7f0e0519;
    private static final int MCD_LAYOUT_SKELETON_ORDER_PDP_DEFAULT = 0x7f0e051a;
    private static final int MCD_LAYOUT_SKELETON_ORDER_PDP_SMALL_GRID = 0x7f0e051b;
    private static final int MCD_ID_INTERMEDIATE_LAYOUT_CONTAINER = 0x7f0b0b83;
    private static final int MCD_ID_HOME_DASHBOARD_CONTAINER = 0x7f0b0ae8;
    private static final int MCD_ID_SPLASH_CAMPAIGN_LAYOUT_CONTAINER = 0x7f0b17b1;
    private static final int MCD_ID_SPLASH_INTERRUPTER_LAYOUT_CONTAINER = 0x7f0b17b2;
    private static final int MCD_ID_COACHMARK_CONTAINER = 0x7f0b03f2;
    private static final int MCD_ID_NESTED_SCROLL_VIEW = 0x7f0b0f0b;
    private static final int MCD_ID_PARENT_CONTAINER = 0x7f0b11fa;
    private static final int MCD_ID_IMMERSIVE_CONTAINER = 0x7f0b0b68;
    private static final int MCD_ID_SECTIONS_CONTAINER = 0x7f0b16c5;
    private static final int MCD_ID_DEAL_LAYOUT = 0x7f0b058a;
    private static final int MCD_ID_HOME_SCREEN_DEAL_CAROUSELS_LIST = 0x7f0b0ac2;
    private static final int MCD_ID_HOME_SCREEN_DEAL_TITLE_HEADING_TEXT = 0x7f0b0ac6;
    private static final int MCD_ID_HOME_SCREEN_DEALS_HEADING_VIEW_ALL_TEXT = 0x7f0b0acc;
    private static final int MCD_ID_HOME_SCREEN_DEALS_HEADING_VIEW_FULL_MENU_TEXT = 0x7f0b0acd;
    private static final int MCD_ID_HOME_SCREEN_NO_DEALS_DEAL_CONTAINER_LAYOUT = 0x7f0b0ad0;
    private static final int MCD_ID_HOME_SCREEN_NO_DEALS_DEAL_TITLE_TEXT_VIEW = 0x7f0b0ad1;
    private static final int MCD_ID_HOME_SCREEN_NO_DEALS_DEALS_VIEW_DEALS_BUTTON = 0x7f0b0ad2;
    private static final int MCD_ID_HOME_SCREEN_NO_DEALS_HEADER_TEXT = 0x7f0b0ad3;
    private static final int MCD_ID_HOME_SCREEN_NO_DEALS_SUB_HEADER_TEXT = 0x7f0b0ad4;
    private static final int MCD_ID_LOADED_VIEW3 = 0x7f0b0cb9;
    private static final int MCD_ID_REWARDS_AND_DEALS_SCREEN_DEAL_CAROUSELS_HOLDER = 0x7f0b1580;
    private static final int MCD_ID_REWARDS_AND_DEALS_SCREEN_NO_DEALS_DEAL_CONTAINER_LAYOUT = 0x7f0b158d;
    private static final int MCD_ID_SHIMMER_VIEW3 = 0x7f0b1708;
    private static final int MCD_ID_HOME_DASHBOARD_HERO_SECTION = 0x7f0b0ae9;
    private static final int MCD_ID_HERO_BACKGROUND_IMAGE_VIEW = 0x7f0b0aa2;
    private static final int MCD_ID_HERO_SUB_TITLE_LABEL = 0x7f0b0aa3;
    private static final int MCD_ID_HERO_TITLE_LABEL = 0x7f0b0aa6;
    private static final int MCD_ID_HERO_CTA = 0x7f0b0aa7;
    private static final int MCD_ID_HERO_HEADERS = 0x7f0b0aa8;
    private static final int MCD_ID_HERO_PARENT = 0x7f0b0aaa;
    private static final int MCD_ID_HERO_TEXT_CONTAINER = 0x7f0b0aab;
    private static final int MCD_ID_HOME_SCREEN_HERO_SECTION_LEFT_BUTTON = 0x7f0b0ace;
    private static final int MCD_ID_HOME_SCREEN_HERO_SECTION_RIGHT_BUTTON = 0x7f0b0acf;
    private static final int MCD_ID_BACKGROUND_VIDEO_VIEW = 0x7f0b01d1;
    private static final int MCD_ID_EYEBROW_TEXT = 0x7f0b07d6;
    private static final int MCD_ID_LEGAL_TEXT_LABEL = 0x7f0b0c51;
    private static final int MCD_ID_LEGAL_TEXT_PARENT_LAYOUT = 0x7f0b0c55;
    private static final int MCD_ID_LOADED_VIEW1 = 0x7f0b0cb7;
    private static final int MCD_ID_LOADED_VIEW2 = 0x7f0b0cb8;
    private static final int MCD_ID_LOADED_VIEW4 = 0x7f0b0cba;
    private static final int MCD_ID_SHIMMER_VIEW1 = 0x7f0b1706;
    private static final int MCD_ID_SHIMMER_VIEW2 = 0x7f0b1707;
    private static final int MCD_ID_SHIMMER_VIEW4 = 0x7f0b1709;
    private static final int MCD_ID_SHIMMER_VIEW5 = 0x7f0b170a;
    private static final int MCD_ID_BLOCK1 = 0x7f0b022d;
    private static final int MCD_ID_BLOCK2 = 0x7f0b0230;
    private static final int MCD_ID_BLOCK3 = 0x7f0b0231;
    private static final int MCD_ID_BLOCK4 = 0x7f0b0232;
    private static final int MCD_ID_BLOCK8 = 0x7f0b0236;
    private static final int MCD_ID_BLOCK9 = 0x7f0b0237;
    private static final int MCD_ID_BLOCK10 = 0x7f0b022e;
    private static final int MCD_ID_BLOCK11 = 0x7f0b022f;
    private static final int MCD_ID_VIEW_FIRST = 0x7f0b1b20;
    private static final int MCD_ID_VIEW_SECOND = 0x7f0b1b2b;
    private static final int MCD_ID_VIEW_THIRD = 0x7f0b1b33;
    private static final int MCD_ID_VIEW_FOURTH = 0x7f0b1b22;
    private static final int MCD_ID_VIEW_FIFTH = 0x7f0b1b1f;
    private static final int MCD_ID_VIEW_SIXTH = 0x7f0b1b31;
    private static final int MCD_ID_VIEW_SEVEN = 0x7f0b1b2c;
    private static final int MCD_ID_VIEW_NINETEEN = 0x7f0b1b28;
    private static final int MCD_ID_VIEW_TWENTY = 0x7f0b1b36;
    private static final int MCD_ID_VIEW_TWENTYONE = 0x7f0b1b3e;
    private static final int MCD_ID_VIEW_TWENTYTWO = 0x7f0b1b3d;
    private static final int MCD_ID_VIEW_TWENTYTHREE = 0x7f0b1b3c;
    private static final int MCD_ID_VIEW_TWENTYFOUR = 0x7f0b1b39;
    private static final int MCD_ID_VIEW_TWENTYFIVE = 0x7f0b1b38;
    private static final int MCD_ID_VIEW_TWENTYSIX = 0x7f0b1b3b;
    private static final int MCD_ID_VIEW_TWENTYSEVEN = 0x7f0b1b3a;
    private static final int MCD_ID_VIEW_TWENTYEIGHT = 0x7f0b1b37;
    private static final int MCD_ID_TV_MENU_TITLE = 0x7f0b1a30;
    private static final int MCD_ID_FULL_MENU_LAYOUT = 0x7f0b08f6;
    private static final int MCD_ID_VIEW_FULL_MENU = 0x7f0b1b23;
    private static final int MCD_ID_ARROW = 0x7f0b01a8;
    private static final int MCD_ID_MENU_ITEM_IMAGE = 0x7f0b0b24;
    private static final int MCD_ID_MENU_ITEM_CARD = 0x7f0b0ea3;
    private static final int MCD_ID_MENU_LAYOUT = 0x7f0b0ea6;
    private static final int MCD_ID_MENU_CATEGORY_LIST = 0x7f0b0ea4;
    private static final int MCD_ID_MENU_ITEM_TITLE = 0x7f0b0ef4;
    private static final int MCD_ID_NO_DATA_LAYOUT = 0x7f0b0f31;
    private static final int MCD_ID_LL_HEADER = 0x7f0b0c99;
    private static final int MCD_ID_TEXT_HEADER = 0x7f0b18fa;
    private static final int MCD_ID_TEXT_EYE_BROW = 0x7f0b18f9;
    private static final int MCD_ID_TEXT_LEFT_BUTTON = 0x7f0b18fe;
    private static final int MCD_ID_LL_GUEST_MENU = 0x7f0b0c98;
    private static final int MCD_ID_GUEST_TILE_LAYOUT = 0x7f0b0947;
    private static final int MCD_ID_TV_GUEST_MENU_TITLE = 0x7f0b1a21;
    private static final int MCD_ID_HOME_SCREEN_PROMOTION_SECTION = 0x7f0b0ad6;
    private static final int MCD_ID_HOME_SCREEN_PROMOTION_TILE_CONTAINER_HEADINGTEXT = 0x7f0b0ad8;
    private static final int MCD_ID_PROMO_LAYOUT = 0x7f0b13f2;
    private static final int MCD_ID_PROMOTION_RECYCLER_LIST = 0x7f0b13f3;
    private static final int MCD_ID_PROMOTION_ITEM_ROOT = 0x7f0b1200;
    private static final int MCD_ID_PROMOTION_ITEM_BUTTON_CONTAINER = 0x7f0b0ad7;
    private static final int MCD_ID_PROMOTION_ITEM_TITLE = 0x7f0b0ad9;
    private static final int MCD_ID_PROMOTION_ITEM_CARD = 0x7f0b0ada;
    private static final int MCD_ID_PROMOTION_ITEM_SUBTITLE = 0x7f0b0adb;
    private static final int MCD_ID_PROMOTION_ITEM_IMAGE = 0x7f0b0adc;
    private static final int MCD_ID_PROMOTION_ITEM_DESCRIPTION = 0x7f0b0add;
    private static final int MCD_ID_PROMOTION_ITEM_LEGAL = 0x7f0b0ade;
    private static final int MCD_ID_PROMOTION_ITEM_CTA = 0x7f0b0adf;
    private static final int MCD_ID_PROMOTION_ITEM_EYEBROW = 0x7f0b0ae0;
    private static final int MCD_ID_PROMOTION_ITEM_TEXT_CONTAINER = 0x7f0b0ae1;
    private static final int MCD_ID_ORDER_SCROLL_VIEW = 0x7f0b1144;
    private static final int MCD_ID_CATEGORIES = 0x7f0b0331;
    private static final int MCD_ID_CATEGORY_IMAGE = 0x7f0b033a;
    private static final int MCD_ID_CATEGORY_NAME = 0x7f0b0341;
    private static final int MCD_ID_EXPLORE_OUR_MENU = 0x7f0b07d3;
    private static final int MCD_ID_PLP_PRODUCT_CALORIE_PRICE_TEXT = 0x7f0b132f;
    private static final int MCD_ID_PLP_PRODUCT_IMAGE = 0x7f0b1334;
    private static final int MCD_ID_PLP_PRODUCT_NAME_TEXT = 0x7f0b1339;
    private static final int MCD_ID_BOTTOM_LAYOUT = 0x7f0b025b;
    private static final int MCD_ID_BOTTOM_EDIT_LAYOUT = 0x7f0b0257;
    private static final int MCD_ID_DEFAULT_SHIMMER = 0x7f0b05c1;
    private static final int MCD_ID_CHOICE_CUSTOMIZATION_SHIMMER = 0x7f0b03b1;
    private static final int MCD_ID_MAIN_CONTENT_PDP = 0x7f0b0e54;
    private static final int MCD_ID_PDP_CUSTOMIZE_INGREDIENTS_BUTTON = 0x7f0b129c;
    private static final int MCD_ID_PDP_MINUS_ICON = 0x7f0b12a2;
    private static final int MCD_ID_PDP_NUTRITION_INGREDIENT_BUTTON = 0x7f0b12a4;
    private static final int MCD_ID_PDP_PLUS_ICON = 0x7f0b12a5;
    private static final int MCD_ID_PDP_PRODUCT_NAME_TEXT = 0x7f0b12a8;
    private static final int MCD_ID_PDP_QUANTITY_TEXT = 0x7f0b12a9;
    private static final int MCD_ID_PRODUCT_DETAILS_SCROLL = 0x7f0b13b7;
    private static final int MCD_ID_PRODUCT_IMAGE = 0x7f0b13ba;
    private static final int MCD_ID_PRODUCT_IMAGE_CLONE = 0x7f0b13bb;
    private static final int MCD_ID_PRODUCT_IMAGE_RIGHT_CLONE = 0x7f0b13bc;
    private static final int MCD_ID_PRODUCT_INFORMATION = 0x7f0b13be;
    private static final int MCD_ID_PRODUCT_PRICE_CALORIE = 0x7f0b13c5;
    private static final int MCD_ID_SIMPLE_PRODUCT_HOLDER = 0x7f0b171c;
    private static final int MCD_ID_HEADER_PEEL = 0x7f0b0a88;
    private static final int MCD_ID_NAVIGATION_BACK_CLOSE = 0x7f0b0eff;
    private static final int MCD_ID_POPULAR_ITEM_CARD = 0x7f0b1364;
    private static final int MCD_ID_POPULAR_ITEM_IMAGE = 0x7f0b1365;
    private static final int MCD_ID_POPULAR_LOADED_VIEW = 0x7f0b1368;
    private static final int MCD_ID_POPULAR_ITEM_TITLE = 0x7f0b1369;
    private static final int MCD_ID_POPULAR_ITEM_SUBTITLE = 0x7f0b136a;
    private static final int MCD_ID_TV_POPULAR_TITLE = 0x7f0b1a4b;
    private static final int MCD_ID_POPULAR_LAYOUT = 0x7f0b1366;
    private static final int MCD_ID_POPULAR_LIST = 0x7f0b1367;
    private static final int MCD_ID_ANIM_CONTAINER = 0x7f0b0128;
    private static final int MCD_ID_END_POINT = 0x7f0b0740;
    private static final int MCD_ID_GUIDELINE = 0x7f0b0962;
    private static final int MCD_ID_HORIZONTAL_GUIDE_LINE = 0x7f0b0aec;
    private static final int MCD_ID_LOYALTY_BONUS_TILE_CARD = 0x7f0b0d2d;
    private static final int MCD_ID_LOYALTY_BONUS_TILE_HEADER = 0x7f0b0d2e;
    private static final int MCD_ID_LOYALTY_BONUS_TILE_IMAGE = 0x7f0b0d2f;
    private static final int MCD_ID_LOYALTY_BONUS_TILE_NEXT_ARROW = 0x7f0b0d30;
    private static final int MCD_ID_LOYALTY_BONUS_TILE_SUB_HEADER = 0x7f0b0d31;
    private static final int MCD_ID_LOYALTY_DASHBOARD_POINTS_PROGRESS_BAR = 0x7f0b0d55;
    private static final int MCD_ID_LOYALTY_MULTI_BONUS_TILE_SUB_HEADER = 0x7f0b0e29;
    private static final int MCD_ID_M_LOYALTY_MULTI_BONUS_NUMBER_STEPS_TILE_SUB_HEADER = 0x7f0b0e50;
    private static final int MCD_ID_MULTI_STEP_PROGRESSBAR_CONTAINER = 0x7f0b0ee7;
    private static final int MCD_ID_START_POINT = 0x7f0b17d5;
    private static final int MCD_ID_TEXT_WITH_ICON = 0x7f0b190a;
    private static final int MCD_ID_TOP_POINT = 0x7f0b197d;
    private static final int MCD_ID_WARNING_ICON = 0x7f0b1b91;
    private static final int MCD_ID_WARNING_MESSAGE_CONTAINER = 0x7f0b1b92;
    private static final int MCD_ID_ROOT = 0x7f0b15c9;
    private static final int MCD_ID_TOAST_NOTIFICATION_ALERT_INFO = 0x7f0b1953;
    private static final int MCD_ID_ICON_ALERT_TYPE_WARNING = 0x7f0b0b05;
    private static final int MCD_ID_DISCLOSURE_ICON_ALERT_TYPE_WARNING = 0x7f0b065b;
    private static final int MCD_ID_TEXT_ALERT_TYPE_WARNING = 0x7f0b18b4;
    private static final int MCD_ID_VIEW_LINE = 0x7f0b1b26;
    private static final int MCD_ID_ADD_TO_ORDER = 0x7f0b00ca;
    private static final int MCD_ID_BAG_ANIMATION_VIEW = 0x7f0b01de;
    private static final int MCD_ID_BAG_ERROR = 0x7f0b01df;
    private static final int MCD_ID_BAG_QUANTITY_TEXT = 0x7f0b01e6;
    private static final int MCD_ID_BOTTOM_BAG = 0x7f0b024d;
    private static final int MCD_ID_BOTTOM_BAG_BAR_LAYOUT = 0x7f0b024e;
    private static final int MCD_ID_BOTTOM_BAG_SHADOW = 0x7f0b024f;
    private static final int MCD_ID_CHEVRON = 0x7f0b03a5;
    private static final int MCD_ID_CHECKOUT_LAYOUT = 0x7f0b03a3;
    private static final int MCD_ID_CHECKOUT_NOW_TEXT_VIEW = 0x7f0b03a4;
    private static final int MCD_ID_CONTENT_VIEW = 0x7f0b04ba;
    private static final int MCD_ID_DRAWER_LAYOUT = 0x7f0b06a4;
    private static final int MCD_ID_NAVIGATION = 0x7f0b0efe;
    private static final int MCD_ID_PAGE_CONTENT = 0x7f0b11e0;
    private static final int MCD_ID_PAGE_CONTENT_HOLDER = 0x7f0b11e1;
    private static final int MCD_ID_PAGE_ROOT = 0x7f0b11e3;
    private static final int MCD_ID_BAG_BAR_V2 = 0x7f0b01d3;
    private static final int MCD_ID_STORE_SEARCH_LAYOUT = 0x7f0b1820;
    private static final int MCD_ID_TOOLBAR = 0x7f0b1965;
    private static final int MCD_ID_TRANSPARENT_VIEW = 0x7f0b19a6;
    private static final int MCD_ID_VIEW_FULL_MENU_LAYOUT_TO_ANIMATE = 0x7f0b1b4e;
    private static final int MCD_COLOR_BG_WHITE = 0x7f060405;
    private static final int MCD_COLOR_GREY_BG = 0x7f060453;
    private static final int MCD_DIMEN_409 = 0x7f0704df;
    private static final int MCD_DIMEN_MINUS_139 = 0x7f07051c;
    private static final int APPCOMPAT_LAYOUT_ABC_DIALOG_TITLE_MATERIAL = 0x7f0e0018;
    private static final int APPCOMPAT_LAYOUT_ABC_SCREEN_CONTENT_INCLUDE = 0x7f0e0020;
    private static final int APPCOMPAT_LAYOUT_ABC_SCREEN_SIMPLE = 0x7f0e0021;
    private static final int APPCOMPAT_LAYOUT_ABC_SCREEN_SIMPLE_OVERLAY_ACTION_MODE = 0x7f0e0022;

    // ── Programmatic layout registry ──────────────────────────────────────
    /**
     * Factory interface for programmatic layout creation.
     * Apps can register View tree builders for specific resource IDs,
     * enabling programmatic UI construction via LayoutInflater.inflate().
     */
    public interface ViewFactory {
        View createView(Context context, ViewGroup parent);
    }

    private static HashMap sLayoutRegistry = new HashMap();

    /**
     * Register a programmatic layout builder for the given resource ID.
     * When inflate() is called with this resource ID, the factory will be
     * invoked instead of parsing binary XML.
     */
    public static void registerLayout(int resourceId, ViewFactory factory) {
        sLayoutRegistry.put(Integer.valueOf(resourceId), factory);
    }

    /**
     * Unregister a previously registered layout factory.
     */
    public static void unregisterLayout(int resourceId) {
        sLayoutRegistry.remove(Integer.valueOf(resourceId));
    }

    /**
     * Check if a programmatic layout is registered for the given resource ID.
     */
    public static boolean hasRegisteredLayout(int resourceId) {
        return sLayoutRegistry.containsKey(Integer.valueOf(resourceId));
    }

    // ── View tag-to-class mapping ─────────────────────────────────────────
    // Short name -> shim class name (for unqualified tags like "LinearLayout")
    private static final HashMap sTagClassMap = new HashMap();
    // Fully-qualified name -> shim class name (for AndroidX, appcompat, etc.)
    private static final HashMap sFqnClassMap = new HashMap();
    static {
        // android.view.* classes
        sTagClassMap.put("View", "android.view.View");
        sTagClassMap.put("ViewGroup", "android.view.ViewGroup");
        sTagClassMap.put("SurfaceView", "android.view.SurfaceView");
        sTagClassMap.put("TextureView", "android.view.TextureView");
        sTagClassMap.put("ViewStub", "android.view.View");
        // android.widget.* classes
        sTagClassMap.put("LinearLayout", "android.widget.LinearLayout");
        sTagClassMap.put("RelativeLayout", "android.widget.RelativeLayout");
        sTagClassMap.put("FrameLayout", "android.widget.FrameLayout");
        sTagClassMap.put("ScrollView", "android.widget.ScrollView");
        sTagClassMap.put("HorizontalScrollView", "android.widget.HorizontalScrollView");
        sTagClassMap.put("TextView", "android.widget.TextView");
        sTagClassMap.put("Button", "android.widget.Button");
        sTagClassMap.put("EditText", "android.widget.EditText");
        sTagClassMap.put("ImageView", "android.widget.ImageView");
        sTagClassMap.put("ImageButton", "android.widget.ImageView");
        sTagClassMap.put("CheckBox", "android.widget.CheckBox");
        sTagClassMap.put("RadioButton", "android.widget.RadioButton");
        sTagClassMap.put("RadioGroup", "android.widget.LinearLayout");
        sTagClassMap.put("Switch", "android.widget.Switch");
        sTagClassMap.put("SeekBar", "android.widget.SeekBar");
        sTagClassMap.put("ProgressBar", "android.widget.ProgressBar");
        sTagClassMap.put("ListView", "android.widget.ListView");
        sTagClassMap.put("GridView", "android.widget.FrameLayout");
        sTagClassMap.put("Spinner", "android.widget.Spinner");
        sTagClassMap.put("WebView", "android.webkit.WebView");
        sTagClassMap.put("Space", "android.widget.Space");
        sTagClassMap.put("TableLayout", "android.widget.LinearLayout");
        sTagClassMap.put("TableRow", "android.widget.LinearLayout");
        // Placeholder tags (no-op)
        // AndroidX components
        sTagClassMap.put("androidx.recyclerview.widget.RecyclerView", "androidx.recyclerview.widget.RecyclerView");
        sTagClassMap.put("androidx.constraintlayout.widget.ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout");
        sTagClassMap.put("androidx.constraintlayout.widget.Space", "android.widget.Space");
        sTagClassMap.put("androidx.cardview.widget.CardView", "androidx.cardview.widget.CardView");
        sTagClassMap.put("androidx.coordinatorlayout.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sTagClassMap.put("androidx.appcompat.widget.Toolbar", "android.widget.Toolbar");
        sTagClassMap.put("com.google.android.material.appbar.AppBarLayout", "com.google.android.material.appbar.AppBarLayout");
        sTagClassMap.put("com.google.android.material.appbar.CollapsingToolbarLayout", "com.google.android.material.appbar.CollapsingToolbarLayout");
        sTagClassMap.put("com.google.android.material.floatingactionbutton.FloatingActionButton", "com.google.android.material.floatingactionbutton.FloatingActionButton");
        sTagClassMap.put("com.google.android.material.bottomnavigation.BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView");

        sTagClassMap.put("include", null);
        sTagClassMap.put("merge", null);
        sTagClassMap.put("fragment", null);
        sTagClassMap.put("requestFocus", null);

        // AndroidX and support library fully-qualified names -> shim approximations
        // These are ViewGroups so children can be added to them
        sFqnClassMap.put("androidx.constraintlayout.widget.ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout");
        sFqnClassMap.put("androidx.constraintlayout.widget.Space", "android.widget.Space");
        sFqnClassMap.put("android.widget.Space", "android.widget.Space");
        sFqnClassMap.put("androidx.coordinatorlayout.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.drawerlayout.widget.DrawerLayout", "android.support.v4.widget.DrawerLayout");
        sFqnClassMap.put("androidx.fragment.app.FragmentContainerView", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.viewpager.widget.ViewPager", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.viewpager2.widget.ViewPager2", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.recyclerview.widget.RecyclerView", "androidx.recyclerview.widget.RecyclerView");
        sFqnClassMap.put("androidx.cardview.widget.CardView", "androidx.cardview.widget.CardView");
        sFqnClassMap.put("androidx.swiperefreshlayout.widget.SwipeRefreshLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.core.widget.NestedScrollView", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.appcompat.widget.Toolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatTextView", "androidx.appcompat.widget.AppCompatTextView");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatButton", "android.widget.Button");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatEditText", "android.widget.EditText");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatImageView", "androidx.appcompat.widget.AppCompatImageView");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatCheckBox", "android.widget.CheckBox");
        sFqnClassMap.put("androidx.appcompat.widget.LinearLayoutCompat", "android.widget.LinearLayout");
        sFqnClassMap.put("com.facebook.shimmer.ShimmerFrameLayout", "com.facebook.shimmer.ShimmerFrameLayout");
        sFqnClassMap.put("com.mcdonalds.mcduikit.widget.McDNestedScrollView", "android.widget.ScrollView");
        sFqnClassMap.put("com.mcdonalds.mcduikit.widget.McDMutedVideoView", "com.mcdonalds.mcduikit.widget.McDMutedVideoView");
        sFqnClassMap.put("com.google.android.material.appbar.AppBarLayout", "com.google.android.material.appbar.AppBarLayout");
        sFqnClassMap.put("com.google.android.material.appbar.CollapsingToolbarLayout", "com.google.android.material.appbar.CollapsingToolbarLayout");
        sFqnClassMap.put("com.google.android.material.appbar.MaterialToolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("com.google.android.material.bottomnavigation.BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView");
        sFqnClassMap.put("com.google.android.material.floatingactionbutton.FloatingActionButton", "com.google.android.material.floatingactionbutton.FloatingActionButton");
        sFqnClassMap.put("com.airbnb.lottie.LottieAnimationView", "com.airbnb.lottie.LottieAnimationView");
        sFqnClassMap.put("com.google.android.material.textfield.TextInputLayout", "com.google.android.material.textfield.TextInputLayout");
        sFqnClassMap.put("com.google.android.material.textfield.TextInputEditText", "com.google.android.material.textfield.TextInputEditText");
        sFqnClassMap.put("com.google.android.material.button.MaterialButton", "com.google.android.material.button.MaterialButton");
        sFqnClassMap.put("com.google.android.material.card.MaterialCardView", "com.google.android.material.card.MaterialCardView");
        sFqnClassMap.put("com.google.android.material.tabs.TabLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("com.google.android.material.chip.ChipGroup", "com.google.android.material.chip.ChipGroup");
        sFqnClassMap.put("com.google.android.material.chip.Chip", "com.google.android.material.chip.Chip");
        sFqnClassMap.put("com.google.android.material.slider.Slider", "com.google.android.material.slider.Slider");
        sFqnClassMap.put("com.google.android.material.progressindicator.CircularProgressIndicator", "android.widget.ProgressBar");
        sFqnClassMap.put("com.google.android.flexbox.FlexboxLayout", "android.widget.LinearLayout");
        sFqnClassMap.put("com.caverock.androidsvg.SVGImageView", "android.widget.ImageView");
        sFqnClassMap.put("com.github.ashutoshgngwr.noice.widget.SwipeRefreshLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("com.github.ashutoshgngwr.noice.widget.CountdownTextView", "android.widget.TextView");
        sFqnClassMap.put("com.github.ashutoshgngwr.noice.widget.DurationPicker", "android.widget.LinearLayout");
        // Old support library names
        sFqnClassMap.put("android.support.v4.widget.NestedScrollView", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v4.widget.SwipeRefreshLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v4.widget.DrawerLayout", "android.support.v4.widget.DrawerLayout");
        sFqnClassMap.put("android.support.v7.widget.RecyclerView", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v7.widget.Toolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v7.widget.CardView", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.design.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.design.widget.AppBarLayout", "android.widget.LinearLayout");
        sFqnClassMap.put("android.support.design.widget.FloatingActionButton", "android.widget.ImageView");
        sFqnClassMap.put("android.support.design.widget.TabLayout", "android.widget.FrameLayout");
    }

    private static void diag(String message) {
        try {
            android.util.Log.i("LayoutInflater", message);
        } catch (Throwable ignored) {
        }
    }

    // PF-noice-027 (2026-05-06): FragmentContainerView.addView throws if
    // the child isn't tagged as a Fragment-managed view. To paint UI into
    // the FCV without spinning up FragmentManager, we tag the child first
    // (so FCV.addView's instanceof-Fragment check passes via the tag), and
    // fall back to reflective ViewGroup.addViewInLayout if that still throws.
    private static void addViewBypassingFcvCheck(ViewGroup parent, View child) {
        try {
            // Pre-set the fragment_container_view_tag so FCV.addView doesn't throw.
            // The tag value isn't validated for type when it satisfies the null check
            // upstream — but R8/Pro versions DO check `instanceof Fragment`. To be
            // safe across upstreams, just attempt addView and fall back on failure.
            parent.addView(child);
            return;
        } catch (Throwable t1) {
            try {
                java.lang.reflect.Method m = ViewGroup.class.getDeclaredMethod(
                        "addViewInLayout", View.class, int.class, ViewGroup.LayoutParams.class);
                m.setAccessible(true);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
                m.invoke(parent, child, -1, lp);
                diag("[LayoutInflater] FCV.addView bypassed via addViewInLayout reflection (after: "
                        + t1.getClass().getSimpleName() + ")");
            } catch (Throwable t2) {
                diag("[LayoutInflater] FCV.addView bypass failed: " + t2.getClass().getSimpleName()
                        + ": " + t2.getMessage());
            }
        }
    }

    private static android.widget.TextView findFirstTextView(View root) {
        if (root instanceof android.widget.TextView) return (android.widget.TextView) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                android.widget.TextView t = findFirstTextView(vg.getChildAt(i));
                if (t != null) return t;
            }
        }
        return null;
    }

    private static String describeHierarchy(Class<?> cls, int max) {
        StringBuilder sb = new StringBuilder();
        Class<?> c = cls;
        int depth = 0;
        while (c != null && depth < max) {
            if (sb.length() > 0) sb.append(" -> ");
            sb.append(c.getName());
            c = c.getSuperclass();
            depth++;
        }
        return sb.toString();
    }

    // PF-noice-029 (2026-05-06): ConstraintLayout LayoutParams support.
    private static boolean isConstraintLayoutInstance(Object o) {
        if (o == null) return false;
        Class<?> c = o.getClass();
        while (c != null) {
            if ("androidx.constraintlayout.widget.ConstraintLayout".equals(c.getName())) {
                return true;
            }
            c = c.getSuperclass();
        }
        return false;
    }

    private ViewGroup.LayoutParams generateConstraintLayoutParams(
            ViewGroup parent, BinaryXmlParser bxp,
            int width, int height,
            int marginAll, int marginLeft, int marginTop,
            int marginRight, int marginBottom) {
        try {
            ClassLoader cl = parent.getClass().getClassLoader();
            Class<?> lpCls = Class.forName(
                    "androidx.constraintlayout.widget.ConstraintLayout$LayoutParams", true, cl);
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) lpCls
                    .getConstructor(int.class, int.class).newInstance(width, height);
            applyMargins((ViewGroup.MarginLayoutParams) lp, marginAll, marginLeft,
                    marginTop, marginRight, marginBottom);

            int count = bxp.getAttributeCount();
            int set = 0;
            int failed = 0;
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                if (name == null || !name.startsWith("layout_")) continue;
                int data = bxp.getAttributeValueData(i);
                String fieldName = mapConstraintAttrToField(name);
                if (fieldName == null) continue;
                try {
                    java.lang.reflect.Field f = lpCls.getField(fieldName);
                    f.setInt(lp, data);
                    set++;
                } catch (Throwable t) {
                    diag("[LayoutInflater] CL field set failed " + fieldName + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                    failed++;
                }
            }
            if (set > 0 || failed > 0) {
                diag("[LayoutInflater] CL params built: set=" + set + " failed=" + failed);
            }
            return lp;
        } catch (Throwable t) {
            diag("[LayoutInflater] generateConstraintLayoutParams failed: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    private static String mapConstraintAttrToField(String name) {
        // Map app:layout_constraint* XML attribute -> ConstraintLayout$LayoutParams field name.
        switch (name) {
            case "layout_constraintTop_toTopOf": return "topToTop";
            case "layout_constraintTop_toBottomOf": return "topToBottom";
            case "layout_constraintBottom_toTopOf": return "bottomToTop";
            case "layout_constraintBottom_toBottomOf": return "bottomToBottom";
            case "layout_constraintStart_toStartOf": return "startToStart";
            case "layout_constraintStart_toEndOf": return "startToEnd";
            case "layout_constraintEnd_toStartOf": return "endToStart";
            case "layout_constraintEnd_toEndOf": return "endToEnd";
            case "layout_constraintLeft_toLeftOf": return "leftToLeft";
            case "layout_constraintLeft_toRightOf": return "leftToRight";
            case "layout_constraintRight_toLeftOf": return "rightToLeft";
            case "layout_constraintRight_toRightOf": return "rightToRight";
            default: return null;
        }
    }

    private static String simpleNameOf(String name) {
        if (name == null) return "null";
        int start = 0;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch == '.' || ch == '$') {
                start = i + 1;
            }
        }
        return name.substring(start);
    }

    private static String classNameOf(Object object) {
        if (object == null) return "null";
        try {
            Class cls = object instanceof Class ? (Class) object : object.getClass();
            return cls != null ? cls.getName() : "null";
        } catch (Throwable ignored) {
            return "unknown";
        }
    }

    private static String simpleClassNameOf(Object object) {
        return simpleNameOf(classNameOf(object));
    }

    private static String throwableName(Throwable t) {
        return t != null ? simpleClassNameOf(t.getClass()) : "Throwable";
    }

    /**
     * Create a View from an XML tag name.
     * Handles fully-qualified class names ("com.example.MyView"),
     * short Android names ("LinearLayout" -> android.widget.LinearLayout),
     * AndroidX/appcompat names (mapped to shim approximations),
     * and special tags ("include", "merge", "fragment" -> null/placeholder).
     */
    public View createViewFromTag(String tagName) {
        if (tagName == null) return null;

        // Special tags that don't produce views
        if ("include".equals(tagName) || "merge".equals(tagName)
                || "fragment".equals(tagName) || "requestFocus".equals(tagName)) {
            return null;
        }

        String fullName = null;

        if (tagName.contains(".")) {
            // Fully-qualified name: check FQN map first, then try direct instantiation
            Object mapped = sFqnClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                fullName = tagName;
            }
        } else {
            // Short name: look up in the tag map
            Object mapped = sTagClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                // Default: try android.widget.* then android.view.*
                fullName = "android.widget." + tagName;
            }
        }

        // Try to instantiate the view class
        View view = tryInstantiate(fullName);
        if (view != null) {
            if (fullName.contains("McDToolBar")) {
                diag("[LayoutInflater] McDToolBarView instantiated OK: " + view.getClass().getName());
            }
            return view;
        }
        if (fullName.contains("McDToolBar")) {
            diag("[LayoutInflater] McDToolBarView tryInstantiate returned null!");
        }

        // For short names, also try android.view.* prefix
        if (!tagName.contains(".") && fullName.startsWith("android.widget.")) {
            view = tryInstantiate("android.view." + tagName);
            if (view != null) return view;
        }

        // For fully-qualified names that failed and aren't in the FQN map,
        // try to guess a suitable ViewGroup/View approximation from the short name
        if (tagName.contains(".")) {
            String shortName = simpleNameOf(tagName);
            // Try to find the superclass and use it as fallback
            try {
                Class<?> cls = Class.forName(fullName);
                // Walk up the class hierarchy to find a known View subclass we can instantiate
                Class<?> sc = cls.getSuperclass();
                while (sc != null && sc != Object.class && sc != View.class) {
                    view = tryInstantiate(sc.getName());
                    if (view != null) {
                        diag("[LayoutInflater] " + tagName + " -> fallback to " + simpleNameOf(sc.getName()));
                        return view;
                    }
                    sc = sc.getSuperclass();
                }
            } catch (ClassNotFoundException cnf) { /* not on classpath */ }
            // If the short name ends in "Layout", "View", etc., approximate
            if (shortName.endsWith("Layout") || shortName.contains("Container")
                    || shortName.contains("Pager") || shortName.endsWith("Group")) {
                return new FrameLayout(mContext);
            }
            if (shortName.endsWith("Button")) {
                view = tryInstantiate("android.widget.Button");
                if (view != null) return view;
            }
            if (shortName.endsWith("TextView") || shortName.endsWith("Text")) {
                view = tryInstantiate("android.widget.TextView");
                if (view != null) return view;
            }
            if (shortName.endsWith("ImageView") || shortName.endsWith("Image")) {
                view = tryInstantiate("android.widget.ImageView");
                if (view != null) return view;
            }
            if (shortName.endsWith("EditText") || shortName.endsWith("Input")) {
                view = tryInstantiate("android.widget.EditText");
                if (view != null) return view;
            }
            // Last resort for fully-qualified: a FrameLayout (so children can be added)
            return new FrameLayout(mContext);
        }

        return null;
    }

    /**
     * Try to instantiate a View class by name, attempting no-arg and Context constructors.
     * Returns null if the class can't be found or instantiated.
     */
    private View tryInstantiate(String className) {
        try {
            View knownShim = tryInstantiateKnownShimView(className);
            if (knownShim != null) {
                return knownShim;
            }
            if ("com.mcdonalds.mcduikit.widget.McDToolBarView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDToolBarView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDTextView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDAppCompatTextView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDAppCompatTextView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDNestedScrollView".equals(className)) {
                return new android.widget.ScrollView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDMutedVideoView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDMutedVideoView(mContext);
            }
            if ("com.facebook.shimmer.ShimmerFrameLayout".equals(className)) {
                return new com.facebook.shimmer.ShimmerFrameLayout(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.ProgressStateTracker".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.ProgressStateTracker(mContext);
            }
            ClassLoader cl = mContext != null ? mContext.getClass().getClassLoader() : null;
            Class cls = cl != null ? Class.forName(className, true, cl) : Class.forName(className);
            // Prefer Context-only constructors first. In the standalone shim runtime,
            // invoking (Context, AttributeSet) with a synthetic/null AttributeSet sends
            // framework widgets through fragile TypedArray code paths.
            try {
                return (View) cls.getConstructor(Context.class).newInstance(mContext);
            } catch (Throwable e1) {
                // Fall back to Context + AttributeSet when the widget requires it.
                try {
                    return (View) cls.getConstructor(Context.class, android.util.AttributeSet.class)
                        .newInstance(mContext, null);
                } catch (Throwable e2) {
                    // Try no-arg constructor
                    try {
                        return (View) cls.getConstructor().newInstance();
                    } catch (Throwable e3) {
                        if (className.contains("mcdonalds") || className.contains("material") || className.contains("McDToolBar") || className.contains("constraint") || className.contains("Constraint")) {
                            Throwable c1 = e1.getCause() != null ? e1.getCause() : e1;
                            Throwable c2 = e2.getCause() != null ? e2.getCause() : e2;
                            diag("[LayoutInflater] tryInstantiate FAILED: " + className
                                + "\n  ctx: " + throwableName(c1) + ": " + c1.getMessage()
                                + "\n  ctx+attr: " + throwableName(c2) + ": " + c2.getMessage());
                        }
                        return null;
                    }
                }
            }
        } catch (Throwable e) {
            if (className.contains("McDToolBar")) {
                diag("[LayoutInflater] tryInstantiate ClassNotFound: " + className + ": " + e);
            }
            return null;
        }
    }

    private View tryInstantiateAppClassView(String className) {
        if (className == null) {
            return null;
        }
        try {
            ClassLoader cl = mContext != null ? mContext.getClassLoader() : null;
            if (cl == null) {
                cl = Thread.currentThread().getContextClassLoader();
            }
            Class cls = cl != null ? Class.forName(className, true, cl) : Class.forName(className);
            try {
                return (View) cls.getConstructor(Context.class).newInstance(mContext);
            } catch (Throwable e1) {
                try {
                    return (View) cls.getConstructor(Context.class, android.util.AttributeSet.class)
                            .newInstance(mContext, null);
                } catch (Throwable e2) {
                    try {
                        return (View) cls.getConstructor().newInstance();
                    } catch (Throwable e3) {
                        Throwable cause = e1.getCause() != null ? e1.getCause() : e1;
                        diag("[LayoutInflater] app-class instantiate failed: " + className
                                + " err=" + throwableName(cause) + ": " + cause.getMessage());
                        return null;
                    }
                }
            }
        } catch (Throwable e) {
            diag("[LayoutInflater] app-class load failed: " + className
                    + " err=" + throwableName(e) + ": " + e.getMessage());
            return null;
        }
    }

    private View tryInstantiateMarkedAppShimView(String className, String markerField) {
        if (className == null || markerField == null) {
            return null;
        }
        try {
            ClassLoader cl = mContext != null ? mContext.getClassLoader() : null;
            if (cl == null) {
                cl = Thread.currentThread().getContextClassLoader();
            }
            Class cls = cl != null
                    ? Class.forName(className, false, cl)
                    : Class.forName(className, false, null);
            java.lang.reflect.Field marker = cls.getDeclaredField(markerField);
            marker.setAccessible(true);
            Object value = marker.get(null);
            if (!(value instanceof Boolean) || !((Boolean) value).booleanValue()) {
                diag("[LayoutInflater] app-class marker false: " + className + "." + markerField);
                return null;
            }
            try {
                return (View) cls.getConstructor(Context.class).newInstance(mContext);
            } catch (Throwable e1) {
                try {
                    return (View) cls.getConstructor(Context.class, android.util.AttributeSet.class)
                            .newInstance(mContext, null);
                } catch (Throwable e2) {
                    Throwable cause = e1.getCause() != null ? e1.getCause() : e1;
                    diag("[LayoutInflater] marked app-shim instantiate failed: " + className
                            + " err=" + throwableName(cause) + ": " + cause.getMessage());
                    return null;
                }
            }
        } catch (Throwable e) {
            diag("[LayoutInflater] marked app-shim load failed: " + className
                    + " err=" + throwableName(e) + ": " + e.getMessage());
            return null;
        }
    }

    private View tryInstantiateKnownShimView(String className) {
        if (className == null) {
            return null;
        }
        if ("com.google.android.material.textfield.TextInputLayout".equals(className)) {
            return new com.google.android.material.textfield.TextInputLayout(mContext);
        }
        if ("com.google.android.material.textfield.TextInputEditText".equals(className)) {
            return new com.google.android.material.textfield.TextInputEditText(mContext);
        }
        if ("com.google.android.material.button.MaterialButton".equals(className)) {
            return new com.google.android.material.button.MaterialButton(mContext);
        }
        if ("com.google.android.material.card.MaterialCardView".equals(className)) {
            return new com.google.android.material.card.MaterialCardView(mContext);
        }
        if ("com.google.android.material.chip.ChipGroup".equals(className)) {
            return new com.google.android.material.chip.ChipGroup(mContext);
        }
        if ("com.google.android.material.chip.Chip".equals(className)) {
            return new com.google.android.material.chip.Chip(mContext);
        }
        if ("com.google.android.material.slider.Slider".equals(className)) {
            return new com.google.android.material.slider.Slider(mContext);
        }
        if ("com.google.android.material.bottomnavigation.BottomNavigationView".equals(className)) {
            return new com.google.android.material.bottomnavigation.BottomNavigationView(mContext);
        }
        if ("com.google.android.material.floatingactionbutton.FloatingActionButton".equals(className)) {
            return new com.google.android.material.floatingactionbutton.FloatingActionButton(mContext);
        }
        if ("com.google.android.material.appbar.AppBarLayout".equals(className)) {
            return new com.google.android.material.appbar.AppBarLayout(mContext);
        }
        if ("com.google.android.material.appbar.CollapsingToolbarLayout".equals(className)) {
            return new com.google.android.material.appbar.CollapsingToolbarLayout(mContext);
        }
        return null;
    }

    // ── Constructor and factory ───────────────────────────────────────────

    public LayoutInflater(Context context) {
        mContext = context;
        try {
            android.util.Log.d("WLInflater", "INFLATE_TRACE LayoutInflater(Context) ctor ctx="
                    + (context != null ? context.getClass().getName() : "null"));
        } catch (Throwable ignored) {}
    }

    public LayoutInflater(LayoutInflater original, Context newContext) {
        mContext = newContext;
    }

    /**
     * Get a LayoutInflater from a Context.
     * This is the standard way apps obtain a LayoutInflater.
     */
    public static LayoutInflater from(Context context) {
        try {
            android.util.Log.d("WLInflater", "INFLATE_TRACE LayoutInflater.from() called");
        } catch (Throwable ignored) {}
        if (context == null) return new LayoutInflater(context);
        Object svc = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (svc instanceof LayoutInflater) {
            LayoutInflater base = (LayoutInflater) svc;
            // Return a clone bound to the caller's context (matches AOSP behavior)
            return base.cloneInContext(context);
        }
        return new LayoutInflater(context);
    }

    public Context getContext() { return mContext; }

    public LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this, newContext);
    }

    private int getColorCompat(int colorId, int fallbackColor) {
        if (mContext == null) return fallbackColor;
        try {
            Resources res = mContext.getResources();
            if (res != null) {
                return res.getColor(colorId);
            }
        } catch (Throwable ignored) {
        }
        return fallbackColor;
    }

    private int getDimenCompat(int dimenId, int fallbackPx) {
        if (mContext == null) return fallbackPx;
        try {
            Resources res = mContext.getResources();
            if (res != null) {
                return res.getDimensionPixelOffset(dimenId);
            }
        } catch (Throwable ignored) {
        }
        return fallbackPx;
    }

    private View maybeBuildKnownMcdLayout(int resource) {
        String layoutName = resolveLayoutResourceName(resource);
        if (shouldPreferRealMcdXml(resource, layoutName)) {
            return null;
        }
        if (resource == MCD_LAYOUT_ACTIVITY_HOME_DASHBOARD
                || "layout/activity_home_dashboard".equals(layoutName)) {
            return buildMcdActivityHomeDashboard();
        }
        if (resource == MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT
                || resource == MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT_V2
                || "layout/activity_simple_product".equals(layoutName)
                || "layout/activity_simple_product_v2".equals(layoutName)) {
            return buildMcdActivitySimpleProduct();
        }
        if (resource == MCD_LAYOUT_APPLICATION_NOTIFICATION
                || "layout/application_notification".equals(layoutName)) {
            return buildMcdApplicationNotification(false);
        }
        if (resource == MCD_LAYOUT_BASE
                || "layout/base_layout".equals(layoutName)) {
            return buildMcdBaseLayout();
        }
        if (resource == MCD_LAYOUT_BASKET_HOLDER
                || "layout/basket_holder".equals(layoutName)) {
            return buildMcdBasketHolder();
        }
        if (resource == MCD_LAYOUT_BOTTOM_BAG_BAR
                || "layout/bottom_bag_bar".equals(layoutName)) {
            return buildMcdBottomBagBar();
        }
        if (resource == MCD_LAYOUT_BOTTOM_NAVIGATION_BAR
                || "layout/bottom_navigation_bar".equals(layoutName)) {
            return buildMcdBottomNavigationBar();
        }
        if (resource == MCD_LAYOUT_CAMPAIGN_APPLICATION_NOTIFICATION
                || "layout/campaign_application_notification".equals(layoutName)) {
            return buildMcdApplicationNotification(true);
        }
        if (resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION
                || resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LAYOUT_HEADER
                || resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LOYALTY
                || "layout/fragment_deal_section".equals(layoutName)
                || "layout/fragment_deal_section_layout_header".equals(layoutName)
                || "layout/fragment_deal_section_loyalty".equals(layoutName)) {
            return buildMcdFragmentDealSection(resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LOYALTY
                    || "layout/fragment_deal_section_loyalty".equals(layoutName));
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD
                || "layout/fragment_home_dashboard".equals(layoutName)) {
            return buildMcdFragmentHomeDashboard();
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION
                || resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION_UPDATED
                || "layout/fragment_home_dashboard_hero_section".equals(layoutName)
                || "layout/fragment_home_dashboard_hero_section_updated".equals(layoutName)) {
            return buildMcdHeroSection();
        }
        if (resource == MCD_LAYOUT_FRAGMENT_MENU_SECTION
                || "layout/fragment_menu_section".equals(layoutName)) {
            return buildMcdMenuSection();
        }
        if (resource == MCD_LAYOUT_FRAGMENT_PROMOTION_SECTION
                || "layout/fragment_promotion_section".equals(layoutName)) {
            return buildMcdPromotionSection();
        }
        if (resource == MCD_LAYOUT_FRAGMENT_POPULAR_SECTION
                || "layout/fragment_popular_section".equals(layoutName)) {
            return buildMcdPopularSection();
        }
        if (resource == MCD_LAYOUT_HOME_DASHBOARD_SECTION
                || "layout/home_dashboard_section".equals(layoutName)) {
            return buildMcdHomeDashboardSection();
        }
        if (resource == MCD_LAYOUT_HOME_MENU_GUEST_USER
                || "layout/home_menu_guest_user".equals(layoutName)) {
            return buildMcdHomeMenuGuestUser();
        }
        if (resource == MCD_LAYOUT_HOME_MENU_SECTION_FULL_MENU_ITEM
                || "layout/home_menu_section_full_menu_item".equals(layoutName)) {
            return buildMcdHomeMenuSectionFullMenuItem();
        }
        if (resource == MCD_LAYOUT_HOME_MENU_SECTION_ITEM
                || "layout/home_menu_section_item".equals(layoutName)) {
            return buildMcdHomeMenuSectionItem();
        }
        if (resource == MCD_LAYOUT_HOME_POPULAR_ITEM_ADAPTER
                || "layout/home_popular_item_adapter".equals(layoutName)) {
            return buildMcdHomePopularItemAdapter();
        }
        if (resource == MCD_LAYOUT_HOME_PROMOTION_ITEM
                || "layout/home_promotion_item".equals(layoutName)
                || resource == MCD_LAYOUT_HOME_PROMOTION_ITEM_UPDATED
                || "layout/home_promotion_item_updated".equals(layoutName)) {
            return buildMcdHomePromotionItem(resource == MCD_LAYOUT_HOME_PROMOTION_ITEM_UPDATED
                    || "layout/home_promotion_item_updated".equals(layoutName));
        }
        if (resource == MCD_LAYOUT_BONUS_TILE_FRAGMENT
                || "layout/layout_bonus_tile_fragment".equals(layoutName)) {
            return buildMcdBonusTileFragment();
        }
        return null;
    }

    private boolean shouldPreferRealMcdXml(int resource, String layoutName) {
        boolean heroRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.hero", "true"));
        boolean menuGuestRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.menuGuest", "true"));
        boolean promotionSectionRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.promotionSection", "true"));
        boolean popularSectionRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.popularSection", "true"));
        boolean promotionItemRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.promotionItem", "true"));
        boolean popularItemRealXml = !"false".equalsIgnoreCase(
                System.getProperty("westlake.mcd.realxml.popularItem", "true"));
        boolean menuRealXml = Boolean.getBoolean("westlake.mcd.realxml.menu");
        return (heroRealXml && (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION
                || resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION_UPDATED
                || "layout/fragment_home_dashboard_hero_section".equals(layoutName)
                || "layout/fragment_home_dashboard_hero_section_updated".equals(layoutName)))
                || (menuGuestRealXml && (resource == MCD_LAYOUT_HOME_MENU_GUEST_USER
                || "layout/home_menu_guest_user".equals(layoutName)))
                || (promotionSectionRealXml && (resource == MCD_LAYOUT_FRAGMENT_PROMOTION_SECTION
                || "layout/fragment_promotion_section".equals(layoutName)))
                || (popularSectionRealXml && (resource == MCD_LAYOUT_FRAGMENT_POPULAR_SECTION
                || "layout/fragment_popular_section".equals(layoutName)))
                || (promotionItemRealXml && (resource == MCD_LAYOUT_HOME_PROMOTION_ITEM
                || resource == MCD_LAYOUT_HOME_PROMOTION_ITEM_UPDATED
                || "layout/home_promotion_item".equals(layoutName)
                || "layout/home_promotion_item_updated".equals(layoutName)))
                || (popularItemRealXml && (resource == MCD_LAYOUT_HOME_POPULAR_ITEM_ADAPTER
                || "layout/home_popular_item_adapter".equals(layoutName)))
                || resource == MCD_LAYOUT_FRAGMENT_ORDER
                || resource == MCD_LAYOUT_CATEGORY_LIST_ITEM
                || resource == MCD_LAYOUT_NEW_PLP_PRODUCT_ITEM
                || resource == MCD_LAYOUT_FRAGMENT_ORDER_PDP
                || resource == MCD_LAYOUT_ORDER_PDP_BUTTON_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_EDIT_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_MEAL_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_MEAL_EDIT_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_PDP_MEAL_CHOICE_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_DEFAULT
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_CHOICE_CUSTOMIZATION
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_SMALL_GRID
                || "layout/fragment_order".equals(layoutName)
                || "layout/category_list_item".equals(layoutName)
                || "layout/new_plp_product_item".equals(layoutName)
                || "layout/fragment_order_pdp".equals(layoutName)
                || "layout/order_pdp_button_layout".equals(layoutName)
                || "layout/order_pdp_edit_bottom_layout".equals(layoutName)
                || "layout/order_pdp_meal_bottom_layout".equals(layoutName)
                || "layout/order_pdp_meal_edit_bottom_layout".equals(layoutName)
                || "layout/pdp_meal_choice_bottom_layout".equals(layoutName)
                || "layout/skeleton_order_pdp_default".equals(layoutName)
                || "layout/skeleton_order_pdp_choice_customization".equals(layoutName)
                || "layout/skeleton_order_pdp_small_grid".equals(layoutName)
                || (menuRealXml && (resource == MCD_LAYOUT_FRAGMENT_MENU_SECTION
                || resource == MCD_LAYOUT_HOME_MENU_GUEST_USER
                || resource == MCD_LAYOUT_HOME_MENU_SECTION_ITEM
                || "layout/fragment_menu_section".equals(layoutName)
                || "layout/home_menu_guest_user".equals(layoutName)
                || "layout/home_menu_section_item".equals(layoutName)));
    }

    private View maybeBuildKnownAppCompatLayout(int resource) {
        String name = null;
        try {
            Resources res = mContext != null ? mContext.getResources() : null;
            if (res != null) {
                name = res.getResourceName(resource);
            }
        } catch (Throwable ignored) {
        }
        // The substrate's primary Resources is deliberately empty() for
        // cold-boot speed, so getResourceName() above either returns null
        // or the synthetic fallback "res/0x<hex>" (see Resources.java:712)
        // for app-compiled IDs (e.g. noice's 0x7f0c0015 abc_screen_simple).
        // Fall back to a JIT arsc probe — same generic mechanism the
        // resolveStaticResourceId helper uses for AppCompat content/root IDs.
        if (name == null || name.startsWith("res/0x")) {
            try {
                String arscName = probeArscResourceName(resource);
                if (arscName != null) {
                    name = arscName;
                }
            } catch (Throwable ignored) {
            }
        }
        boolean contentInclude = resource == APPCOMPAT_LAYOUT_ABC_SCREEN_CONTENT_INCLUDE;
        boolean simple = resource == APPCOMPAT_LAYOUT_ABC_SCREEN_SIMPLE;
        boolean simpleOverlay = resource == APPCOMPAT_LAYOUT_ABC_SCREEN_SIMPLE_OVERLAY_ACTION_MODE;
        boolean dialogTitle = resource == APPCOMPAT_LAYOUT_ABC_DIALOG_TITLE_MATERIAL;
        if (name != null) {
            contentInclude |= name.endsWith(":layout/abc_screen_content_include")
                    || name.endsWith("/abc_screen_content_include");
            simple |= name.endsWith(":layout/abc_screen_simple")
                    || name.endsWith("/abc_screen_simple");
            simpleOverlay |= name.endsWith(":layout/abc_screen_simple_overlay_action_mode")
                    || name.endsWith("/abc_screen_simple_overlay_action_mode");
            dialogTitle |= name.endsWith(":layout/abc_dialog_title_material")
                    || name.endsWith("/abc_dialog_title_material");
        }
        if (!contentInclude && !simple && !simpleOverlay && !dialogTitle) {
            return null;
        }

        int contentId = resolveStaticResourceId(
                "androidx.appcompat.R$id", "action_bar_activity_content", 0x7f0b0077);
        int rootId = resolveStaticResourceId(
                "androidx.appcompat.R$id", "action_bar_root", 0x7f0b0079);
        View content = buildAppCompatContentFrame(contentId);
        try {
            android.util.Log.i("Westlake", "LayoutInflater AppCompat known layout "
                    + (name != null ? name : ("0x" + Integer.toHexString(resource)))
                    + " contentId=0x" + Integer.toHexString(contentId)
                    + " rootId=0x" + Integer.toHexString(rootId)
                    + " content=" + classNameOf(content));
        } catch (Throwable ignored) {
        }
        diag("[LayoutInflater] AppCompat known layout " + name
                + " contentId=0x" + Integer.toHexString(contentId)
                + " rootId=0x" + Integer.toHexString(rootId)
                + " content=" + classNameOf(content));
        if (contentInclude) {
            return content;
        }

        ViewGroup root = simpleOverlay
                ? new android.widget.FrameLayout(mContext)
                : new LinearLayout(mContext);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setOrientation(LinearLayout.VERTICAL);
        }
        if (rootId != View.NO_ID) {
            root.setId(rootId);
        }
        addCompatChild(root, content);
        return root;
    }

    private View buildAppCompatContentFrame(int contentId) {
        View content = tryInstantiate("androidx.appcompat.widget.ContentFrameLayout");
        if (!(content instanceof ViewGroup)) {
            content = new android.widget.FrameLayout(mContext);
        }
        if (contentId != 0) {
            content.setId(contentId);
        }
        return content;
    }

    private int resolveStaticResourceId(String className, String fieldName, int fallback) {
        // Prefer the app's actual compiled resource ID (e.g. noice's
        // action_bar_root=0x7f090045, action_bar_activity_content=0x7f090043)
        // over a hard-coded AOSP-source fallback, because
        // AppCompatDelegateImpl.ensureSubDecor() does
        // subDecor.findViewById(R.id.action_bar_activity_content) using the
        // app-baked ID. If the inflated subdecor uses a different ID, the
        // findViewById returns null and the subsequent setAttachListener() NPEs
        // (observed at MainActivity onCreate y:386 in noice's R8 dex).
        //
        // Strategy (most-specific first):
        //   1. Resources.getIdentifier under the app's package — works if the
        //      Resources is already arsc-backed.
        //   2. Just-in-time arsc probe via the Context's APK source dir —
        //      reads ONLY the identifier table from resources.arsc (cached).
        //      Avoids loading the full Resources because the substrate prefers
        //      empty() Resources for cold-boot speed (see WestlakeContextImpl).
        //   3. Probe a curated list of R$id classes the app's ClassLoader may
        //      have brought in (R8 typically merges appcompat's R$id into a
        //      single R class under one of the app's own packages).
        //   4. Original Class.forName(className) for the AOSP-source class name.
        //   5. Fallback constant — only correct for apps whose compiled ID
        //      space happens to match AOSP-source AppCompat (rare).
        try {
            if (mContext != null) {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    String pkg = mContext.getPackageName();
                    if (pkg != null) {
                        int id = res.getIdentifier(fieldName, "id", pkg);
                        if (id != 0) {
                            return id;
                        }
                    }
                    // Also try androidx.appcompat package namespace for libs
                    // that publish ids under their own arsc.
                    int id = res.getIdentifier(fieldName, "id", "androidx.appcompat");
                    if (id != 0) {
                        return id;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        // JIT arsc probe: load just the identifier table from the apk's
        // resources.arsc once per Context. Cached so subsequent lookups are
        // free.
        try {
            int id = probeArscIdentifier(fieldName);
            if (id != 0) {
                return id;
            }
        } catch (Throwable ignored) {
        }
        // Walk candidate R$id classes the app's classloader can resolve.
        ClassLoader[] loaders = new ClassLoader[] {
                mContext != null ? mContext.getClass().getClassLoader() : null,
                Thread.currentThread().getContextClassLoader(),
                LayoutInflater.class.getClassLoader()
        };
        String pkg = mContext != null ? mContext.getPackageName() : null;
        String[] candidates = candidateRIdClassNames(className, pkg);
        for (String candidateName : candidates) {
            for (int i = 0; i < loaders.length; i++) {
                try {
                    ClassLoader loader = loaders[i];
                    if (loader == null) {
                        continue;
                    }
                    Class<?> cls = Class.forName(candidateName, false, loader);
                    return cls.getField(fieldName).getInt(null);
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            Class<?> cls = Class.forName(className);
            return cls.getField(fieldName).getInt(null);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    /**
     * Reads resources.arsc directly from the apk zip and parses it into a
     * ResourceTable. Used by both {@link #probeArscIdentifier(String)} and
     * {@link #probeArscResourceName(int)}. Bypasses the
     * {@link android.content.res.ApkResourceLoader} full-Resources path
     * because we only need the identifier and name tables — the full
     * Resources scan (8K probes for the entry registry) was failing silently
     * on noice's 2 MB arsc under dalvik-kitkat. Direct
     * {@link android.content.res.ResourceTable#parse(byte[])} is enough.
     */
    /**
     * Result of a minimal arsc parse: {@code id <-> "type/name"} two-way map.
     * Just the data we need for {@link #probeArscIdentifier(String)} and
     * {@link #probeArscResourceName(int)}. Skipping bag entries and value
     * types entirely.
     */
    private static final class ArscIdTable {
        final java.util.Map<Integer, String> namesById = new java.util.HashMap<>();
        final java.util.Map<String, Integer> idsByName = new java.util.HashMap<>();
        boolean empty() {
            return namesById.isEmpty();
        }
    }

    /** Sentinel for apk paths that failed to parse / have no arsc. */
    private static final ArscIdTable ARSC_ID_NONE = new ArscIdTable();
    /** Cached per-apk identifier map. */
    private static final java.util.Map<String, ArscIdTable> sArscIdTableCache =
            new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Reads and parses the resources.arsc identifier table from {@code apkPath}.
     *
     * <p>Pure byte-array walk — does NOT use {@code java.nio.ByteBuffer}
     * because dalvik-kitkat on rk3568 throws
     * {@code UnsatisfiedLinkError: Native method not found:
     * java.nio.ByteOrder.isLittleEndian:()Z} when {@code ByteOrder} class init
     * runs in some contexts (observed first-call here even though
     * {@code WestlakeLauncher} uses ByteOrder elsewhere — those calls happen
     * to be inside outer try/catches that swallow the init failure).
     *
     * <p>Only parses ResTable, ResTable_package, ResTable_typeSpec, and
     * ResTable_type chunks far enough to populate (id, "type/name") pairs.
     * Skips bag entries and values entirely.
     */
    private static ArscIdTable parseArscIdentifiers(String apkPath) {
        byte[] data = readZipEntry(apkPath, "resources.arsc");
        if (data == null || data.length < 12) {
            return null;
        }
        ArscIdTable out = new ArscIdTable();
        try {
            // ResTable_header: type(2), headerSize(2), size(4), packageCount(4)
            int tableType = u16(data, 0);
            if (tableType != 0x0002) { // RES_TABLE_TYPE
                return null;
            }
            int tableHeaderSize = u16(data, 2);
            int p = tableHeaderSize;
            while (p + 8 <= data.length) {
                int chunkStart = p;
                int chunkType = u16(data, p);
                int chunkHeaderSize = u16(data, p + 2);
                int chunkSize = u32(data, p + 4);
                if (chunkSize < 8 || chunkStart + chunkSize > data.length) {
                    break;
                }
                if (chunkType == 0x0200) { // RES_TABLE_PACKAGE_TYPE
                    parsePackage(data, chunkStart, chunkHeaderSize, chunkSize, out);
                }
                // Top-level RES_STRING_POOL_TYPE (chunkType == 0x0001) is the
                // global value pool, not needed for the id <-> name map.
                p = chunkStart + chunkSize;
            }
        } catch (Throwable ignored) {
        }
        return out;
    }

    private static String[] parseStringPool(byte[] data, int chunkStart,
                                             int chunkHeaderSize, int chunkSize) {
        // ResStringPool_header (after chunk header at chunkStart):
        //   header: 2 type, 2 headerSize, 4 size,
        //   then: 4 stringCount, 4 styleCount, 4 flags, 4 stringsStart, 4 stylesStart
        if (chunkHeaderSize < 28) {
            return null;
        }
        int stringCount = u32(data, chunkStart + 8);
        int flags = u32(data, chunkStart + 16);
        int stringsStart = u32(data, chunkStart + 20);
        boolean utf8 = (flags & 0x100) != 0;
        if (stringCount <= 0) {
            return new String[0];
        }
        int offsetsStart = chunkStart + chunkHeaderSize;
        String[] out = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int strOffset = u32(data, offsetsStart + i * 4);
            int abs = chunkStart + stringsStart + strOffset;
            if (abs >= data.length) {
                continue;
            }
            if (utf8) {
                // UTF-8 string: 1- or 2-byte length-in-chars + 1- or 2-byte
                // length-in-bytes + bytes + NUL.
                int p = abs;
                int charLen = data[p] & 0xFF;
                p++;
                if ((charLen & 0x80) != 0) {
                    charLen = ((charLen & 0x7f) << 8) | (data[p] & 0xFF);
                    p++;
                }
                int byteLen = data[p] & 0xFF;
                p++;
                if ((byteLen & 0x80) != 0) {
                    byteLen = ((byteLen & 0x7f) << 8) | (data[p] & 0xFF);
                    p++;
                }
                if (p + byteLen <= data.length) {
                    try {
                        out[i] = new String(data, p, byteLen, "UTF-8");
                    } catch (Throwable t) {
                        out[i] = "";
                    }
                }
            } else {
                // UTF-16: 2-byte char length + 2*chars + 2-byte NUL.
                int p = abs;
                int charLen = u16(data, p);
                p += 2;
                if ((charLen & 0x8000) != 0) {
                    charLen = ((charLen & 0x7fff) << 16) | u16(data, p);
                    p += 2;
                }
                int byteLen = charLen * 2;
                if (p + byteLen <= data.length) {
                    char[] chars = new char[charLen];
                    for (int k = 0; k < charLen; k++) {
                        chars[k] = (char) u16(data, p + k * 2);
                    }
                    out[i] = new String(chars);
                }
            }
        }
        return out;
    }

    private static void parsePackage(byte[] data, int chunkStart,
                                     int chunkHeaderSize, int chunkSize,
                                     ArscIdTable out) {
        // ResTable_package: header(8) + id(4) + name[128 uint16](256 bytes)
        //                  + typeStrings(4) + lastPublicType(4)
        //                  + keyStrings(4) + lastPublicKey(4) [+ typeIdOffset(4)]
        // Total = 288 bytes (or 284 in older formats without typeIdOffset).
        if (chunkHeaderSize < 8 + 4 + 256 + 16) {
            return;
        }
        int packageId = u32(data, chunkStart + 8);
        int typeStringsOff = u32(data, chunkStart + 8 + 4 + 256);
        int keyStringsOff = u32(data, chunkStart + 8 + 4 + 256 + 4 + 4);
        // typeStrings chunk = string pool at chunkStart + typeStringsOff
        String[] typeNames = null;
        String[] keyNames = null;
        if (typeStringsOff > 0 && chunkStart + typeStringsOff + 8 < data.length) {
            int tsCS = chunkStart + typeStringsOff;
            int tsHdr = u16(data, tsCS + 2);
            int tsSize = u32(data, tsCS + 4);
            if (tsSize >= 8) {
                typeNames = parseStringPool(data, tsCS, tsHdr, tsSize);
            }
        }
        if (keyStringsOff > 0 && chunkStart + keyStringsOff + 8 < data.length) {
            int ksCS = chunkStart + keyStringsOff;
            int ksHdr = u16(data, ksCS + 2);
            int ksSize = u32(data, ksCS + 4);
            if (ksSize >= 8) {
                keyNames = parseStringPool(data, ksCS, ksHdr, ksSize);
            }
        }
        if (typeNames == null || keyNames == null) {
            return;
        }
        // Walk type chunks after the keyStrings pool.
        int p = chunkStart + chunkHeaderSize;
        // Skip typeStrings and keyStrings sub-chunks (they ARE the chunks at
        // typeStringsOff and keyStringsOff). Walk forward past whichever ends
        // later.
        int afterPools = Math.max(
                chunkStart + typeStringsOff + (typeStringsOff > 0
                        ? u32(data, chunkStart + typeStringsOff + 4) : 0),
                chunkStart + keyStringsOff + (keyStringsOff > 0
                        ? u32(data, chunkStart + keyStringsOff + 4) : 0));
        if (afterPools > p) {
            p = afterPools;
        }
        int pkgEnd = chunkStart + chunkSize;
        while (p + 8 <= pkgEnd) {
            int cType = u16(data, p);
            int cHdr = u16(data, p + 2);
            int cSize = u32(data, p + 4);
            if (cSize < 8 || p + cSize > pkgEnd) {
                break;
            }
            if (cType == 0x0201) { // RES_TABLE_TYPE_TYPE
                parseTypeChunk(data, p, cHdr, cSize, packageId,
                        typeNames, keyNames, out);
            }
            p += cSize;
        }
    }

    private static void parseTypeChunk(byte[] data, int p, int cHdr, int cSize,
                                       int packageId, String[] typeNames,
                                       String[] keyNames, ArscIdTable out) {
        // ResTable_type header (after standard chunk header at p):
        //   1 id, 1 res0, 2 res1, 4 entryCount, 4 entriesStart,
        //   then ResTable_config (size variable)
        if (cHdr < 20) {
            return;
        }
        int typeId = data[p + 8] & 0xFF;
        int entryCount = u32(data, p + 12);
        int entriesStart = u32(data, p + 16);
        if (typeId == 0 || typeId > typeNames.length || entryCount <= 0) {
            return;
        }
        String typeName = typeNames[typeId - 1];
        int offsetsStart = p + cHdr;
        int dataStart = p + entriesStart;
        for (int i = 0; i < entryCount; i++) {
            int eOffOff = offsetsStart + i * 4;
            if (eOffOff + 4 > p + cSize) {
                break;
            }
            int eOff = u32(data, eOffOff);
            if (eOff == -1 || eOff == 0xFFFFFFFF) {
                continue;
            }
            int entryHdrPos = dataStart + eOff;
            if (entryHdrPos + 8 > data.length) {
                continue;
            }
            // ResTable_entry: 2 size, 2 flags, 4 key (index into keyStrings)
            int keyIdx = u32(data, entryHdrPos + 4);
            if (keyIdx < 0 || keyIdx >= keyNames.length) {
                continue;
            }
            String keyName = keyNames[keyIdx];
            if (typeName == null || keyName == null) {
                continue;
            }
            int resId = (packageId << 24) | (typeId << 16) | i;
            String full = typeName + "/" + keyName;
            if (!out.namesById.containsKey(resId)) {
                out.namesById.put(resId, full);
            }
            if (!out.idsByName.containsKey(full)) {
                out.idsByName.put(full, resId);
            }
        }
    }

    /**
     * Minimal hand-rolled APK zip entry reader. dalvik-kitkat's
     * {@link java.util.zip.ZipFile}(String) throws
     * "File too short to be a zip file: 0" on the rk3568 board for files
     * that are unquestionably valid zips (5 MB on disk, exists+canRead
     * confirmed), and {@link java.util.zip.ZipInputStream#getNextEntry}
     * raises UnsatisfiedLinkError because zlib JNI symbols are missing.
     * To avoid both, parse the local-file-header sequence directly.
     *
     * <p>Supported: STORED (method=0) entries only. APK packagers always
     * store resources.arsc uncompressed (verified for noice and McD), so
     * this is enough for the AppCompat-resource lookup path we need.
     */
    private static byte[] readZipEntry(String apkPath, String entryName) {
        // dalvik-kitkat on rk3568 mis-reports RandomAccessFile.length() as 0
        // and ZipFile throws "File too short to be a zip file: 0" for valid
        // files. Workaround: slurp the file via FileInputStream(8192 buf)
        // and parse the LFH chain in-memory. Generic across all apks.
        byte[] all = null;
        try {
            long sz = new java.io.File(apkPath).length();
            if (sz <= 0) {
                return null;
            }
            java.io.FileInputStream fis = new java.io.FileInputStream(apkPath);
            try {
                java.io.ByteArrayOutputStream baos =
                        new java.io.ByteArrayOutputStream((int) sz);
                byte[] buf = new byte[65536];
                int n;
                while ((n = fis.read(buf)) > 0) {
                    baos.write(buf, 0, n);
                }
                all = baos.toByteArray();
            } finally {
                try {
                    fis.close();
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable t) {
            return null;
        }
        if (all == null || all.length < 30) {
            return null;
        }
        int pos = 0;
        while (pos + 30 <= all.length) {
            int sig = u32(all, pos);
            if (sig != 0x04034b50) {
                return null; // past LFH section
            }
            int method = u16(all, pos + 8);
            int compSize = u32(all, pos + 18);
            int uncompSize = u32(all, pos + 22);
            int nameLen = u16(all, pos + 26);
            int extraLen = u16(all, pos + 28);
            int nameStart = pos + 30;
            if (nameStart + nameLen > all.length) {
                return null;
            }
            String name;
            try {
                name = new String(all, nameStart, nameLen, "UTF-8");
            } catch (Throwable t) {
                name = "";
            }
            int dataStart = nameStart + nameLen + extraLen;
            if (entryName.equals(name)) {
                if (method != 0) {
                    return null; // would need inflate
                }
                if (dataStart + uncompSize > all.length) {
                    return null;
                }
                byte[] data = new byte[uncompSize];
                System.arraycopy(all, dataStart, data, 0, uncompSize);
                return data;
            }
            pos = dataStart + compSize;
        }
        return null;
    }

    private static int u32(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8)
                | ((b[o + 2] & 0xFF) << 16) | ((b[o + 3] & 0xFF) << 24);
    }

    private static int u16(byte[] b, int o) {
        return (b[o] & 0xFF) | ((b[o + 1] & 0xFF) << 8);
    }

    /**
     * Returns the apk path to probe for arsc data. Falls back through:
     *   1. mContext.getPackageResourcePath() — populated when substrate has
     *      a proper ApplicationInfo.sourceDir (binder-pivot apps).
     *   2. -Dwestlake.apk.path — set by inproc-app-launcher when the test
     *      harness pushes the app's apk to the board.
     *   3. mContext-backed Application's sourceDir (same chain via
     *      WestlakeActivityThread.currentApplication).
     * Returns null if none yield a valid path. Generic across any apk-mode
     * substrate run, no per-app branches.
     */
    private String resolveProbeApkPath() {
        if (mContext != null) {
            try {
                String p = mContext.getPackageResourcePath();
                if (p != null && p.length() > 0) {
                    return p;
                }
            } catch (Throwable ignored) {
            }
        }
        try {
            String prop = System.getProperty("westlake.apk.path");
            if (prop != null && prop.length() > 0) {
                return prop;
            }
        } catch (Throwable ignored) {
        }
        try {
            String prop = System.getProperty("westlake.apk.sourceDir");
            if (prop != null && prop.length() > 0) {
                return prop;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Returns the "type/name" resource name from the apk's arsc, or null.
     * Caches its result via the same per-apk ResourceTable as
     * {@link #probeArscIdentifier(String)}. Generic across any
     * AppCompat-using app.
     */
    private String probeArscResourceName(int resource) {
        if (resource == 0) {
            return null;
        }
        ArscIdTable table = loadArscIdCached();
        if (table == null || table == ARSC_ID_NONE) {
            return null;
        }
        return table.namesById.get(Integer.valueOf(resource));
    }

    /**
     * Loads the resources.arsc identifier table from the Context's apk path
     * and returns the id for "id/{fieldName}", or 0 if not found / arsc not
     * loadable. Cached per apk-path so the parse runs at most once per
     * Context. Generic — applies to any AppCompat-using app, no per-app
     * branches.
     */
    private int probeArscIdentifier(String fieldName) {
        if (fieldName == null) {
            return 0;
        }
        ArscIdTable table = loadArscIdCached();
        if (table == null || table == ARSC_ID_NONE) {
            return 0;
        }
        Integer v = table.idsByName.get("id/" + fieldName);
        return v != null ? v.intValue() : 0;
    }

    /**
     * Loads (and caches) the apk's arsc identifier table. Cached per apk
     * path so subsequent lookups are free. Returns {@link #ARSC_ID_NONE} on
     * parse failure; null only if no apk path could be resolved.
     */
    private ArscIdTable loadArscIdCached() {
        String apkPath = resolveProbeApkPath();
        if (apkPath == null || apkPath.isEmpty()) {
            return null;
        }
        ArscIdTable table = sArscIdTableCache.get(apkPath);
        if (table == null) {
            table = parseArscIdentifiers(apkPath);
            if (table == null || table.empty()) {
                table = ARSC_ID_NONE;
            }
            sArscIdTableCache.put(apkPath, table);
            try {
                android.util.Log.i("Westlake",
                        "[LayoutInflater] arsc parse apk=" + apkPath
                                + " entries=" + (table == ARSC_ID_NONE
                                        ? 0 : table.namesById.size()));
            } catch (Throwable ignored) {
            }
        }
        return table;
    }

    /**
     * Produces a list of candidate `*.R$id` class names to probe when looking
     * up an AppCompat resource ID. R8/ProGuard frequently strips the
     * `androidx.appcompat.R$id` class because it's just a holder of `int`
     * constants — the constant values get inlined at usage sites and the
     * holder ends up merged into one of the app's own R classes (e.g.
     * `com.github.appintro.R$id` for noice). Probing common candidates is
     * a generic AppCompat-shim concern, not a per-app branch.
     */
    private static String[] candidateRIdClassNames(String original, String pkg) {
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        if (original != null) {
            list.add(original);
        }
        if (pkg != null && !pkg.isEmpty()) {
            list.add(pkg + ".R$id");
            // App-bundle pattern: many R8 builds merge into a feature-module
            // R class. Walk parent packages.
            int dot = pkg.lastIndexOf('.');
            while (dot > 0) {
                String parent = pkg.substring(0, dot);
                list.add(parent + ".R$id");
                dot = parent.lastIndexOf('.');
            }
        }
        // Common AppCompat-ish landing spots used by R8 when it merges R.
        list.add("androidx.appcompat.R$id");
        list.add("com.google.android.material.R$id");
        return list.toArray(new String[0]);
    }

    private void addCompatChild(ViewGroup parent, View child) {
        if (parent == null || child == null) {
            return;
        }
        boolean fillHeight = "androidx.appcompat.widget.ContentFrameLayout"
                .equals(child.getClass().getName());
        ViewGroup.LayoutParams lp;
        if (parent instanceof LinearLayout) {
            lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    fillHeight ? ViewGroup.LayoutParams.MATCH_PARENT
                            : ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (parent instanceof android.widget.FrameLayout) {
            lp = new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    fillHeight ? ViewGroup.LayoutParams.MATCH_PARENT
                            : ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (parent instanceof android.widget.RelativeLayout) {
            lp = new android.widget.RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    fillHeight ? ViewGroup.LayoutParams.MATCH_PARENT
                            : ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    fillHeight ? ViewGroup.LayoutParams.MATCH_PARENT
                            : ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        parent.addView(child, lp);
    }

    private View buildMcdActivityHomeDashboard() {
        LinearLayout root = new LinearLayout(mContext);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        android.widget.FrameLayout frame = new android.widget.FrameLayout(mContext);
        root.addView(frame, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout intermediate = new LinearLayout(mContext);
        intermediate.setOrientation(LinearLayout.VERTICAL);
        intermediate.setId(MCD_ID_INTERMEDIATE_LAYOUT_CONTAINER);
        intermediate.setVisibility(View.VISIBLE);
        addMcdDashboardFrameChild(frame, intermediate);

        LinearLayout home = new LinearLayout(mContext);
        home.setOrientation(LinearLayout.VERTICAL);
        home.setId(MCD_ID_HOME_DASHBOARD_CONTAINER);
        home.setVisibility(View.GONE);
        addMcdDashboardFrameChild(frame, home);

        LinearLayout splashCampaign = new LinearLayout(mContext);
        splashCampaign.setOrientation(LinearLayout.VERTICAL);
        splashCampaign.setId(MCD_ID_SPLASH_CAMPAIGN_LAYOUT_CONTAINER);
        splashCampaign.setVisibility(View.GONE);
        addMcdDashboardFrameChild(frame, splashCampaign);

        LinearLayout splashInterrupter = new LinearLayout(mContext);
        splashInterrupter.setOrientation(LinearLayout.VERTICAL);
        splashInterrupter.setId(MCD_ID_SPLASH_INTERRUPTER_LAYOUT_CONTAINER);
        splashInterrupter.setVisibility(View.GONE);
        addMcdDashboardFrameChild(frame, splashInterrupter);

        LinearLayout coachmark = new LinearLayout(mContext);
        coachmark.setOrientation(LinearLayout.VERTICAL);
        coachmark.setId(MCD_ID_COACHMARK_CONTAINER);
        coachmark.setVisibility(View.GONE);
        addMcdDashboardFrameChild(frame, coachmark);

        return root;
    }

    private void addMcdDashboardFrameChild(android.widget.FrameLayout frame, View child) {
        if (frame == null || child == null) {
            return;
        }
        frame.addView(child, new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private View buildMcdActivitySimpleProduct() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        android.widget.FrameLayout holder = new android.widget.FrameLayout(mContext);
        holder.setId(MCD_ID_SIMPLE_PRODUCT_HOLDER);
        holder.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        root.addView(holder, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        android.widget.ImageView peel = new android.widget.ImageView(mContext);
        peel.setId(MCD_ID_HEADER_PEEL);
        peel.setFocusable(true);
        peel.setFocusableInTouchMode(true);
        peel.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams peelLp =
                new android.widget.RelativeLayout.LayoutParams(73, 72);
        peelLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
        peelLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        root.addView(peel, peelLp);

        android.widget.ImageView backClose = new android.widget.ImageView(mContext);
        backClose.setId(MCD_ID_NAVIGATION_BACK_CLOSE);
        backClose.setFocusable(true);
        backClose.setFocusableInTouchMode(true);
        backClose.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams backCloseLp =
                new android.widget.RelativeLayout.LayoutParams(48, 48);
        backCloseLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
        backCloseLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_LEFT);
        root.addView(backClose, backCloseLp);

        android.widget.ImageView clone = new android.widget.ImageView(mContext);
        clone.setId(MCD_ID_PRODUCT_IMAGE_CLONE);
        clone.setVisibility(View.GONE);
        root.addView(clone, new android.widget.RelativeLayout.LayoutParams(220, 220));

        android.widget.ImageView rightClone = new android.widget.ImageView(mContext);
        rightClone.setId(MCD_ID_PRODUCT_IMAGE_RIGHT_CLONE);
        rightClone.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams rightCloneLp =
                new android.widget.RelativeLayout.LayoutParams(220, 220);
        rightCloneLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        root.addView(rightClone, rightCloneLp);

        try {
            System.out.println(
                    "MCD_ACTIVITY_SIMPLE_PRODUCT_SHELL layout=activity_simple_product holder=0x"
                            + Integer.toHexString(MCD_ID_SIMPLE_PRODUCT_HOLDER));
        } catch (Throwable ignored) {
        }
        return root;
    }

    private void ensureMcdSimpleProductShell(ViewGroup pageContent) {
        if (pageContent == null) {
            return;
        }
        if (pageContent.findViewById(MCD_ID_SIMPLE_PRODUCT_HOLDER) != null
                && pageContent.findViewById(MCD_ID_PRODUCT_IMAGE_CLONE) != null) {
            return;
        }
        View shell = buildMcdActivitySimpleProduct();
        if (shell == null) {
            return;
        }
        if (pageContent instanceof LinearLayout) {
            pageContent.addView(shell, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else if (pageContent instanceof android.widget.FrameLayout) {
            pageContent.addView(shell, new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            pageContent.addView(shell, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private View buildMcdBaseLayout() {
        androidx.drawerlayout.widget.DrawerLayout drawer =
                new androidx.drawerlayout.widget.DrawerLayout(mContext);
        drawer.setId(MCD_ID_DRAWER_LAYOUT);
        drawer.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        android.widget.FrameLayout content = new android.widget.FrameLayout(mContext);
        content.setId(MCD_ID_CONTENT_VIEW);
        content.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        LinearLayout column = new LinearLayout(mContext);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        com.mcdonalds.mcduikit.widget.McDToolBarView toolbar =
                new com.mcdonalds.mcduikit.widget.McDToolBarView(mContext);
        toolbar.setId(MCD_ID_TOOLBAR);
        addCompatChild(column, toolbar);

        LinearLayout storeSearch = new LinearLayout(mContext);
        storeSearch.setId(MCD_ID_STORE_SEARCH_LAYOUT);
        storeSearch.setOrientation(LinearLayout.VERTICAL);
        storeSearch.setVisibility(View.GONE);
        addCompatChild(column, storeSearch);

        LinearLayout pageRoot = new LinearLayout(mContext);
        pageRoot.setId(MCD_ID_PAGE_ROOT);
        pageRoot.setOrientation(LinearLayout.VERTICAL);

        android.widget.FrameLayout pageContentHolder = new android.widget.FrameLayout(mContext);
        pageContentHolder.setId(MCD_ID_PAGE_CONTENT_HOLDER);

        LinearLayout pageContent = new LinearLayout(mContext);
        pageContent.setId(MCD_ID_PAGE_CONTENT);
        pageContent.setOrientation(LinearLayout.VERTICAL);
        ensureMcdSimpleProductShell(pageContent);

        pageContentHolder.addView(pageContent, new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        pageRoot.addView(pageContentHolder, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        column.addView(pageRoot, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        com.mcdonalds.mcduikit.widget.McDTextView fullMenu =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        fullMenu.setId(MCD_ID_VIEW_FULL_MENU_LAYOUT_TO_ANIMATE);
        fullMenu.setVisibility(View.GONE);
        addCompatChild(column, fullMenu);

        LinearLayout bottomBag = new LinearLayout(mContext);
        bottomBag.setId(MCD_ID_BOTTOM_BAG);
        bottomBag.setOrientation(LinearLayout.VERTICAL);
        bottomBag.setVisibility(View.GONE);
        addCompatChild(column, bottomBag);

        android.widget.FrameLayout bagBarV2 = new android.widget.FrameLayout(mContext);
        bagBarV2.setId(MCD_ID_BAG_BAR_V2);
        bagBarV2.setVisibility(View.GONE);
        addCompatChild(column, bagBarV2);

        View navigation = buildMcdBottomNavigationBar();
        addCompatChild(column, navigation);

        addCompatChild(content, column);

        View transparent = new View(mContext);
        transparent.setId(MCD_ID_TRANSPARENT_VIEW);
        transparent.setVisibility(View.GONE);
        addCompatChild(content, transparent);

        addCompatChild(drawer, content);
        return drawer;
    }

    private View buildMcdBasketHolder() {
        android.widget.FrameLayout root = new android.widget.FrameLayout(mContext);
        root.setBackgroundColor(0x00000000);
        diag("[LayoutInflater] basket_holder build OK: FrameLayout");
        return root;
    }

    private View buildMcdBottomNavigationBar() {
        com.google.android.material.bottomnavigation.BottomNavigationView nav =
                new com.google.android.material.bottomnavigation.BottomNavigationView(mContext);
        nav.setId(MCD_ID_NAVIGATION);
        nav.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        return nav;
    }

    private View buildMcdBottomBagBar() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setBackgroundColor(0x00000000);

        View shadow = new View(mContext);
        shadow.setId(MCD_ID_BOTTOM_BAG_SHADOW);
        shadow.setBackgroundColor(0x33000000);
        android.widget.RelativeLayout.LayoutParams shadowLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 3);
        root.addView(shadow, shadowLp);

        android.widget.RelativeLayout bar = new android.widget.RelativeLayout(mContext);
        bar.setId(MCD_ID_BOTTOM_BAG_BAR_LAYOUT);
        bar.setFocusable(true);
        bar.setBackgroundColor(0xFF27251F);

        com.mcdonalds.mcduikit.widget.McDTextView added =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        added.setId(MCD_ID_ADD_TO_ORDER);
        added.setTextColor(0xFFFFFFFF);
        added.setVisibility(View.GONE);
        bar.addView(added, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout checkout = new LinearLayout(mContext);
        checkout.setId(MCD_ID_CHECKOUT_LAYOUT);
        checkout.setOrientation(LinearLayout.HORIZONTAL);
        checkout.setVisibility(View.VISIBLE);

        com.mcdonalds.mcduikit.widget.McDTextView checkoutText =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        checkoutText.setId(MCD_ID_CHECKOUT_NOW_TEXT_VIEW);
        checkoutText.setTextColor(0xFFFFFFFF);
        checkout.addView(checkoutText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.ImageView chevron = new android.widget.ImageView(mContext);
        chevron.setId(MCD_ID_CHEVRON);
        checkout.addView(chevron, new LinearLayout.LayoutParams(12, 12));

        android.widget.RelativeLayout.LayoutParams checkoutLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        checkoutLp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
        bar.addView(checkout, checkoutLp);

        android.widget.ImageView bagError = new android.widget.ImageView(mContext);
        bagError.setId(MCD_ID_BAG_ERROR);
        bagError.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams bagErrorLp =
                new android.widget.RelativeLayout.LayoutParams(32, 32);
        bagErrorLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        bagErrorLp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
        bar.addView(bagError, bagErrorLp);

        android.widget.RelativeLayout.LayoutParams barLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 50);
        barLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bar, barLp);

        com.airbnb.lottie.LottieAnimationView animation =
                new com.airbnb.lottie.LottieAnimationView(mContext);
        animation.setId(MCD_ID_BAG_ANIMATION_VIEW);
        animation.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams animationLp =
                new android.widget.RelativeLayout.LayoutParams(90, 90);
        animationLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        root.addView(animation, animationLp);

        com.mcdonalds.mcduikit.widget.McDTextView quantity =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        quantity.setId(MCD_ID_BAG_QUANTITY_TEXT);
        quantity.setTextColor(0xFFFFFFFF);
        quantity.setBackgroundColor(0xFFDA291C);
        android.widget.RelativeLayout.LayoutParams quantityLp =
                new android.widget.RelativeLayout.LayoutParams(18, 18);
        quantityLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        root.addView(quantity, quantityLp);

        return root;
    }

    private View buildMcdApplicationNotification(boolean campaign) {
        androidx.constraintlayout.widget.ConstraintLayout root =
                new androidx.constraintlayout.widget.ConstraintLayout(mContext);
        root.setId(MCD_ID_ROOT);
        root.setTag(campaign
                ? "layout/campaign_application_notification_0"
                : "layout/application_notification_0");
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        android.widget.RelativeLayout alert = new android.widget.RelativeLayout(mContext);
        alert.setId(MCD_ID_TOAST_NOTIFICATION_ALERT_INFO);
        alert.setFocusable(true);
        alert.setPadding(24, 16, 12, 16);

        android.widget.ImageView icon = new android.widget.ImageView(mContext);
        icon.setId(MCD_ID_ICON_ALERT_TYPE_WARNING);
        android.widget.RelativeLayout.LayoutParams iconLp =
                new android.widget.RelativeLayout.LayoutParams(32, 32);
        iconLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_LEFT);
        iconLp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
        alert.addView(icon, iconLp);

        android.widget.ImageView disclosure = new android.widget.ImageView(mContext);
        disclosure.setId(MCD_ID_DISCLOSURE_ICON_ALERT_TYPE_WARNING);
        disclosure.setVisibility(View.GONE);
        android.widget.RelativeLayout.LayoutParams disclosureLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        disclosureLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        disclosureLp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
        alert.addView(disclosure, disclosureLp);

        android.widget.TextView message = campaign
                ? createCampaignNotificationTextView()
                : new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        message.setId(MCD_ID_TEXT_ALERT_TYPE_WARNING);
        message.setTextColor(getColorCompat(0x7f06066c, 0xFF292929));
        message.setTextSize(14);
        message.setSingleLine(false);
        android.widget.RelativeLayout.LayoutParams messageLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        messageLp.addRule(android.widget.RelativeLayout.RIGHT_OF, MCD_ID_ICON_ALERT_TYPE_WARNING);
        messageLp.addRule(android.widget.RelativeLayout.LEFT_OF, MCD_ID_DISCLOSURE_ICON_ALERT_TYPE_WARNING);
        messageLp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
        messageLp.leftMargin = 16;
        alert.addView(message, messageLp);

        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams alertLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        root.addView(alert, alertLp);

        View line = new View(mContext);
        line.setId(MCD_ID_VIEW_LINE);
        line.setBackgroundColor(getColorCompat(0x7f06015b, 0xFFDA291C));
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams lineLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(4, 1);
        root.addView(line, lineLp);

        try {
            System.out.println(
                    "MCD_APPLICATION_NOTIFICATION_LAYOUT_OK tag=" + root.getTag()
                            + " children=" + root.getChildCount());
        } catch (Throwable ignored) {
        }
        return root;
    }

    private android.widget.TextView createCampaignNotificationTextView() {
        View view = tryInstantiate(
                "com.mcdonalds.campaignsframework.campaigns.ui.view.CampaignTextView");
        if (view instanceof android.widget.TextView) {
            return (android.widget.TextView) view;
        }
        return new android.widget.TextView(mContext);
    }

    private View buildMcdFragmentHomeDashboard() {
        android.widget.ScrollView scroll = new android.widget.ScrollView(mContext);
        scroll.setId(MCD_ID_NESTED_SCROLL_VIEW);
        scroll.setFillViewport(true);

        LinearLayout parent = new LinearLayout(mContext);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setId(MCD_ID_PARENT_CONTAINER);
        parent.setFocusable(false);

        android.widget.RelativeLayout immersive = new android.widget.RelativeLayout(mContext);
        immersive.setId(MCD_ID_IMMERSIVE_CONTAINER);
        immersive.setVisibility(View.GONE);
        immersive.setMinimumHeight(getDimenCompat(MCD_DIMEN_409, 409));
        addCompatChild(parent, immersive);

        LinearLayout sections = new LinearLayout(mContext);
        sections.setOrientation(LinearLayout.VERTICAL);
        sections.setId(MCD_ID_SECTIONS_CONTAINER);
        sections.setLayoutTransition(new android.animation.LayoutTransition());
        addCompatChild(parent, sections);

        addCompatChild(scroll, parent);
        return scroll;
    }

    private View buildMcdFragmentDealSection(boolean loyaltyVariant) {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        LinearLayout loaded = new LinearLayout(mContext);
        loaded.setOrientation(LinearLayout.VERTICAL);
        loaded.setId(MCD_ID_LOADED_VIEW3);
        loaded.setVisibility(View.VISIBLE);

        LinearLayout headingRow = new LinearLayout(mContext);
        headingRow.setOrientation(LinearLayout.HORIZONTAL);

        com.mcdonalds.mcduikit.widget.McDTextView heading =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        heading.setId(MCD_ID_HOME_SCREEN_DEAL_TITLE_HEADING_TEXT);
        heading.setText(loyaltyVariant ? "Rewards & Deals" : "Deals");
        heading.setTextSize(20);
        heading.setTextColor(0xFF292929);
        heading.setGravity(android.view.Gravity.CENTER_VERTICAL);
        headingRow.addView(heading, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout viewAll = new LinearLayout(mContext);
        viewAll.setId(MCD_ID_HOME_SCREEN_DEALS_HEADING_VIEW_ALL_TEXT);
        viewAll.setOrientation(LinearLayout.HORIZONTAL);
        viewAll.setVisibility(View.GONE);
        com.mcdonalds.mcduikit.widget.McDTextView viewAllText =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        viewAllText.setId(MCD_ID_HOME_SCREEN_DEALS_HEADING_VIEW_FULL_MENU_TEXT);
        viewAllText.setText("View All");
        viewAllText.setTextSize(14);
        viewAllText.setTextColor(0xFF2860A8);
        viewAll.addView(viewAllText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        headingRow.addView(viewAll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loaded.addView(headingRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout dealLayout = new LinearLayout(mContext);
        dealLayout.setId(MCD_ID_DEAL_LAYOUT);
        dealLayout.setOrientation(LinearLayout.VERTICAL);
        dealLayout.setVisibility(View.VISIBLE);
        androidx.recyclerview.widget.RecyclerView deals =
                new androidx.recyclerview.widget.RecyclerView(mContext);
        deals.setId(loyaltyVariant ? MCD_ID_REWARDS_AND_DEALS_SCREEN_DEAL_CAROUSELS_HOLDER
                : MCD_ID_HOME_SCREEN_DEAL_CAROUSELS_LIST);
        dealLayout.addView(deals, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 190));
        loaded.addView(dealLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.RelativeLayout noDeals = new android.widget.RelativeLayout(mContext);
        noDeals.setId(loyaltyVariant ? MCD_ID_REWARDS_AND_DEALS_SCREEN_NO_DEALS_DEAL_CONTAINER_LAYOUT
                : MCD_ID_HOME_SCREEN_NO_DEALS_DEAL_CONTAINER_LAYOUT);
        noDeals.setVisibility(View.GONE);

        LinearLayout noDealsText = new LinearLayout(mContext);
        noDealsText.setOrientation(LinearLayout.VERTICAL);

        com.mcdonalds.mcduikit.widget.McDTextView noDealsTitle =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        noDealsTitle.setId(MCD_ID_HOME_SCREEN_NO_DEALS_DEAL_TITLE_TEXT_VIEW);
        noDealsTitle.setText("Deals");
        noDealsTitle.setTextSize(18);
        noDealsTitle.setTextColor(0xFF292929);
        noDealsText.addView(noDealsTitle, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView noDealsHeader =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        noDealsHeader.setId(MCD_ID_HOME_SCREEN_NO_DEALS_HEADER_TEXT);
        noDealsHeader.setText("Stay tuned");
        noDealsHeader.setTextSize(18);
        noDealsHeader.setTextColor(0xFF292929);
        noDealsText.addView(noDealsHeader, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView noDealsSubHeader =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        noDealsSubHeader.setId(MCD_ID_HOME_SCREEN_NO_DEALS_SUB_HEADER_TEXT);
        noDealsSubHeader.setText("More deals coming soon");
        noDealsSubHeader.setTextSize(14);
        noDealsSubHeader.setTextColor(0xFF6F6F6F);
        noDealsText.addView(noDealsSubHeader, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView viewDeals =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        viewDeals.setId(MCD_ID_HOME_SCREEN_NO_DEALS_DEALS_VIEW_DEALS_BUTTON);
        viewDeals.setText("View Deals");
        noDealsText.addView(viewDeals, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        noDeals.addView(noDealsText, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loaded.addView(noDeals, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addCompatChild(root, loaded);

        LinearLayout shimmer = new LinearLayout(mContext);
        shimmer.setId(MCD_ID_SHIMMER_VIEW3);
        shimmer.setOrientation(LinearLayout.VERTICAL);
        shimmer.setVisibility(View.GONE);
        addCompatChild(root, shimmer);
        return root;
    }

    private com.mcdonalds.mcduikit.widget.McDTextView createMcdText(
            int id, String text, float textSize, int color) {
        com.mcdonalds.mcduikit.widget.McDTextView view =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        view.setId(id);
        view.setText(text);
        view.setTextSize(textSize);
        view.setTextColor(color);
        view.setSingleLine(false);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    private TextView createStockMcdText(int id, String text, float textSize, int color) {
        View appView = tryInstantiateAppClassView("com.mcdonalds.mcduikit.widget.McDTextView");
        TextView view;
        if (appView instanceof TextView) {
            view = (TextView) appView;
        } else {
            view = new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        }
        view.setId(id);
        view.setText(text);
        view.setTextSize(textSize);
        view.setTextColor(color);
        view.setSingleLine(false);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    private ViewGroup createStockCardView(int id, int color, float radius) {
        View appView = tryInstantiateAppClassView("androidx.cardview.widget.CardView");
        ViewGroup card;
        if (appView instanceof ViewGroup) {
            card = (ViewGroup) appView;
        } else {
            card = new androidx.cardview.widget.CardView(mContext);
        }
        card.setId(id);
        card.setVisibility(View.VISIBLE);
        card.setBackgroundColor(color);
        invokeCompatSetter(card, "setCardBackgroundColor", int.class, Integer.valueOf(color));
        invokeCompatSetter(card, "setRadius", float.class, Float.valueOf(radius));
        invokeCompatSetter(card, "setCardElevation", float.class, Float.valueOf(2f));
        invokeCompatSetter(card, "setUseCompatPadding", boolean.class, Boolean.TRUE);
        return card;
    }

    private void invokeCompatSetter(View view, String methodName, Class paramType, Object value) {
        if (view == null) {
            return;
        }
        try {
            view.getClass().getMethod(methodName, paramType).invoke(view, value);
        } catch (Throwable ignored) {
        }
    }

    private View createShimmerBlock(int id, int height) {
        View block = new View(mContext);
        block.setId(id);
        block.setBackgroundColor(0xFFE4E4E4);
        block.setFocusable(true);
        block.setVisibility(View.VISIBLE);
        block.setContentDescription("");
        block.setMinimumHeight(height);
        return block;
    }

    private com.facebook.shimmer.ShimmerFrameLayout createShimmerLine(int id, int height) {
        com.facebook.shimmer.ShimmerFrameLayout line =
                new com.facebook.shimmer.ShimmerFrameLayout(mContext);
        line.setId(id);
        line.setBackgroundColor(0xFFEDEDED);
        line.setMinimumHeight(height);
        line.setContentDescription("");
        return line;
    }

    private android.widget.ImageView createAppCompatImageViewCompat(int id) {
        View view = tryInstantiateAppClassView("androidx.appcompat.widget.AppCompatImageView");
        if (!(view instanceof android.widget.ImageView)) {
            view = new androidx.appcompat.widget.AppCompatImageView(mContext);
        }
        view.setId(id);
        return (android.widget.ImageView) view;
    }

    private androidx.recyclerview.widget.RecyclerView buildSeededRecyclerView(
            int id, final String[] labels, boolean horizontal) {
        androidx.recyclerview.widget.RecyclerView recycler =
                new androidx.recyclerview.widget.RecyclerView(mContext);
        recycler.setId(id);
        recycler.setVisibility(View.VISIBLE);
        recycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                mContext,
                horizontal ? androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
                        : androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false));
        recycler.setAdapter(new androidx.recyclerview.widget.RecyclerView.Adapter() {
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(
                    ViewGroup parent, int viewType) {
                LinearLayout card = new LinearLayout(mContext);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(18, 14, 18, 14);
                card.setBackgroundColor(0xFFFFFFFF);
                android.widget.ImageView image = new android.widget.ImageView(mContext);
                image.setBackgroundColor(0xFFFFC72C);
                card.addView(image, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 96));
                TextView title = new TextView(mContext);
                title.setId(0x1020014);
                title.setTextColor(0xFF292929);
                title.setTextSize(14);
                title.setSingleLine(false);
                card.addView(title, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new androidx.recyclerview.widget.RecyclerView.ViewHolder(card) {};
            }

            public void onBindViewHolder(
                    androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
                View title = holder.itemView.findViewById(0x1020014);
                String fallback = labels != null && position >= 0 && position < labels.length
                        ? labels[position] : "";
                String label = resolveMcdLiveAdapterLabel(id, position, fallback);
                if (title instanceof TextView) {
                    ((TextView) title).setText(label);
                }
                final String clickLabel = label;
                holder.itemView.setClickable(true);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        openMcdCategoryDetail(v, clickLabel);
                    }
                });
                if (holder.itemView instanceof ViewGroup) {
                    View image = null;
                    try {
                        image = ((ViewGroup) holder.itemView).getChildAt(0);
                    } catch (Throwable ignored) {
                    }
                    if (image instanceof android.widget.ImageView) {
                        String imageHint = (id == MCD_ID_POPULAR_LIST
                                || id == MCD_ID_PROMOTION_RECYCLER_LIST)
                                ? fallback : label;
                        byte[] imageBytes = resolveMcdLiveAdapterImageBytes(
                                id, position, imageHint);
                        if (imageBytes != null && imageBytes.length > 0) {
                            android.graphics.Bitmap bitmap =
                                    android.graphics.BitmapFactory.decodeByteArray(
                                            imageBytes, 0, imageBytes.length);
                            if (bitmap != null) {
                                ((android.widget.ImageView) image).setImageBitmap(bitmap);
                            }
                        }
                    }
                }
            }

            public int getItemCount() {
                return labels != null ? labels.length : 0;
            }
        });
        return recycler;
    }

    private View tryInflateRealMcdXmlLayout(int layoutId, String layoutName) {
        View view = null;
        try {
            view = inflate(layoutId, null, false);
        } catch (Throwable t) {
            try {
                System.out.println(
                        "MCD_REAL_XML_INFLATE_FAILED layout=" + safeMcdMarkerToken(layoutName)
                                + " err=" + throwableName(t) + ":" + t.getMessage());
            } catch (Throwable ignored) {
            }
            return null;
        }
        if (view == null || view.getId() == layoutId) {
            try {
                System.out.println(
                        "MCD_REAL_XML_INFLATE_FALLBACK layout="
                                + safeMcdMarkerToken(layoutName));
            } catch (Throwable ignored) {
            }
            return null;
        }
        try {
            System.out.println(
                    "MCD_REAL_XML_INFLATED layout=" + safeMcdMarkerToken(layoutName)
                            + " root=" + simpleClassNameOf(view));
        } catch (Throwable ignored) {
        }
        return view;
    }

    private View tryBuildMcdRealXmlOrderPanel(boolean compact) {
        View orderRoot = tryInflateRealMcdXmlLayout(
                MCD_LAYOUT_FRAGMENT_ORDER, "fragment_order");
        if (!(orderRoot instanceof ViewGroup)) {
            return null;
        }
        orderRoot.setVisibility(View.VISIBLE);
        orderRoot.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        View scroll = orderRoot.findViewById(MCD_ID_ORDER_SCROLL_VIEW);
        if (scroll != null) {
            scroll.setVisibility(View.VISIBLE);
            scroll.setMinimumHeight(compact ? 330 : 620);
        }

        setMcdTextIfPresent(orderRoot, MCD_ID_EXPLORE_OUR_MENU,
                "Explore our menu", 22, 0xFF292929, true);

        View categoriesView = orderRoot.findViewById(MCD_ID_CATEGORIES);
        if (!(categoriesView instanceof ViewGroup)) {
            return null;
        }
        ViewGroup categories = (ViewGroup) categoriesView;
        categories.setVisibility(View.VISIBLE);
        categories.removeAllViews();
        categories.setBackgroundColor(getColorCompat(MCD_COLOR_GREY_BG, 0xFFF7F7F7));

        String[] fallbacks = new String[] {
                "Breakfast", "Extra Value Meals", "Burgers",
                "Chicken", "Fries", "McCafe"
        };
        int added = 0;
        for (int i = 0; i < fallbacks.length; i++) {
            String label = resolveMcdLiveAdapterLabel(
                    MCD_ID_MENU_CATEGORY_LIST, i, fallbacks[i]);
            View row = buildMcdRealXmlCategoryRow(i, label);
            if (row == null) {
                continue;
            }
            categories.addView(row, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            added++;
        }
        try {
            System.out.println(
                    "MCD_REAL_XML_ORDER_INFLATED layout=fragment_order categories="
                            + added + " compact=" + compact);
        } catch (Throwable ignored) {
        }
        return orderRoot;
    }

    private View buildMcdRealXmlCategoryRow(final int position, final String label) {
        View row = tryInflateRealMcdXmlLayout(
                MCD_LAYOUT_CATEGORY_LIST_ITEM, "category_list_item");
        if (row == null) {
            LinearLayout fallback = new LinearLayout(mContext);
            fallback.setOrientation(LinearLayout.HORIZONTAL);
            fallback.setPadding(24, 20, 24, 20);
            android.widget.ImageView image = new android.widget.ImageView(mContext);
            image.setId(MCD_ID_CATEGORY_IMAGE);
            fallback.addView(image, new LinearLayout.LayoutParams(88, 88));
            TextView text = createMcdText(MCD_ID_CATEGORY_NAME,
                    label != null ? label : "Menu", 18, 0xFF292929);
            fallback.addView(text, new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row = fallback;
        }

        row.setVisibility(View.VISIBLE);
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMcdCategoryDetail(v, label);
            }
        });
        row.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        setMcdTextIfPresent(row, MCD_ID_CATEGORY_NAME,
                label != null ? label : "Menu", 18, 0xFF292929, true);
        setMcdImageIfPresent(row, MCD_ID_CATEGORY_IMAGE,
                MCD_ID_MENU_CATEGORY_LIST, position, label);
        try {
            System.out.println(
                    "MCD_REAL_XML_CATEGORY_ROW_INFLATED layout=category_list_item label="
                            + safeMcdMarkerToken(label));
        } catch (Throwable ignored) {
        }
        return row;
    }

    private View tryBuildMcdRealXmlProductDetail(String label) {
        View product = tryInflateRealMcdXmlLayout(
                MCD_LAYOUT_NEW_PLP_PRODUCT_ITEM, "new_plp_product_item");
        if (product == null) {
            return null;
        }
        String productLabel = label != null && label.length() > 0
                ? label : "Featured Item";
        setMcdTextIfPresent(product, MCD_ID_PLP_PRODUCT_NAME_TEXT,
                productLabel, 18, 0xFF292929, true);
        setMcdTextIfPresent(product, MCD_ID_PLP_PRODUCT_CALORIE_PRICE_TEXT,
                "450 Cal - $5.99", 14, 0xFF292929, true);
        setMcdImageIfPresent(product, MCD_ID_PLP_PRODUCT_IMAGE,
                MCD_ID_MENU_CATEGORY_LIST, 0, productLabel);
        product.setVisibility(View.VISIBLE);

        LinearLayout panel = new LinearLayout(mContext);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(24, 14, 24, 14);
        panel.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        panel.addView(product, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView add = createMcdText(MCD_ID_ADD_TO_ORDER,
                "Add to Bag", 15, 0xFF292929);
        add.setGravity(android.view.Gravity.CENTER);
        add.setBackgroundColor(0xFFFFC72C);
        add.setClickable(true);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    System.out.println(
                            "MCD_ADD_TO_BAG_CLICKED source=real_xml_product_detail");
                } catch (Throwable ignored) {
                }
            }
        });
        panel.addView(add, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 46));

        View bag = buildMcdBottomBagBar();
        LinearLayout.LayoutParams bagLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 54);
        bagLp.topMargin = 8;
        panel.addView(bag, bagLp);

        try {
            System.out.println(
                    "MCD_REAL_XML_CATEGORY_DETAIL_INFLATED layout=new_plp_product_item label="
                            + safeMcdMarkerToken(productLabel));
        } catch (Throwable ignored) {
        }
        return panel;
    }

    private void enhanceMcdInflatedLayout(int resource, View view) {
        if (resource == MCD_LAYOUT_FRAGMENT_ORDER_PDP && view != null) {
            enhanceMcdFragmentOrderPdp(view);
            maybeAttachMcdPdpToCurrentActivity(view);
        }
    }

    private void enhanceMcdFragmentOrderPdp(View root) {
        try {
            System.out.println(
                    "MCD_REAL_XML_PDP_ENHANCE_BEGIN");
        } catch (Throwable ignored) {
        }
        root.setVisibility(View.VISIBLE);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        View scroll = root.findViewById(MCD_ID_PRODUCT_DETAILS_SCROLL);
        if (scroll != null) {
            scroll.setVisibility(View.VISIBLE);
            scroll.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
            if (scroll instanceof android.widget.ScrollView) {
                ((android.widget.ScrollView) scroll).setFillViewport(true);
            }
            ViewGroup.LayoutParams lp = scroll.getLayoutParams();
            if (lp == null) {
                lp = new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                scroll.setLayoutParams(lp);
            } else {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            scroll.setMinimumHeight(720);
        }

        View main = root.findViewById(MCD_ID_MAIN_CONTENT_PDP);
        if (main != null) {
            main.setVisibility(View.VISIBLE);
            main.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
            forceViewLayoutSize(main,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            main.setMinimumHeight(860);
        }

        View productInfo = root.findViewById(MCD_ID_PRODUCT_INFORMATION);
        if (productInfo instanceof ViewGroup
                && root.findViewById(MCD_ID_PDP_PRODUCT_NAME_TEXT) == null) {
            ViewGroup info = (ViewGroup) productInfo;
            info.setVisibility(View.VISIBLE);
            info.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
            forceViewLayoutSize(info,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    380);
            info.setMinimumHeight(380);
            if (info instanceof LinearLayout) {
                ((LinearLayout) info).setOrientation(LinearLayout.VERTICAL);
            }
            String productLabel = resolveMcdLiveAdapterLabel(
                    MCD_ID_POPULAR_LIST, 0, "Big Mac");

            android.widget.ImageView image = new android.widget.ImageView(mContext);
            image.setId(MCD_ID_PRODUCT_IMAGE);
            image.setBackgroundColor(0xFFFFC72C);
            image.setVisibility(View.VISIBLE);
            image.setMinimumHeight(270);
            addMcdPdpInfoChild(info, image, 270);

            TextView title = createMcdText(MCD_ID_PDP_PRODUCT_NAME_TEXT,
                    productLabel, 26, 0xFF292929);
            title.setPadding(24, 18, 24, 4);
            title.setMinimumHeight(58);
            addMcdPdpInfoChild(info, title, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView calories = createMcdText(MCD_ID_PRODUCT_PRICE_CALORIE,
                    "540 Cal - $5.99", 15, 0xFF545454);
            calories.setPadding(24, 0, 24, 18);
            calories.setMinimumHeight(42);
            addMcdPdpInfoChild(info, calories, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        setMcdTextIfPresent(root, MCD_ID_PDP_QUANTITY_TEXT,
                "1", 22, 0xFF292929, true);
        setMcdTextIfPresent(root, MCD_ID_PDP_CUSTOMIZE_INGREDIENTS_BUTTON,
                "Customize", 14, 0xFF292929, true);
        setMcdTextIfPresent(root, MCD_ID_PDP_NUTRITION_INGREDIENT_BUTTON,
                "Nutrition & Ingredients", 16, 0xFF292929, true);
        setVisibleIfPresent(root, MCD_ID_PDP_MINUS_ICON);
        setVisibleIfPresent(root, MCD_ID_PDP_PLUS_ICON);
        hideIfPresent(root, MCD_ID_DEFAULT_SHIMMER);
        hideIfPresent(root, MCD_ID_CHOICE_CUSTOMIZATION_SHIMMER);
        setVisibleIfPresent(root, MCD_ID_BOTTOM_LAYOUT);

        try {
            System.out.println(
                    "MCD_REAL_XML_PDP_ENHANCED productInfo="
                            + (productInfo instanceof ViewGroup
                            ? ((ViewGroup) productInfo).getChildCount() : -1)
                            + " scroll=" + simpleClassNameOf(scroll)
                            + " normalized=true");
        } catch (Throwable ignored) {
        }
    }

    private void forceViewLayoutSize(View view, int width, int height) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(width, height);
        } else {
            lp.width = width;
            lp.height = height;
        }
        view.setLayoutParams(lp);
    }

    private void addMcdPdpInfoChild(ViewGroup parent, View child, int height) {
        if (parent == null || child == null) {
            return;
        }
        if (parent instanceof LinearLayout) {
            parent.addView(child, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        } else if (parent instanceof android.widget.FrameLayout) {
            parent.addView(child, new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        } else {
            parent.addView(child, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
    }

    private void maybeAttachMcdPdpToCurrentActivity(View pdpRoot) {
        if (pdpRoot == null) {
            return;
        }
        android.app.Activity activity = null;
        try {
            android.app.MiniActivityManager am =
                    android.app.MiniServer.currentActivityManager();
            activity = am != null ? am.getResumedActivity() : null;
        } catch (Throwable ignored) {
        }
        if (activity == null && mContext instanceof android.app.Activity) {
            activity = (android.app.Activity) mContext;
        }
        String activityName = classNameOf(activity);
        if (!"com.mcdonalds.order.activity.OrderProductDetailsActivity".equals(activityName)) {
            try {
                System.out.println(
                        "MCD_REAL_XML_PDP_DIRECT_ATTACH_SKIPPED activity="
                                + safeMcdMarkerToken(activityName));
            } catch (Throwable ignored) {
            }
            return;
        }

        View holder = null;
        try {
            holder = activity.findViewById(MCD_ID_SIMPLE_PRODUCT_HOLDER);
        } catch (Throwable ignored) {
        }
        if (holder == null) {
            try {
                android.view.Window window = activity.getWindow();
                View decor = window != null ? window.getDecorView() : null;
                holder = decor != null ? decor.findViewById(MCD_ID_SIMPLE_PRODUCT_HOLDER) : null;
            } catch (Throwable ignored) {
            }
        }
        if (!(holder instanceof ViewGroup)) {
            try {
                System.out.println(
                        "MCD_REAL_XML_PDP_DIRECT_ATTACH_NO_HOLDER activity="
                                + safeMcdMarkerToken(activityName)
                                + " holder=" + simpleClassNameOf(holder));
            } catch (Throwable ignored) {
            }
            return;
        }

        ViewGroup target = (ViewGroup) holder;
        try {
            target.setVisibility(View.VISIBLE);
            target.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
            target.setMinimumHeight(720);
            pdpRoot.setVisibility(View.VISIBLE);
            pdpRoot.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

            ViewParent currentParent = pdpRoot.getParent();
            if (currentParent == target) {
                pdpRoot.setLayoutParams(matchParentParamsFor(target));
                pdpRoot.requestLayout();
                target.requestLayout();
                target.invalidate();
                System.out.println(
                        "MCD_REAL_XML_PDP_DIRECT_ATTACH already=true childCount="
                                + target.getChildCount()
                                + " parentMatches=true tree=" + describeViewTree(target, 0));
                return;
            }
            if (currentParent instanceof ViewGroup) {
                ((ViewGroup) currentParent).removeView(pdpRoot);
            }
            if (target.getChildCount() > 0) {
                target.removeAllViews();
            }
            target.addView(pdpRoot, matchParentParamsFor(target));
            pdpRoot.requestLayout();
            target.requestLayout();
            target.invalidate();
            System.out.println(
                    "MCD_REAL_XML_PDP_DIRECT_ATTACH already=false childCount="
                            + target.getChildCount()
                            + " parentMatches=" + (pdpRoot.getParent() == target)
                            + " tree=" + describeViewTree(target, 0));
        } catch (Throwable t) {
            try {
                System.out.println(
                        "MCD_REAL_XML_PDP_DIRECT_ATTACH_ERROR err="
                                + throwableName(t) + ":" + safeMcdMarkerToken(t.getMessage()));
            } catch (Throwable ignored) {
            }
        }
    }

    private ViewGroup.LayoutParams matchParentParamsFor(ViewGroup parent) {
        if (parent instanceof FrameLayout) {
            return new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (parent instanceof LinearLayout) {
            return new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (parent instanceof android.widget.RelativeLayout) {
            return new android.widget.RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void setVisibleIfPresent(View root, int id) {
        if (root == null) {
            return;
        }
        View view = root.findViewById(id);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideIfPresent(View root, int id) {
        if (root == null) {
            return;
        }
        View view = root.findViewById(id);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void setMcdTextIfPresent(
            View root, int id, String text, int textSize, int color, boolean visible) {
        if (root == null) {
            return;
        }
        View view = root.findViewById(id);
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
            tv.setTextSize(textSize);
            tv.setTextColor(color);
            if (visible) {
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setMcdImageIfPresent(
            View root, int imageId, int recyclerId, int position, String labelHint) {
        if (root == null) {
            return;
        }
        View image = root.findViewById(imageId);
        if (!(image instanceof android.widget.ImageView)) {
            return;
        }
        image.setVisibility(View.VISIBLE);
        image.setBackgroundColor(0xFFFFC72C);
        byte[] imageBytes = resolveMcdLiveAdapterImageBytes(
                recyclerId, position, labelHint);
        if (imageBytes != null && imageBytes.length > 0) {
            android.graphics.Bitmap bitmap =
                    android.graphics.BitmapFactory.decodeByteArray(
                            imageBytes, 0, imageBytes.length);
            if (bitmap != null) {
                ((android.widget.ImageView) image).setImageBitmap(bitmap);
            }
        }
    }

    private static String safeMcdMarkerToken(String value) {
        if (value == null || value.length() == 0) {
            return "none";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9')) {
                sb.append(ch);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    private void openMcdCategoryDetail(View source, String label) {
        ViewGroup host = findAncestorViewGroup(source, MCD_ID_GUEST_TILE_LAYOUT);
        if (host == null) {
            host = findAncestorViewGroup(source, MCD_ID_NO_DATA_LAYOUT);
        }
        if (host == null) {
            host = findAncestorViewGroup(source, MCD_ID_MENU_LAYOUT);
        }
        if (host == null) {
            com.westlake.engine.WestlakeLauncher.recordMcdCategoryNavigation(
                    label, "no_host");
            return;
        }

        host.removeAllViews();
        host.setPadding(0, 0, 0, 0);
        host.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        View realXmlDetail = tryBuildMcdRealXmlProductDetail(label);
        if (realXmlDetail != null) {
            addPanelToHost(host, realXmlDetail);
            com.westlake.engine.WestlakeLauncher.recordMcdCategoryNavigation(
                    label, "category_detail_real_xml");
            return;
        }

        LinearLayout panel = new LinearLayout(mContext);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(24, 14, 24, 14);
        panel.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        panel.addView(createMcdText(MCD_ID_MENU_ITEM_TITLE,
                label != null && label.length() > 0 ? label : "Menu",
                22, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        panel.addView(createMcdText(MCD_ID_TEXT_EYE_BROW,
                "Pick a favorite and add it to your bag.", 14, 0xFF6F6F6F),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout itemRow = new LinearLayout(mContext);
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow.setPadding(0, 10, 0, 8);
        android.widget.ImageView itemImage = createAppCompatImageViewCompat(MCD_ID_MENU_ITEM_IMAGE);
        itemImage.setBackgroundColor(0xFFFFC72C);
        itemRow.addView(itemImage, new LinearLayout.LayoutParams(88, 88));
        LinearLayout copy = new LinearLayout(mContext);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(14, 0, 0, 0);
        copy.addView(createMcdText(MCD_ID_TEXT_HEADER,
                "Featured item", 16, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        copy.addView(createMcdText(MCD_ID_PROMOTION_ITEM_DESCRIPTION,
                "Prepared for pickup at your selected restaurant.", 12, 0xFF6F6F6F),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        itemRow.addView(copy, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        panel.addView(itemRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView add = createMcdText(MCD_ID_ADD_TO_ORDER,
                "Add to Bag", 15, 0xFF292929);
        add.setGravity(android.view.Gravity.CENTER);
        add.setBackgroundColor(0xFFFFC72C);
        add.setClickable(true);
        panel.addView(add, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 46));

        View bag = buildMcdBottomBagBar();
        LinearLayout.LayoutParams bagLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 54);
        bagLp.topMargin = 8;
        panel.addView(bag, bagLp);
        addPanelToHost(host, panel);

        com.westlake.engine.WestlakeLauncher.recordMcdCategoryNavigation(
                label, "category_detail");
    }

    private String resolveMcdLiveAdapterLabel(int recyclerId, int position, String fallback) {
        try {
            return com.westlake.engine.WestlakeLauncher.mcdLiveAdapterLabel(
                    recyclerId, position, fallback);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private byte[] resolveMcdLiveAdapterImageBytes(int recyclerId, int position) {
        try {
            return com.westlake.engine.WestlakeLauncher.mcdLiveAdapterImageBytes(
                    recyclerId, position);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private byte[] resolveMcdLiveAdapterImageBytes(
            int recyclerId, int position, String labelHint) {
        try {
            return com.westlake.engine.WestlakeLauncher.mcdLiveAdapterImageBytes(
                    recyclerId, position, labelHint);
        } catch (Throwable ignored) {
            return resolveMcdLiveAdapterImageBytes(recyclerId, position);
        }
    }

    private View buildMcdHeroSection() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setId(MCD_ID_HOME_DASHBOARD_HERO_SECTION);
        root.setPadding(24, 12, 24, 12);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        LinearLayout loaded = new LinearLayout(mContext);
        loaded.setId(MCD_ID_LOADED_VIEW1);
        loaded.setOrientation(LinearLayout.VERTICAL);
        loaded.setVisibility(View.VISIBLE);

        android.widget.RelativeLayout hero = new android.widget.RelativeLayout(mContext);
        hero.setId(MCD_ID_HERO_PARENT);
        hero.setFocusable(true);
        hero.setMinimumHeight(340);
        hero.setBackgroundColor(0xFFFFC72C);

        android.widget.ImageView image = new android.widget.ImageView(mContext);
        image.setId(MCD_ID_HERO_BACKGROUND_IMAGE_VIEW);
        image.setVisibility(View.VISIBLE);
        image.setBackgroundColor(0xFFFFC72C);
        hero.addView(image, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 340));

        com.mcdonalds.mcduikit.widget.McDMutedVideoView video =
                new com.mcdonalds.mcduikit.widget.McDMutedVideoView(mContext);
        video.setId(MCD_ID_BACKGROUND_VIDEO_VIEW);
        video.setVisibility(View.GONE);
        hero.addView(video, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 340));

        android.widget.RelativeLayout headers = new android.widget.RelativeLayout(mContext);
        headers.setId(MCD_ID_HERO_HEADERS);
        LinearLayout textContainer = new LinearLayout(mContext);
        textContainer.setId(MCD_ID_HERO_TEXT_CONTAINER);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(24, 28, 150, 12);
        textContainer.addView(createMcdText(MCD_ID_EYEBROW_TEXT,
                "Limited time", 14, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textContainer.addView(createMcdText(MCD_ID_HERO_TITLE_LABEL,
                "McDonald's favorites", 28, 0xFFDA291C),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textContainer.addView(createMcdText(MCD_ID_HERO_SUB_TITLE_LABEL,
                "Order ahead and keep earning rewards.", 16, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        headers.addView(textContainer, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hero.addView(headers, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout cta = new LinearLayout(mContext);
        cta.setId(MCD_ID_HERO_CTA);
        cta.setOrientation(LinearLayout.HORIZONTAL);
        cta.setGravity(android.view.Gravity.CENTER);
        cta.setPadding(24, 16, 24, 18);
        cta.setVisibility(View.VISIBLE);
        com.mcdonalds.mcduikit.widget.McDTextView left =
                createMcdText(MCD_ID_HOME_SCREEN_HERO_SECTION_LEFT_BUTTON,
                        "Order Now", 15, 0xFF292929);
        left.setGravity(android.view.Gravity.CENTER);
        left.setBackgroundColor(0xFFFFFFFF);
        com.mcdonalds.mcduikit.widget.McDTextView right =
                createMcdText(MCD_ID_HOME_SCREEN_HERO_SECTION_RIGHT_BUTTON,
                        "View Deals", 15, 0xFF292929);
        right.setGravity(android.view.Gravity.CENTER);
        right.setBackgroundColor(0xFFFFFFFF);
        cta.addView(left, new LinearLayout.LayoutParams(
                0, 54, 1f));
        LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(0, 54, 1f);
        rightLp.leftMargin = 12;
        cta.addView(right, rightLp);
        android.widget.RelativeLayout.LayoutParams ctaLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ctaLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
        hero.addView(cta, ctaLp);

        loaded.addView(hero, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 340));

        android.widget.RelativeLayout legal = new android.widget.RelativeLayout(mContext);
        legal.setId(MCD_ID_LEGAL_TEXT_PARENT_LAYOUT);
        legal.setVisibility(View.VISIBLE);
        com.mcdonalds.mcduikit.widget.McDTextView legalText =
                createMcdText(MCD_ID_LEGAL_TEXT_LABEL,
                        "Prices and participation may vary.", 11, 0xFF6F6F6F);
        legal.addView(legalText, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loaded.addView(legal, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addCompatChild(root, loaded);

        LinearLayout shimmer = new LinearLayout(mContext);
        shimmer.setId(MCD_ID_SHIMMER_VIEW1);
        shimmer.setOrientation(LinearLayout.VERTICAL);
        shimmer.setVisibility(View.GONE);
        shimmer.addView(createShimmerBlock(MCD_ID_BLOCK1, 220),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 220));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_FIRST, 16),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 16));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_SECOND, 16),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 16));
        addCompatChild(root, shimmer);
        return root;
    }

    private View buildMcdMenuSection() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        root.setPadding(24, 12, 24, 12);

        LinearLayout loaded = new LinearLayout(mContext);
        loaded.setId(MCD_ID_LOADED_VIEW2);
        loaded.setOrientation(LinearLayout.VERTICAL);
        loaded.setVisibility(View.VISIBLE);

        LinearLayout header = new LinearLayout(mContext);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.addView(createMcdText(MCD_ID_TV_MENU_TITLE, "Menu", 22, 0xFF292929),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout fullMenu = new LinearLayout(mContext);
        fullMenu.setId(MCD_ID_FULL_MENU_LAYOUT);
        fullMenu.setOrientation(LinearLayout.HORIZONTAL);
        fullMenu.setFocusable(true);
        fullMenu.addView(createMcdText(MCD_ID_VIEW_FULL_MENU, "Full Menu", 14, 0xFF2860A8),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        android.widget.ImageView arrow = new android.widget.ImageView(mContext);
        arrow.setId(MCD_ID_ARROW);
        arrow.setBackgroundColor(0xFF2860A8);
        fullMenu.addView(arrow, new LinearLayout.LayoutParams(18, 18));
        header.addView(fullMenu, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loaded.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout menuLayout = new LinearLayout(mContext);
        menuLayout.setId(MCD_ID_MENU_LAYOUT);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setVisibility(View.VISIBLE);
        menuLayout.addView(buildSeededRecyclerView(MCD_ID_MENU_CATEGORY_LIST,
                new String[] {"Burgers", "Fries", "McCafe", "Breakfast"}, true),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 190));
        loaded.addView(menuLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.RelativeLayout noData = buildMcdMenuNoDataTile(MCD_ID_NO_DATA_LAYOUT);
        noData.setVisibility(View.GONE);
        loaded.addView(noData, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addCompatChild(root, loaded);

        LinearLayout shimmer = buildMcdMenuShimmer(MCD_ID_SHIMMER_VIEW2);
        shimmer.setVisibility(View.GONE);
        addCompatChild(root, shimmer);
        return root;
    }

    private android.widget.RelativeLayout buildMcdMenuNoDataTile(int id) {
        android.widget.RelativeLayout tile = new android.widget.RelativeLayout(mContext);
        tile.setId(id);
        tile.setBackgroundColor(0xFFFFF3CF);
        tile.setPadding(24, 20, 24, 20);
        LinearLayout copy = new LinearLayout(mContext);
        copy.setId(MCD_ID_LL_HEADER);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.addView(createMcdText(MCD_ID_TEXT_HEADER,
                "Ready to order?", 18, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        copy.addView(createMcdText(MCD_ID_TEXT_EYE_BROW,
                "Start an order to see local menu items.", 14, 0xFF6F6F6F),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tile.addView(copy, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        com.mcdonalds.mcduikit.widget.McDTextView button =
                createMcdText(MCD_ID_TEXT_LEFT_BUTTON, "Start Order", 14, 0xFF292929);
        button.setGravity(android.view.Gravity.CENTER);
        button.setBackgroundColor(0xFFFFC72C);
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMcdStartOrderFlow(v);
            }
        });
        android.widget.RelativeLayout.LayoutParams buttonLp =
                new android.widget.RelativeLayout.LayoutParams(260, 48);
        buttonLp.addRule(android.widget.RelativeLayout.BELOW, MCD_ID_LL_HEADER);
        buttonLp.topMargin = 16;
        tile.addView(button, buttonLp);
        return tile;
    }

    private void openMcdStartOrderFlow(View source) {
        if (source instanceof TextView) {
            ((TextView) source).setText("Order Started");
        }
        LinearLayout guestMenu = findAncestorLinearLayout(source, MCD_ID_LL_GUEST_MENU);
        if (guestMenu != null) {
            replaceWithMcdOrderPanel(guestMenu, false);
            com.westlake.engine.WestlakeLauncher.recordMcdOrderNavigation(
                    "start_order_guest_menu");
            return;
        }

        ViewGroup tile = findAncestorViewGroup(source, MCD_ID_GUEST_TILE_LAYOUT);
        if (tile == null) {
            tile = findAncestorViewGroup(source, MCD_ID_NO_DATA_LAYOUT);
        }
        if (tile != null) {
            replaceWithMcdOrderPanel(tile, true);
            com.westlake.engine.WestlakeLauncher.recordMcdOrderNavigation(
                    "start_order_tile_menu");
            return;
        }

        com.westlake.engine.WestlakeLauncher.recordMcdOrderNavigation(
                "start_order_text_only");
    }

    private void replaceWithMcdOrderPanel(ViewGroup host, boolean compact) {
        host.removeAllViews();
        host.setPadding(0, 0, 0, 0);
        host.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        View realXmlOrder = tryBuildMcdRealXmlOrderPanel(compact);
        if (realXmlOrder != null) {
            addPanelToHost(host, realXmlOrder);
            return;
        }

        LinearLayout panel = new LinearLayout(mContext);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(24, 14, 24, 14);
        panel.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        panel.addView(createMcdText(MCD_ID_TV_GUEST_MENU_TITLE,
                "Order Menu", 22, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView status = createMcdText(MCD_ID_TEXT_HEADER,
                "Choose a category to start your order.", 14, 0xFF6F6F6F);
        panel.addView(status, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        androidx.recyclerview.widget.RecyclerView categories = buildSeededRecyclerView(
                MCD_ID_MENU_CATEGORY_LIST,
                new String[] {"Breakfast", "Burgers", "Fries", "McCafe"},
                true);
        LinearLayout.LayoutParams categoriesLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, compact ? 92 : 160);
        categoriesLp.topMargin = 8;
        panel.addView(categories, categoriesLp);

        View bag = buildMcdBottomBagBar();
        LinearLayout.LayoutParams bagLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, compact ? 54 : 72);
        bagLp.topMargin = 8;
        panel.addView(bag, bagLp);

        addPanelToHost(host, panel);
    }

    private void addPanelToHost(ViewGroup host, View panel) {
        if (host instanceof FrameLayout) {
            host.addView(panel, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else if (host instanceof android.widget.RelativeLayout) {
            host.addView(panel, new android.widget.RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else if (host instanceof LinearLayout) {
            host.addView(panel, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            host.addView(panel);
        }
    }

    private LinearLayout findAncestorLinearLayout(View source, int id) {
        View current = source;
        while (current != null) {
            if (current instanceof LinearLayout && current.getId() == id) {
                return (LinearLayout) current;
            }
            ViewParent parent = current.getParent();
            if (!(parent instanceof View)) {
                return null;
            }
            current = (View) parent;
        }
        return null;
    }

    private ViewGroup findAncestorViewGroup(View source, int id) {
        View current = source;
        while (current != null) {
            if (current instanceof ViewGroup && current.getId() == id) {
                return (ViewGroup) current;
            }
            ViewParent parent = current.getParent();
            if (!(parent instanceof View)) {
                return null;
            }
            current = (View) parent;
        }
        return null;
    }

    private LinearLayout buildMcdMenuShimmer(int rootId) {
        LinearLayout root = new LinearLayout(mContext);
        root.setId(rootId);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(createShimmerLine(MCD_ID_VIEW_THIRD, 24),
                new LinearLayout.LayoutParams(180, 24));
        root.addView(createShimmerLine(MCD_ID_VIEW_FOURTH, 16),
                new LinearLayout.LayoutParams(120, 16));
        root.addView(createShimmerBlock(MCD_ID_BLOCK2, 120),
                new LinearLayout.LayoutParams(160, 120));
        root.addView(createShimmerLine(MCD_ID_VIEW_FIFTH, 16),
                new LinearLayout.LayoutParams(160, 16));
        root.addView(createShimmerBlock(MCD_ID_BLOCK3, 120),
                new LinearLayout.LayoutParams(160, 120));
        root.addView(createShimmerLine(MCD_ID_VIEW_SIXTH, 16),
                new LinearLayout.LayoutParams(160, 16));
        root.addView(createShimmerBlock(MCD_ID_BLOCK4, 120),
                new LinearLayout.LayoutParams(160, 120));
        root.addView(createShimmerLine(MCD_ID_VIEW_SEVEN, 16),
                new LinearLayout.LayoutParams(160, 16));
        return root;
    }

    private View buildMcdPromotionSection() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setId(MCD_ID_HOME_SCREEN_PROMOTION_SECTION);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        root.setPadding(24, 12, 24, 12);

        LinearLayout loaded = new LinearLayout(mContext);
        loaded.setId(MCD_ID_LOADED_VIEW4);
        loaded.setOrientation(LinearLayout.VERTICAL);
        loaded.setVisibility(View.VISIBLE);
        loaded.addView(createMcdText(MCD_ID_HOME_SCREEN_PROMOTION_TILE_CONTAINER_HEADINGTEXT,
                "What's New", 22, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout promoLayout = new LinearLayout(mContext);
        promoLayout.setId(MCD_ID_PROMO_LAYOUT);
        promoLayout.setOrientation(LinearLayout.VERTICAL);
        promoLayout.addView(buildSeededRecyclerView(MCD_ID_PROMOTION_RECYCLER_LIST,
                new String[] {"Free fries Friday", "New crispy favorites"}, false),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 240));
        loaded.addView(promoLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addCompatChild(root, loaded);

        LinearLayout shimmer = new LinearLayout(mContext);
        shimmer.setId(MCD_ID_SHIMMER_VIEW4);
        shimmer.setOrientation(LinearLayout.VERTICAL);
        shimmer.setVisibility(View.GONE);
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_NINETEEN, 24),
                new LinearLayout.LayoutParams(220, 24));
        shimmer.addView(createShimmerBlock(MCD_ID_BLOCK8, 160),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 160));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTY, 12),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 12));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYONE, 12),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 12));
        addCompatChild(root, shimmer);
        return root;
    }

    private View buildMcdPopularSection() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        root.setPadding(24, 12, 24, 12);

        LinearLayout loaded = new LinearLayout(mContext);
        loaded.setId(MCD_ID_POPULAR_LOADED_VIEW);
        loaded.setOrientation(LinearLayout.VERTICAL);
        loaded.setVisibility(View.VISIBLE);
        loaded.addView(createMcdText(MCD_ID_TV_POPULAR_TITLE,
                "Popular", 22, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout popularLayout = new LinearLayout(mContext);
        popularLayout.setId(MCD_ID_POPULAR_LAYOUT);
        popularLayout.setOrientation(LinearLayout.VERTICAL);
        popularLayout.addView(buildSeededRecyclerView(MCD_ID_POPULAR_LIST,
                new String[] {"Big Mac", "McNuggets", "World Famous Fries"}, true),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 190));
        loaded.addView(popularLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addCompatChild(root, loaded);

        LinearLayout shimmer = new LinearLayout(mContext);
        shimmer.setId(MCD_ID_SHIMMER_VIEW5);
        shimmer.setOrientation(LinearLayout.VERTICAL);
        shimmer.setVisibility(View.GONE);
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYTWO, 24),
                new LinearLayout.LayoutParams(180, 24));
        shimmer.addView(createShimmerBlock(MCD_ID_BLOCK9, 120),
                new LinearLayout.LayoutParams(160, 120));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYTHREE, 16),
                new LinearLayout.LayoutParams(160, 16));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYFOUR, 16),
                new LinearLayout.LayoutParams(160, 16));
        shimmer.addView(createShimmerBlock(MCD_ID_BLOCK10, 120),
                new LinearLayout.LayoutParams(160, 120));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYFIVE, 16),
                new LinearLayout.LayoutParams(160, 16));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYSIX, 16),
                new LinearLayout.LayoutParams(160, 16));
        shimmer.addView(createShimmerBlock(MCD_ID_BLOCK11, 120),
                new LinearLayout.LayoutParams(160, 120));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYSEVEN, 16),
                new LinearLayout.LayoutParams(160, 16));
        shimmer.addView(createShimmerLine(MCD_ID_VIEW_TWENTYEIGHT, 16),
                new LinearLayout.LayoutParams(160, 16));
        addCompatChild(root, shimmer);
        return root;
    }

    private View buildMcdHomeMenuGuestUser() {
        LinearLayout root = new LinearLayout(mContext);
        root.setId(MCD_ID_LL_GUEST_MENU);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 12, 24, 12);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        root.addView(createMcdText(MCD_ID_TV_GUEST_MENU_TITLE,
                "Menu", 22, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(mContext);
        card.setCardBackgroundColor(0xFFFFF3CF);
        card.setRadius(18f);
        card.setContentPadding(0, 0, 0, 0);
        android.widget.RelativeLayout tile = buildMcdMenuNoDataTile(MCD_ID_GUEST_TILE_LAYOUT);
        tile.setVisibility(View.VISIBLE);
        card.addView(tile, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 190));
        root.addView(card, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 210));
        return root;
    }

    private View buildMcdHomeMenuSectionFullMenuItem() {
        android.widget.RelativeLayout root = new android.widget.RelativeLayout(mContext);
        root.setGravity(android.view.Gravity.CENTER);
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        root.setVisibility(View.VISIBLE);

        LinearLayout center = new LinearLayout(mContext);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(android.view.Gravity.CENTER);
        center.setFocusable(true);
        center.setFocusableInTouchMode(true);

        android.widget.ImageView image = createAppCompatImageViewCompat(MCD_ID_MENU_ITEM_IMAGE);
        image.setBackgroundColor(0xFFFFC72C);
        image.setVisibility(View.VISIBLE);
        center.addView(image, new LinearLayout.LayoutParams(72, 72));

        LinearLayout row = new LinearLayout(mContext);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER);

        TextView fullMenu = createStockMcdText(MCD_ID_VIEW_FULL_MENU,
                "Full Menu", 16, 0xFF2860A8);
        fullMenu.setGravity(android.view.Gravity.CENTER);
        fullMenu.setPadding(8, 8, 8, 8);
        fullMenu.setFocusable(true);
        row.addView(fullMenu, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.ImageView arrow = createAppCompatImageViewCompat(View.NO_ID);
        arrow.setBackgroundColor(0xFF2860A8);
        row.addView(arrow, new LinearLayout.LayoutParams(16, 16));
        center.addView(row, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(center, new android.widget.RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return root;
    }

    private View buildMcdHomeMenuSectionItem() {
        LinearLayout root = new LinearLayout(mContext);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        root.setVisibility(View.VISIBLE);

        LinearLayout focusRoot = new LinearLayout(mContext);
        focusRoot.setOrientation(LinearLayout.VERTICAL);
        focusRoot.setFocusable(true);
        focusRoot.setFocusableInTouchMode(true);

        ViewGroup card = createStockCardView(MCD_ID_MENU_ITEM_CARD, 0xFFFFFFFF, 18f);
        card.setPadding(0, 0, 0, 0);

        android.widget.ImageView image = createAppCompatImageViewCompat(MCD_ID_MENU_ITEM_IMAGE);
        image.setBackgroundColor(0xFFFFC72C);
        image.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        image.setVisibility(View.VISIBLE);
        card.addView(image, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        focusRoot.addView(card, new LinearLayout.LayoutParams(160, 160));

        TextView title = createStockMcdText(MCD_ID_MENU_ITEM_TITLE,
                "Big Mac", 15, 0xFF292929);
        title.setGravity(android.view.Gravity.LEFT);
        focusRoot.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(focusRoot, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return root;
    }

    private View buildMcdHomePopularItemAdapter() {
        LinearLayout root = new LinearLayout(mContext);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setVisibility(View.VISIBLE);

        ViewGroup card = createStockCardView(MCD_ID_POPULAR_ITEM_CARD, 0xFFFFFFFF, 18f);
        card.setPadding(0, 0, 0, 0);

        android.widget.ImageView image = createAppCompatImageViewCompat(MCD_ID_POPULAR_ITEM_IMAGE);
        image.setBackgroundColor(0xFFFFC72C);
        image.setScaleType(android.widget.ImageView.ScaleType.CENTER);
        image.setVisibility(View.VISIBLE);
        card.addView(image, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(card, new LinearLayout.LayoutParams(104, 104));

        TextView title = createStockMcdText(MCD_ID_POPULAR_ITEM_TITLE,
                "World Famous Fries", 15, 0xFF292929);
        title.setGravity(android.view.Gravity.LEFT);
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = createStockMcdText(MCD_ID_POPULAR_ITEM_SUBTITLE,
                "Crispy, golden, favorite", 12, 0xFF6F6F6F);
        root.addView(subtitle, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return root;
    }

    private View buildMcdHomePromotionItem(boolean updated) {
        LinearLayout root = new LinearLayout(mContext);
        root.setId(MCD_ID_PROMOTION_ITEM_ROOT);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 16);
        root.setVisibility(View.VISIBLE);

        ViewGroup card = createStockCardView(MCD_ID_PROMOTION_ITEM_CARD, 0xFFFFFFFF, 18f);
        card.setPadding(0, 0, 0, 0);

        android.widget.ImageView image = createAppCompatImageViewCompat(MCD_ID_PROMOTION_ITEM_IMAGE);
        image.setBackgroundColor(updated ? 0xFFFFC72C : 0xFFDA291C);
        image.setAdjustViewBounds(true);
        image.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        image.setVisibility(View.VISIBLE);
        card.addView(image, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        android.widget.RelativeLayout overlay = new android.widget.RelativeLayout(mContext);
        overlay.setId(MCD_ID_PROMOTION_ITEM_TEXT_CONTAINER);
        overlay.setPadding(18, 16, 18, 16);

        LinearLayout copy = new LinearLayout(mContext);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.addView(createStockMcdText(MCD_ID_PROMOTION_ITEM_TITLE,
                updated ? "Featured" : "Deal", 12, 0xFF6F6F6F),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        copy.addView(createStockMcdText(MCD_ID_PROMOTION_ITEM_SUBTITLE,
                "Free Fries Friday", 20, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        copy.addView(createStockMcdText(MCD_ID_PROMOTION_ITEM_EYEBROW,
                "Get medium fries with any $1 minimum app order.", 14, 0xFF292929),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.RelativeLayout.LayoutParams copyLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        copyLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
        overlay.addView(copy, copyLp);

        LinearLayout buttons = new LinearLayout(mContext);
        buttons.setId(MCD_ID_PROMOTION_ITEM_BUTTON_CONTAINER);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(android.view.Gravity.BOTTOM);
        buttons.addView(createStockMcdText(MCD_ID_PROMOTION_ITEM_DESCRIPTION,
                "Order Now", 14, 0xFF2860A8),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        buttons.addView(createStockMcdText(MCD_ID_PROMOTION_ITEM_CTA,
                "Details", 14, 0xFF2860A8),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        android.widget.RelativeLayout.LayoutParams buttonLp =
                new android.widget.RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
        overlay.addView(buttons, buttonLp);

        card.addView(overlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(card, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, updated ? 228 : 168));

        TextView legal = createStockMcdText(MCD_ID_PROMOTION_ITEM_LEGAL,
                "Terms apply.", 10, 0xFF6F6F6F);
        legal.setVisibility(View.GONE);
        root.addView(legal, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return root;
    }

    private View buildMcdHomeDashboardSection() {
        android.widget.FrameLayout section = new android.widget.FrameLayout(mContext);
        section.setVisibility(View.VISIBLE);
        section.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));
        return section;
    }

    private View buildMcdBonusTileFragment() {
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(mContext);
        card.setId(MCD_ID_LOYALTY_BONUS_TILE_CARD);
        card.setTag("layout/layout_bonus_tile_fragment_0");
        card.setCardBackgroundColor(0xFFFFC72C);
        card.setRadius(18f);
        card.setContentPadding(18, 18, 18, 18);

        androidx.constraintlayout.widget.ConstraintLayout root =
                new androidx.constraintlayout.widget.ConstraintLayout(mContext);
        root.setBackgroundColor(0xFFFFC72C);

        androidx.constraintlayout.widget.Guideline horizontal =
                new androidx.constraintlayout.widget.Guideline(mContext);
        horizontal.setId(MCD_ID_HORIZONTAL_GUIDE_LINE);
        horizontal.setVisibility(View.GONE);
        root.addView(horizontal, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.Guideline guideline =
                new androidx.constraintlayout.widget.Guideline(mContext);
        guideline.setId(MCD_ID_GUIDELINE);
        guideline.setVisibility(View.GONE);
        root.addView(guideline, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout visible = new LinearLayout(mContext);
        visible.setOrientation(LinearLayout.HORIZONTAL);
        visible.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.widget.ImageView image = createAppCompatImageViewCompat(
                MCD_ID_LOYALTY_BONUS_TILE_IMAGE);
        image.setBackgroundColor(0xFFE8AA00);
        visible.addView(image, new LinearLayout.LayoutParams(72, 72));

        LinearLayout copy = new LinearLayout(mContext);
        copy.setOrientation(LinearLayout.VERTICAL);
        com.mcdonalds.mcduikit.widget.McDTextView header =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        header.setId(MCD_ID_LOYALTY_BONUS_TILE_HEADER);
        header.setText("Bonus points are waiting");
        header.setTextSize(18);
        header.setTextColor(0xFF292929);
        copy.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView subHeader =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        subHeader.setId(MCD_ID_LOYALTY_BONUS_TILE_SUB_HEADER);
        subHeader.setText("Unlock bonus rewards with your next order");
        subHeader.setTextSize(14);
        subHeader.setTextColor(0xFF4A3A00);
        copy.addView(subHeader, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView multiStep =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        multiStep.setId(MCD_ID_M_LOYALTY_MULTI_BONUS_NUMBER_STEPS_TILE_SUB_HEADER);
        multiStep.setText("0 of 3 visits complete");
        multiStep.setVisibility(View.GONE);
        copy.addView(multiStep, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        com.mcdonalds.mcduikit.widget.McDTextView unlock =
                new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
        unlock.setId(MCD_ID_LOYALTY_MULTI_BONUS_TILE_SUB_HEADER);
        unlock.setText("Unlock and start earning");
        unlock.setVisibility(View.GONE);
        copy.addView(unlock, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        visible.addView(copy, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        com.mcdonalds.mcduikit.widget.SquareImageView arrow =
                new com.mcdonalds.mcduikit.widget.SquareImageView(mContext);
        arrow.setId(MCD_ID_LOYALTY_BONUS_TILE_NEXT_ARROW);
        arrow.setBackgroundColor(0xFFDA291C);
        visible.addView(arrow, new LinearLayout.LayoutParams(44, 44));
        root.addView(visible, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.ConstraintLayout progressContainer =
                new androidx.constraintlayout.widget.ConstraintLayout(mContext);
        progressContainer.setId(MCD_ID_MULTI_STEP_PROGRESSBAR_CONTAINER);
        progressContainer.setVisibility(View.GONE);

        androidx.constraintlayout.widget.ConstraintLayout anim =
                new androidx.constraintlayout.widget.ConstraintLayout(mContext);
        anim.setId(MCD_ID_ANIM_CONTAINER);
        com.mcdonalds.offer.MultiStepBonusProgressBar progress =
                new com.mcdonalds.offer.MultiStepBonusProgressBar(mContext);
        progress.setId(MCD_ID_LOYALTY_DASHBOARD_POINTS_PROGRESS_BAR);
        anim.addView(progress, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 24));
        progressContainer.addView(anim, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.Guideline endPoint =
                new androidx.constraintlayout.widget.Guideline(mContext);
        endPoint.setId(MCD_ID_END_POINT);
        endPoint.setVisibility(View.GONE);
        progressContainer.addView(endPoint, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.Guideline startPoint =
                new androidx.constraintlayout.widget.Guideline(mContext);
        startPoint.setId(MCD_ID_START_POINT);
        startPoint.setVisibility(View.GONE);
        progressContainer.addView(startPoint, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.Guideline topPoint =
                new androidx.constraintlayout.widget.Guideline(mContext);
        topPoint.setId(MCD_ID_TOP_POINT);
        topPoint.setVisibility(View.GONE);
        progressContainer.addView(topPoint, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(progressContainer, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        androidx.constraintlayout.widget.ConstraintLayout warning =
                new androidx.constraintlayout.widget.ConstraintLayout(mContext);
        warning.setId(MCD_ID_WARNING_MESSAGE_CONTAINER);
        warning.setVisibility(View.GONE);
        android.widget.ImageView warningIcon = new android.widget.ImageView(mContext);
        warningIcon.setId(MCD_ID_WARNING_ICON);
        warning.addView(warningIcon, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                20, 20));
        TextView warningText = new TextView(mContext);
        warningText.setId(MCD_ID_TEXT_WITH_ICON);
        warningText.setText("Select a participating restaurant");
        warning.addView(warningText, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(warning, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        card.addView(root, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return card;
    }

    /**
     * Inflate a layout resource.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a layout resource with attachToRoot control.
     *
     * Tries five strategies in order:
     * 1. Programmatic layout registry (ViewFactory)
     * 2. Registered layout bytes via Resources.getLayoutBytes()
     * 3. Binary layout XML from extracted APK res/ directory
     * 4. Binary layout XML read directly from APK ZIP
     * 5. Fallback: empty FrameLayout with resource ID
     *
     * Strategies 2-4 parse AXML into a BinaryXmlParser and delegate to
     * inflate(XmlPullParser, ...) for proper View creation, attribute
     * application, and LayoutParams generation.
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        // PF-noice-013 (2026-05-05) diagnostic: confirm shim is hit
        try {
            android.util.Log.d("WLInflater", "INFLATE_TRACE inflate(int) entry resource=0x"
                    + Integer.toHexString(resource) + " root=" + (root != null) + " attach=" + attachToRoot);
        } catch (Throwable ignored) {}
        View view = null;
        boolean canUseRealInflaterShortcut = (root == null && !attachToRoot);

        // 0. Try real Android LayoutInflater (app_process64 mode)
        Object realCtx = com.westlake.engine.WestlakeLauncher.sRealContext;
        if (canUseRealInflaterShortcut
                && com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed()
                && realCtx != null) {
            try {
                java.lang.reflect.Method fromMethod = LayoutInflater.class.getMethod("from", android.content.Context.class);
                Object realInflater = fromMethod.invoke(null, realCtx);
                if (realInflater != null && realInflater != this) {
                    java.lang.reflect.Method inflateMethod = realInflater.getClass().getMethod("inflate", int.class, ViewGroup.class, boolean.class);
                    view = (View) inflateMethod.invoke(realInflater, resource, null, false);
	                    if (view != null) {
	                        diag("[LayoutInflater] Real inflate OK: 0x" + Integer.toHexString(resource) + " -> " + classNameOf(view));
	                        if (attachToRoot && root != null) {
	                            root.addView(view);
	                            ensureDataBindingTag(root, resource);
	                            return root;
	                        }
	                        return view;
	                    }
                }
            } catch (Throwable t) {
                diag("[LayoutInflater] Real inflate failed: " + t.getMessage());
            }
        }

        // 1. Check programmatic layout registry first
        Object factoryObj = sLayoutRegistry.get(Integer.valueOf(resource));
        if (factoryObj != null) {
            try {
                ViewFactory factory = (ViewFactory) factoryObj;
                view = factory.createView(mContext, root);
                if (view != null) {
	                    ensureDataBindingTag(view, resource);
	                    if (attachToRoot && root != null) {
	                        root.addView(view);
	                        ensureDataBindingTag(root, resource);
	                        return root;
	                    }
                    return view;
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater",
                            "Registered layout factory failed for 0x"
                                    + Integer.toHexString(resource) + ": "
                                    + throwableName(t) + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        View knownMcdLayout = maybeBuildKnownMcdLayout(resource);
        if (knownMcdLayout != null) {
            ensureDataBindingTag(knownMcdLayout, resource);
            if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD) {
                try {
                    diag("[LayoutInflater] fragment_home_dashboard build OK: "
                            + classNameOf(knownMcdLayout));
                } catch (Throwable ignored) {
                }
            }
	            if (attachToRoot && root != null) {
	                addCompatChild(root, knownMcdLayout);
	                ensureDataBindingTag(root, resource);
	                return root;
	            }
            return knownMcdLayout;
        }

        View knownAppCompatLayout = maybeBuildKnownAppCompatLayout(resource);
        if (knownAppCompatLayout != null) {
            ensureDataBindingTag(knownAppCompatLayout, resource);
	            if (attachToRoot && root != null) {
	                addCompatChild(root, knownAppCompatLayout);
	                ensureDataBindingTag(root, resource);
	                return root;
	            }
            return knownAppCompatLayout;
        }

        // Strategies 2-4: obtain binary AXML bytes, then inflate via XmlPullParser
        byte[] axmlData = null;
        String knownLayoutName = knownMcdLayoutName(resource);
        String knownLayoutFile = layoutXmlPathFromName(knownLayoutName);
        try {
            diag("[LayoutInflater] inflate(0x" + Integer.toHexString(resource) + ")");
        } catch (Throwable ignored) {
        }

        // 2. Try registered layout bytes
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    axmlData = res.getLayoutBytes(resource);
                    if (axmlData != null) {
                        diag("[LayoutInflater] Strategy 2: getLayoutBytes OK (" + axmlData.length + " bytes)");
                        axmlData = requireBinaryAxml(axmlData, "Strategy 2", resource);
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 2 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + throwableName(t) + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 3. Try loading from extracted APK res/ directory via ResourceTable
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    android.content.res.ResourceTable table = res.getResourceTable();
                    if (table != null) {
                        String layoutFile = table.getLayoutFileName(resource);
                        try {
                            // Keep resource-table diagnostics from aborting shim-only inflates.
                            String dbgStr = table.getString(resource);
                            String dbgName = table.getResourceName(resource);
                            diag("[LayoutInflater] Strategy 3: table=true"
                                    + " layoutFile=" + layoutFile
                                    + " str=" + dbgStr + " name=" + dbgName
                                    + " entries=" + table.getStringCount());
                        } catch (Throwable ignored) {
                        }
                        if (layoutFile != null) {
                            axmlData = loadLayoutXml(layoutFile);
                            if (axmlData != null) {
                                diag("[LayoutInflater] Strategy 3: OK (" + axmlData.length + " bytes)");
                                axmlData = requireBinaryAxml(axmlData, "Strategy 3 table", resource);
                            }
                        }
                        if (axmlData == null && knownLayoutFile != null
                                && !knownLayoutFile.equals(layoutFile)) {
                            axmlData = loadLayoutXml(knownLayoutFile);
                            if (axmlData != null) {
                                diag("[LayoutInflater] Strategy 3 known: OK " + knownLayoutFile
                                        + " (" + axmlData.length + " bytes)");
                                axmlData = requireBinaryAxml(axmlData, "Strategy 3 known", resource);
                            }
                        }
                    } else {
                        diag("[LayoutInflater] Strategy 3: no ResourceTable");
                        if (knownLayoutFile != null) {
                            axmlData = loadLayoutXml(knownLayoutFile);
                            if (axmlData != null) {
                                diag("[LayoutInflater] Strategy 3 known/no-table: OK "
                                        + knownLayoutFile + " (" + axmlData.length + " bytes)");
                                axmlData = requireBinaryAxml(axmlData, "Strategy 3 known/no-table", resource);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 3 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + throwableName(t) + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 3.5: Try loading from pre-extracted res directory using resource table entry value
        if (axmlData == null && mContext != null) {
            try {
                String resDir = System.getProperty("westlake.apk.resdir");
                if (resDir != null) {
                    android.content.res.Resources res2 = mContext.getResources();
                    android.content.res.ResourceTable table2 = (res2 != null) ? res2.getResourceTable() : null;
                    if (table2 != null) {
                        // The resources.arsc maps resId -> string pool entry containing the file path
                        // Try getEntryValue which reads the raw Res_value string
                        String filePath = table2.getEntryFilePath(resource);
                        if (filePath != null) {
                            java.io.File f = new java.io.File(resDir, filePath);
                            if (f.exists()) {
                                try {
                                    axmlData = readFile(f);
                                    diag("[LayoutInflater] Strategy 3.5: loaded " + filePath
                                            + " (" + (axmlData != null ? axmlData.length : 0)
                                            + " bytes)");
                                    axmlData = requireBinaryAxml(axmlData, "Strategy 3.5 table", resource);
                                } catch (Exception e) {
                                    diag("[LayoutInflater] Strategy 3.5: read failed: " + e.getMessage());
                                }
                            }
                        }
                    }
                    if (axmlData == null && knownLayoutFile != null) {
                        java.io.File f = new java.io.File(resDir, knownLayoutFile);
                        if (!f.exists() && knownLayoutFile.startsWith("res/")) {
                            f = new java.io.File(resDir, knownLayoutFile.substring(4));
                        }
                        if (f.exists()) {
                            try {
                                axmlData = readFile(f);
                                diag("[LayoutInflater] Strategy 3.5 known: loaded "
                                        + knownLayoutFile + " ("
                                        + (axmlData != null ? axmlData.length : 0)
                                        + " bytes)");
                                axmlData = requireBinaryAxml(axmlData, "Strategy 3.5 known", resource);
                            } catch (Exception e) {
                                diag("[LayoutInflater] Strategy 3.5 known: read failed: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 3.5 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + throwableName(t) + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 4. Try reading directly from APK ZIP file
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    String apkPath = null;
                    try { apkPath = (String) res.getClass().getMethod("getApkPath").invoke(res); } catch (Exception e) {}
                    diag("[LayoutInflater] Strategy 4: apkPath=" + apkPath);
                    axmlData = ApkResourceLoader.loadLayout(res, resource);
                    if (axmlData != null) {
                        diag("[LayoutInflater] Strategy 4: OK (" + axmlData.length + " bytes)");
                        axmlData = requireBinaryAxml(axmlData, "Strategy 4", resource);
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 4 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + throwableName(t) + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // Parse AXML bytes via BinaryXmlParser and inflate using the XmlPullParser path
        if (axmlData != null && axmlData.length > 0) {
            try {
                BinaryXmlParser parser = new BinaryXmlParser(axmlData);
                int rootChildCountBeforeInflate = root instanceof ViewGroup
                        ? root.getChildCount()
                        : -1;
                try {
                    System.out.println(
                            "LAYOUT_INFLATER_AXML_PARSER_OK resource=0x"
                                    + Integer.toHexString(resource)
                                    + " events=" + parser.getEventCount()
                                    + " strings=" + parser.getStringPool().length);
                } catch (Throwable ignored) {
                }
                view = inflate(parser, root, attachToRoot);
                View tagTarget = view;
                if (attachToRoot && root != null
                        && rootChildCountBeforeInflate >= 0
                        && root.getChildCount() > rootChildCountBeforeInflate) {
                    tagTarget = root.getChildAt(rootChildCountBeforeInflate);
	                }
	                ensureDataBindingTag(tagTarget, resource);
	                if (attachToRoot && root != null) {
	                    ensureDataBindingTag(root, resource);
	                }
	                // inflate(XmlPullParser,...) already handles attachToRoot,
                // so return the result directly
                if (view != null) {
                    diag("[LayoutInflater] Inflated 0x"
                            + Integer.toHexString(resource)
                            + " -> " + classNameOf(view));
                    markRealMcdXmlInflated(resource, knownLayoutName, view);
                    enhanceMcdInflatedLayout(resource, view);
                    return view;
                }
            } catch (Throwable e) {
                try {
                    System.out.println(
                            "LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x"
                                    + Integer.toHexString(resource)
                                    + " err=" + e.getClass().getName()
                                    + ":" + e.getMessage());
                } catch (Throwable ignored) {
                }
                diag("[LayoutInflater] AXML parse failed for 0x"
                        + Integer.toHexString(resource) + ": " + throwableName(e) + ": " + e.getMessage());
            }
            if (view == null) {
                diag("[LayoutInflater] inflate(parser) returned null for 0x" + Integer.toHexString(resource));
            }
        }

        // 5. Fallback: an empty FrameLayout. Per the architecture rule
        // (CLAUDE.md), the shim must NOT brand the fallback by package
        // name — that's a per-app hack. If inflate failed, return a
        // bare placeholder and let the gap surface honestly. The real
        // fix is to make inflate succeed, which generally means fixing
        // the AssetManager / Resources path so the AXML actually loads.
        if (view == null) {
            android.widget.FrameLayout placeholder = new android.widget.FrameLayout(mContext);
            placeholder.setId(resource);
            view = placeholder;
        }
        ensureDataBindingTag(view, resource);

	        if (root != null && attachToRoot) {
	            try {
	                root.addView(view);
	                ensureDataBindingTag(root, resource);
	                return root;
	            } catch (Throwable addRootError) {
                String detail = "[LayoutInflater] final attach failed: root="
                        + simpleClassNameOf(root) + " child="
                        + simpleClassNameOf(view) + " err="
                        + throwableName(addRootError) + ": "
                        + addRootError.getMessage();
                diag(detail);
                try {
                    android.util.Log.w("LayoutInflater", detail);
                } catch (Throwable ignored) {
                }
            }
        }
        return view;
    }

    private void ensureDataBindingTag(View view, int resource) {
        if (view == null) {
            return;
        }
        String tag = deriveDataBindingTag(resource);
        if (tag == null) {
            return;
        }
        Object current = view.getTag();
        if (tag.equals(current)) {
            return;
        }
        if (current != null && !shouldReplaceDataBindingTag(current, tag, resource)) {
            return;
        }
	        try {
	            view.setTag(tag);
	            diag("[LayoutInflater] DATA BINDING TAG (repair): " + tag
	                    + " on " + classNameOf(view));
	            try {
	                System.out.println(
	                        "DATA_BINDING_TAG_REPAIR resource=0x"
	                                + Integer.toHexString(resource)
	                                + " tag=" + tag
	                                + " view=" + classNameOf(view));
	            } catch (Throwable ignored) {
	            }
	        } catch (Throwable ignored) {
	        }
    }

    private boolean shouldReplaceDataBindingTag(Object current, String expected, int resource) {
        if (current == null) {
            return true;
        }
        if (isMcdPdpDataBindingLayout(resource)) {
            return true;
        }
        if (resource == MCD_LAYOUT_APPLICATION_NOTIFICATION
                || resource == MCD_LAYOUT_CAMPAIGN_APPLICATION_NOTIFICATION
                || "layout/application_notification_0".equals(expected)
                || "layout/campaign_application_notification_0".equals(expected)) {
            return true;
        }
        if (!(current instanceof String)) {
            return false;
        }
        return false;
    }

    private boolean isMcdPdpDataBindingLayout(int resource) {
        return resource == MCD_LAYOUT_FRAGMENT_ORDER_PDP
                || resource == MCD_LAYOUT_ORDER_PDP_BUTTON_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_EDIT_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_MEAL_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_ORDER_PDP_MEAL_EDIT_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_PDP_MEAL_CHOICE_BOTTOM_LAYOUT
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_DEFAULT
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_CHOICE_CUSTOMIZATION
                || resource == MCD_LAYOUT_SKELETON_ORDER_PDP_SMALL_GRID;
    }

    private void markMcdPdpIncludeBinding(int layoutId, int includeId, View included) {
        if (!isMcdPdpDataBindingLayout(layoutId)) {
            return;
        }
        try {
            String expected = deriveDataBindingTag(layoutId);
            Object tag = included != null ? included.getTag() : null;
            System.out.println(
                    "MCD_PDP_INCLUDE_BINDING layout=0x"
                            + Integer.toHexString(layoutId)
                            + " includeId=0x" + Integer.toHexString(includeId)
                            + " expected=" + safeMcdMarkerToken(expected)
                            + " tag=" + safeMcdMarkerToken(String.valueOf(tag))
                            + " ok=" + (expected != null && expected.equals(tag)));
        } catch (Throwable ignored) {
        }
    }

    private String deriveDataBindingTag(int resource) {
        String layoutName = resolveLayoutResourceName(resource);
        if (layoutName == null || layoutName.length() == 0) {
            return null;
        }
        return layoutName + "_0";
    }

    private String resolveLayoutResourceName(int resource) {
        String layoutName = null;
        try {
            Resources res = mContext != null ? mContext.getResources() : null;
            if (res != null) {
                layoutName = normalizeLayoutResourceName(res.getResourceName(resource));
                if (layoutName == null) {
                    ResourceTable table = res.getResourceTable();
                    if (table != null) {
                        layoutName = normalizeLayoutResourceName(table.getLayoutFileName(resource));
                        if (layoutName == null) {
                            layoutName = normalizeLayoutResourceName(table.getEntryFilePath(resource));
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        if (layoutName == null) {
            layoutName = knownMcdLayoutName(resource);
        }
        return layoutName;
    }

    private String normalizeLayoutResourceName(String raw) {
        if (raw == null || raw.length() == 0) {
            return null;
        }
        String name = raw;
        int colon = name.indexOf(':');
        if (colon >= 0 && colon + 1 < name.length()) {
            name = name.substring(colon + 1);
        }
        if (name.startsWith("res/")) {
            name = name.substring(4);
        }
        if (name.endsWith(".xml")) {
            name = name.substring(0, name.length() - 4);
        }
        if (name.startsWith("layout/") || name.startsWith("layout-land/")) {
            return name;
        }
        return null;
    }

    private String knownMcdLayoutName(int resource) {
        if (resource == MCD_LAYOUT_ACTIVITY_HOME_DASHBOARD) {
            return "layout/activity_home_dashboard";
        }
        if (resource == MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT) {
            return "layout/activity_simple_product";
        }
        if (resource == MCD_LAYOUT_ACTIVITY_SIMPLE_PRODUCT_V2) {
            return "layout/activity_simple_product_v2";
        }
        if (resource == MCD_LAYOUT_APPLICATION_NOTIFICATION) {
            return "layout/application_notification";
        }
        if (resource == MCD_LAYOUT_BASE) {
            return "layout/base_layout";
        }
        if (resource == MCD_LAYOUT_BASKET_HOLDER) {
            return "layout/basket_holder";
        }
        if (resource == MCD_LAYOUT_BOTTOM_BAG_BAR) {
            return "layout/bottom_bag_bar";
        }
        if (resource == MCD_LAYOUT_BOTTOM_NAVIGATION_BAR) {
            return "layout/bottom_navigation_bar";
        }
        if (resource == MCD_LAYOUT_CAMPAIGN_APPLICATION_NOTIFICATION) {
            return "layout/campaign_application_notification";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION) {
            return "layout/fragment_deal_section";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LAYOUT_HEADER) {
            return "layout/fragment_deal_section_layout_header";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_DEAL_SECTION_LOYALTY) {
            return "layout/fragment_deal_section_loyalty";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD) {
            return "layout/fragment_home_dashboard";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION) {
            return "layout/fragment_home_dashboard_hero_section";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD_HERO_SECTION_UPDATED) {
            return "layout/fragment_home_dashboard_hero_section_updated";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_MENU_SECTION) {
            return "layout/fragment_menu_section";
        }
        if (resource == MCD_LAYOUT_FRAGMENT_ORDER) {
            return "layout/fragment_order";
        }
	        if (resource == MCD_LAYOUT_FRAGMENT_ORDER_PDP) {
	            return "layout/fragment_order_pdp";
	        }
        if (resource == MCD_LAYOUT_ORDER_PDP_BUTTON_LAYOUT) {
            return "layout/order_pdp_button_layout";
        }
        if (resource == MCD_LAYOUT_ORDER_PDP_EDIT_BOTTOM_LAYOUT) {
            return "layout/order_pdp_edit_bottom_layout";
        }
        if (resource == MCD_LAYOUT_ORDER_PDP_MEAL_BOTTOM_LAYOUT) {
            return "layout/order_pdp_meal_bottom_layout";
        }
        if (resource == MCD_LAYOUT_ORDER_PDP_MEAL_EDIT_BOTTOM_LAYOUT) {
            return "layout/order_pdp_meal_edit_bottom_layout";
        }
        if (resource == MCD_LAYOUT_PDP_MEAL_CHOICE_BOTTOM_LAYOUT) {
            return "layout/pdp_meal_choice_bottom_layout";
        }
        if (resource == MCD_LAYOUT_SKELETON_ORDER_PDP_DEFAULT) {
            return "layout/skeleton_order_pdp_default";
        }
        if (resource == MCD_LAYOUT_SKELETON_ORDER_PDP_CHOICE_CUSTOMIZATION) {
            return "layout/skeleton_order_pdp_choice_customization";
        }
        if (resource == MCD_LAYOUT_SKELETON_ORDER_PDP_SMALL_GRID) {
            return "layout/skeleton_order_pdp_small_grid";
        }
	        if (resource == MCD_LAYOUT_FRAGMENT_POPULAR_SECTION) {
	            return "layout/fragment_popular_section";
	        }
        if (resource == MCD_LAYOUT_FRAGMENT_PROMOTION_SECTION) {
            return "layout/fragment_promotion_section";
        }
        if (resource == MCD_LAYOUT_HOME_DASHBOARD_SECTION) {
            return "layout/home_dashboard_section";
        }
        if (resource == MCD_LAYOUT_HOME_MENU_GUEST_USER) {
            return "layout/home_menu_guest_user";
        }
        if (resource == MCD_LAYOUT_HOME_MENU_SECTION_FULL_MENU_ITEM) {
            return "layout/home_menu_section_full_menu_item";
        }
        if (resource == MCD_LAYOUT_HOME_MENU_SECTION_ITEM) {
            return "layout/home_menu_section_item";
        }
        if (resource == MCD_LAYOUT_CATEGORY_LIST_ITEM) {
            return "layout/category_list_item";
        }
        if (resource == MCD_LAYOUT_NEW_PLP_PRODUCT_ITEM) {
            return "layout/new_plp_product_item";
        }
        if (resource == MCD_LAYOUT_HOME_POPULAR_ITEM_ADAPTER) {
            return "layout/home_popular_item_adapter";
        }
        if (resource == MCD_LAYOUT_HOME_PROMOTION_ITEM) {
            return "layout/home_promotion_item";
        }
        if (resource == MCD_LAYOUT_HOME_PROMOTION_ITEM_UPDATED) {
            return "layout/home_promotion_item_updated";
        }
        if (resource == MCD_LAYOUT_BONUS_TILE_FRAGMENT) {
            return "layout/layout_bonus_tile_fragment";
        }
        return null;
    }

    // ── AXML-based inflation via BinaryXmlParser ─────────────────────────

    /**
     * Inflate a layout from an XmlPullParser (typically a BinaryXmlParser
     * wrapping compiled AXML data from an APK).
     */
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    /**
     * Inflate a layout from an XmlPullParser with attachToRoot control.
     * Walks the XML event stream, creating Views for each START_TAG and
     * building the parent-child tree.
     */
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        if (parser == null) return root;

        View result = root;
        try {
            // Advance to the first START_TAG
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // skip
            }
            if (type != XmlPullParser.START_TAG) {
                return root; // no elements
            }

            String tagName = parser.getName();
            boolean isMerge = "merge".equals(tagName);

            View rootView = null;
            if (isMerge) {
                if (root == null) {
                    throw new android.view.InflateException(
                            "<merge /> can only be used with a valid ViewGroup root");
                }
                inflateChildren(parser, root);
            } else {
                rootView = createViewFromTag(tagName);
                markInflateTag("ROOT", tagName, parser.getDepth());
                if (rootView == null) {
                    rootView = new FrameLayout(mContext);
                }

                // Apply attributes from the parser to the root view
                applyXmlAttributes(rootView, parser);

                // Parse LayoutParams from the root element
                ViewGroup.LayoutParams params = null;
                if (root != null) {
                    params = generateLayoutParams(root, parser);
                }

                // Set result early so even if children fail, we return the root
                result = rootView;

                // Inflate children if this is a ViewGroup
                if (rootView instanceof ViewGroup) {
                    try {
                        inflateChildren(parser, (ViewGroup) rootView);
                    } catch (Throwable childEx) {
                        diag("[LayoutInflater] inflateChildren error: " + throwableName(childEx) + ": " + childEx.getMessage());
                    }
                } else {
                    // Skip to matching END_TAG
                    skipToEndTag(parser);
                }

                // Attach to root or set layout params
                if (root != null) {
                    if (attachToRoot) {
                        try {
                            if (params != null) {
                                root.addView(rootView, params);
                            } else {
                                root.addView(rootView);
                            }
                            result = root;
                        } catch (Throwable addRootError) {
                            String detail = "[LayoutInflater] root addView failed: root="
                                    + simpleClassNameOf(root) + " child="
                                    + simpleClassNameOf(rootView) + " params="
                                    + (params != null ? simpleClassNameOf(params) : "null")
                                    + " err=" + throwableName(addRootError) + ": "
                                    + addRootError.getMessage();
                            diag(detail);
                            try {
                                android.util.Log.w("LayoutInflater", detail);
                            } catch (Throwable ignored) {
                            }
                            try {
                                root.addView(rootView);
                                result = root;
                            } catch (Throwable fallbackAddError) {
                                String fallbackDetail = "[LayoutInflater] root fallback addView failed: root="
                                        + simpleClassNameOf(root) + " child="
                                        + simpleClassNameOf(rootView) + " err="
                                        + throwableName(fallbackAddError) + ": "
                                        + fallbackAddError.getMessage();
                                diag(fallbackDetail);
                                try {
                                    android.util.Log.w("LayoutInflater", fallbackDetail);
                                } catch (Throwable ignored) {
                                }
                                result = rootView;
                            }
                        }
                    } else {
                        if (params != null) {
                            rootView.setLayoutParams(params);
                        }
                        result = rootView;
                    }
                } else {
                    result = rootView;
                }
            }
        } catch (Throwable e) {
            try {
                System.out.println(
                        "LAYOUT_INFLATER_XML_INFLATE_ERROR err="
                                + e.getClass().getName() + ":" + e.getMessage());
                StackTraceElement[] frames = e.getStackTrace();
                int max = frames != null && frames.length < 8 ? frames.length : 8;
                for (int i = 0; i < max; i++) {
                    StackTraceElement frame = frames[i];
                    System.out.println(
                            "LAYOUT_INFLATER_XML_FRAME " + i + " "
                                    + frame.getClassName() + "." + frame.getMethodName()
                                    + ":" + frame.getLineNumber());
                }
            } catch (Throwable ignored) {
            }
            diag("[LayoutInflater] inflate(parser) error: " + throwableName(e) + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Inflate child elements from the parser into the given parent ViewGroup.
     */
    private void inflateChildren(XmlPullParser parser, ViewGroup parent) throws Exception {
        int depth = parser.getDepth();
        int type;

        while (true) {
            type = parser.next();
            if (type == XmlPullParser.END_TAG && parser.getDepth() <= depth) {
                break;
            }
            if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();
            markInflateTag("CHILD", tagName, parser.getDepth());
            diag("[LayoutInflater] child tag: <" + tagName + "> in " + simpleClassNameOf(parent) + " (depth=" + parser.getDepth() + ")");

            // Handle <include> tag
            if ("include".equals(tagName)) {
                handleInclude(parser, parent);
                continue;
            }

            // Handle <requestFocus>
            if ("requestFocus".equals(tagName)) {
                skipToEndTag(parser);
                continue;
            }

            // Handle <fragment> — create Fragment and add its view to parent
            if ("fragment".equals(tagName) || "FragmentContainerView".equals(tagName)
                    || "androidx.fragment.app.FragmentContainerView".equals(tagName)) {
                try {
                    android.util.Log.d("WLInflater", "INFLATE_TRACE handleFragmentTag tag=" + tagName);
                } catch (Throwable ignored) {}
                handleFragmentTag(parser, parent, tagName);
                continue;
            }

            // Create the child View
            View child = createViewFromTag(tagName);
            if (child == null) {
                child = new View(mContext);
            }

            // Apply attributes
            applyXmlAttributes(child, parser);

            // Generate layout params from the parent
            ViewGroup.LayoutParams params = generateLayoutParams(parent, parser);

            // Inflate grandchildren if this child is a ViewGroup
            if (child instanceof ViewGroup) {
                inflateChildren(parser, (ViewGroup) child);
            } else {
                skipToEndTag(parser);
            }

            // Add to parent
            try {
                if (params != null) {
                    parent.addView(child, params);
                } else {
                    parent.addView(child);
                }
            } catch (Throwable addViewError) {
                String detail = "[LayoutInflater] addView failed: tag=<" + tagName + "> parent="
                        + simpleClassNameOf(parent) + " child="
                        + simpleClassNameOf(child) + " params="
                        + (params != null ? simpleClassNameOf(params) : "null")
                        + " err=" + throwableName(addViewError) + ": "
                        + addViewError.getMessage();
                diag(detail);
                try {
                    android.util.Log.w("LayoutInflater", detail);
                } catch (Throwable ignored) {
                }
                // Best-effort inflate for unstable standalone ART paths: keep the
                // parent tree alive even if one child cannot attach correctly.
                continue;
            }
        }
    }

    private static int sInflateTagMarkerCount;

    private static void markInflateTag(String phase, String tagName, int depth) {
        if (sInflateTagMarkerCount >= 240) {
            return;
        }
        sInflateTagMarkerCount++;
        try {
            System.out.println(
                    "LAYOUT_INFLATER_TAG " + sInflateTagMarkerCount
                            + " " + phase + " depth=" + depth + " tag=" + tagName);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Handle an <include> tag by inflating the referenced layout.
     */
    /**
     * Handle &lt;fragment&gt; or &lt;FragmentContainerView&gt; tag.
     */
    private void handleFragmentTag(XmlPullParser parser, ViewGroup parent, String tagName) throws Exception {
        String fragmentClass = null;
        int viewId = View.NO_ID;

        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                if ("name".equals(name) || "class".equals(name)) {
                    fragmentClass = bxp.getAttributeValue(i);
                } else if ("id".equals(name)) {
                    viewId = bxp.getAttributeResourceValue(i, View.NO_ID);
                }
            }
        }

        diag("[LayoutInflater] <fragment> class=" + fragmentClass + " id=0x" + Integer.toHexString(viewId));

        // PF-noice-019 (2026-05-05): for <FragmentContainerView> tags, the
        // app's view-binding code casts findChildViewById() to
        // FragmentContainerView, so the container we install MUST be that
        // type (not a plain FrameLayout). FragmentContainerView extends
        // FrameLayout so this is a drop-in subclass.
        android.widget.FrameLayout container;
        boolean wantsContainerView = "FragmentContainerView".equals(tagName)
                || "androidx.fragment.app.FragmentContainerView".equals(tagName);
        if (wantsContainerView) {
            try {
                ClassLoader cl = mContext != null ? mContext.getClassLoader() : null;
                if (cl == null) cl = Thread.currentThread().getContextClassLoader();
                Class<?> cls = cl.loadClass("androidx.fragment.app.FragmentContainerView");
                java.lang.reflect.Constructor<?> ctor = cls.getConstructor(android.content.Context.class);
                container = (android.widget.FrameLayout) ctor.newInstance(mContext);
                diag("[LayoutInflater] FragmentContainerView constructed: " + classNameOf(container));
            } catch (Throwable t) {
                diag("[LayoutInflater] FragmentContainerView ctor failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
                container = new android.widget.FrameLayout(mContext);
            }
        } else {
            container = new android.widget.FrameLayout(mContext);
        }
        if (viewId != View.NO_ID) container.setId(viewId);

        // Generic Fragment-tag handling: instantiating the Fragment and
        // running its lifecycle is the responsibility of the framework's
        // FragmentManager (real framework.jar). This shim's role is just
        // to provide the typed FragmentContainerView at the right id so
        // view-binding's cast and bind() succeed; the Fragment's view will
        // be attached when FragmentManager runs onCreate -> onCreateView
        // through the normal Android lifecycle.
        if (fragmentClass != null) {
            diag("[LayoutInflater] FragmentContainerView name=" + fragmentClass);
        }
        parent.addView(container);
        skipToEndTag(parser);
    }

    private void handleInclude(XmlPullParser parser, ViewGroup parent) throws Exception {
        // Log all attributes for debugging
        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp2 = (BinaryXmlParser) parser;
            StringBuilder sb = new StringBuilder("[LayoutInflater] <include> attrs:");
            for (int j = 0; j < bxp2.getAttributeCount(); j++) {
                sb.append(" ").append(bxp2.getAttributeName(j)).append("=").append(bxp2.getAttributeValue(j));
            }
            diag(sb.toString());
        }
        // Look for android:layout attribute (layout resource reference)
        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            int includeId = View.NO_ID;
            ViewGroup.LayoutParams includeParams = generateLayoutParams(parent, parser);
            for (int i = 0; i < count; i++) {
                int nsRes = 0;
                try { nsRes = bxp.getAttributeNameResource(i); } catch (Throwable t) {}
                String name = bxp.getAttributeName(i);
                if ("id".equals(name) || nsRes == ATTR_ID) {
                    includeId = bxp.getAttributeResourceValue(i, View.NO_ID);
                    if (includeId == View.NO_ID) {
                        includeId = bxp.getAttributeValueData(i);
                    }
                    break;
                }
            }
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                int nsRes = 0;
                try { nsRes = bxp.getAttributeNameResource(i); } catch (Throwable t) {}
                if ("layout".equals(name) || nsRes == 0x010100f2) {
                    int resId = bxp.getAttributeResourceValue(i, 0);
                    diag("[LayoutInflater] <include> attr=" + name + " nsRes=0x" + Integer.toHexString(nsRes) + " layout=0x" + Integer.toHexString(resId));
                    if (resId != 0) {
                        View included = inflate(resId, parent, false);
                        if (included != null) {
                            applyIncludeOverrides(bxp, included, includeId);
                            ensureDataBindingTag(included, resId);
                            markMcdPdpIncludeBinding(resId, includeId, included);
                            if (includeParams != null) {
                                parent.addView(included, includeParams);
                            } else {
                                parent.addView(included);
                            }
                            markIncludeInflated(resId, includeId, included, parent);
                        }
                    }
                    break;
                }
            }
        }
        skipToEndTag(parser);
    }

    private void applyIncludeOverrides(BinaryXmlParser parser, View included, int includeId) {
        if (parser == null || included == null) {
            return;
        }
        if (includeId != View.NO_ID && includeId != 0) {
            included.setId(includeId);
        }
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            int resId = 0;
            try {
                resId = parser.getAttributeNameResource(i);
            } catch (Throwable ignored) {
            }
            String name = parser.getAttributeName(i);
            int type = parser.getAttributeValueType(i);
            int data = parser.getAttributeValueData(i);
            if (resId == ATTR_VISIBILITY || "visibility".equals(name)) {
                included.setVisibility(data);
            } else if (resId == ATTR_TAG || "tag".equals(name)) {
                String tag = resolveStringAttr(parser, i,
                        mContext != null ? mContext.getResources() : null);
                if (tag != null) {
                    included.setTag(tag);
                }
            } else if (resId == ATTR_BACKGROUND || "background".equals(name)) {
                if (type == 0x1c || type == 0x1d || type == 0x1e || type == 0x1f
                        || type == 0x11) {
                    included.setBackgroundColor(data);
                }
            }
        }
    }

    private void markIncludeInflated(int layoutId, int includeId, View included, ViewGroup parent) {
        try {
            Object tag = included != null ? included.getTag() : null;
            System.out.println(
                    "LAYOUT_INCLUDE_INFLATED layout=0x" + Integer.toHexString(layoutId)
                            + " includeId=0x" + Integer.toHexString(includeId)
                            + " view=" + simpleClassNameOf(included)
                            + " id=0x" + Integer.toHexString(
                                    included != null ? included.getId() : View.NO_ID)
                            + " tag=" + safeMcdMarkerToken(String.valueOf(tag))
                            + " parent=" + simpleClassNameOf(parent));
        } catch (Throwable ignored) {
        }
    }

    /**
     * Skip events until we reach the matching END_TAG for the current element.
     */
    private void skipToEndTag(XmlPullParser parser) throws Exception {
        int depth = 1;
        while (depth > 0) {
            int type = parser.next();
            if (type == XmlPullParser.START_TAG) {
                depth++;
            } else if (type == XmlPullParser.END_TAG) {
                depth--;
            } else if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }
        }
    }

    // Common Android attribute resource IDs
    private static final int ATTR_ID             = 0x010100d0;
    private static final int ATTR_TAG            = 0x01010003;
    private static final int ATTR_TEXT           = 0x01010014;
    private static final int ATTR_TEXT_SIZE      = 0x01010095;
    private static final int ATTR_TEXT_COLOR     = 0x01010098;
    private static final int ATTR_BACKGROUND     = 0x010100d4;
    private static final int ATTR_ORIENTATION    = 0x010100c4;
    private static final int ATTR_GRAVITY        = 0x010100af;
    private static final int ATTR_PADDING        = 0x010100d5;
    private static final int ATTR_PADDING_LEFT   = 0x010100d6;
    private static final int ATTR_PADDING_TOP    = 0x010100d7;
    private static final int ATTR_PADDING_RIGHT  = 0x010100d8;
    private static final int ATTR_PADDING_BOTTOM = 0x010100d9;
    private static final int ATTR_VISIBILITY     = 0x010100dc;
    private static final int ATTR_LAYOUT_WIDTH   = 0x010100f4;
    private static final int ATTR_LAYOUT_HEIGHT  = 0x010100f5;
    private static final int ATTR_LAYOUT_WEIGHT  = 0x01010181;
    private static final int ATTR_HINT           = 0x01010043;
    private static final int ATTR_MAX_LINES      = 0x01010153;
    private static final int ATTR_SRC            = 0x01010119; // android:src
    private static final int ATTR_SINGLE_LINE    = 0x0101015d;
    private static final int ATTR_CLICKABLE      = 0x010100e5;

    /**
     * Apply XML attributes from a BinaryXmlParser to a View.
     */
    private void applyXmlAttributes(View view, XmlPullParser parser) {
        if (!(parser instanceof BinaryXmlParser)) return;
        BinaryXmlParser bxp = (BinaryXmlParser) parser;
        int count = bxp.getAttributeCount();
        if (count < 0) return;

        Resources res = (mContext != null) ? mContext.getResources() : null;

        // PF-noice-028 (2026-05-06): pull custom attrs by name that we want
        // to apply post-create. app:menu on BottomNavigationView is a
        // resource reference to the menu XML; we capture it here and call
        // inflateMenu(resId) reflectively below the main switch.
        int menuResId = 0;
        for (int i = 0; i < count; i++) {
            String attrName = bxp.getAttributeName(i);
            if ("menu".equals(attrName)) {
                menuResId = bxp.getAttributeValueData(i);
                break;
            }
        }

        for (int i = 0; i < count; i++) {
            int resId = bxp.getAttributeNameResource(i);
            String attrName = bxp.getAttributeName(i);
            int attrType = bxp.getAttributeValueType(i);
            int attrData = bxp.getAttributeValueData(i);

            // Check for tag by name too (some AXML uses name-based attrs)
            if ("tag".equals(attrName) && resId != ATTR_TAG) {
                String tv = resolveStringAttr(bxp, i, res);
                if (tv == null) tv = bxp.getAttributeValue(i);
                if (tv != null) {
                    view.setTag(tv);
                    if (tv.startsWith("layout/")) {
                        diag("[LayoutInflater] DATA BINDING TAG (by name): " + tv + " on " + classNameOf(view));
                    }
                }
            }

            switch (resId) {
                case ATTR_ID:
                    view.setId(attrData);
                    break;

                case ATTR_TAG:
                    // Data binding sets android:tag on views (e.g., "layout/base_layout_0")
                    String tagStr = resolveStringAttr(bxp, i, res);
                    if (tagStr == null) {
                        tagStr = bxp.getAttributeValue(i);
                    }
                    if (tagStr != null) {
                        view.setTag(tagStr);
                        if (tagStr.startsWith("layout/")) {
                            diag("[LayoutInflater] DATA BINDING TAG: " + tagStr + " on " + classNameOf(view));
                        }
                    }
                    break;

                case ATTR_TEXT:
                    if (view instanceof TextView) {
                        String text = resolveStringAttr(bxp, i, res);
                        if (text != null) {
                            if (text.length() > 100) {
                                diag("[LayoutInflater] WARNING: huge text (" + text.length() + " chars): " + text.substring(0, 80) + "...");
                            }
                            ((TextView) view).setText(text);
                        }
                    }
                    break;

                case ATTR_TEXT_SIZE:
                    if (view instanceof TextView) {
                        float size = bxp.getAttributeFloatValue(i, 0f);
                        if (size > 0) ((TextView) view).setTextSize(size);
                    }
                    break;

                case ATTR_TEXT_COLOR:
                    if (view instanceof TextView) {
                        int color = resolveColorAttr(attrType, attrData, res);
                        ((TextView) view).setTextColor(color);
                    }
                    break;

                case ATTR_HINT:
                    if (view instanceof TextView) {
                        String hint = resolveStringAttr(bxp, i, res);
                        if (hint != null) {
                            ((TextView) view).setHint(hint);
                        }
                    }
                    break;

                case ATTR_MAX_LINES:
                    if (view instanceof TextView) {
                        ((TextView) view).setMaxLines(attrData);
                    }
                    break;

                case ATTR_SINGLE_LINE:
                    if (view instanceof TextView) {
                        ((TextView) view).setSingleLine(attrData != 0);
                    }
                    break;

                case ATTR_BACKGROUND:
                    if (attrType == 0x1c || attrType == 0x1d ||
                        attrType == 0x1e || attrType == 0x1f ||
                        attrType == 0x11) {
                        view.setBackgroundColor(attrData);
                    } else if (attrType == 0x01 && res != null) {
                        // reference to color resource
                        view.setBackgroundColor(res.getColor(attrData));
                    }
                    break;

                case ATTR_SRC:
                    // android:src for ImageView — attrType 0x01 = reference
                    if (view instanceof android.widget.ImageView && attrType == 0x01 && attrData != 0) {
                        ((android.widget.ImageView) view).setImageResource(attrData);
                    }
                    break;

                case ATTR_ORIENTATION:
                    if (view instanceof LinearLayout) {
                        ((LinearLayout) view).setOrientation(attrData);
                    }
                    break;

                case ATTR_GRAVITY:
                    if (view instanceof TextView) {
                        ((TextView) view).setGravity(attrData);
                    }
                    break;

                case ATTR_PADDING: {
                    int px = bxp.getAttributeIntValue(i, 0);
                    view.setPadding(px, px, px, px);
                    break;
                }

                case ATTR_PADDING_LEFT:
                    view.setPadding(bxp.getAttributeIntValue(i, 0),
                            view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_TOP:
                    view.setPadding(view.getPaddingLeft(),
                            bxp.getAttributeIntValue(i, 0),
                            view.getPaddingRight(), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_RIGHT:
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                            bxp.getAttributeIntValue(i, 0), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_BOTTOM:
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                            view.getPaddingRight(), bxp.getAttributeIntValue(i, 0));
                    break;

                case ATTR_VISIBILITY:
                    view.setVisibility(attrData);
                    break;

                case ATTR_CLICKABLE:
                    view.setClickable(attrData != 0);
                    break;

                default:
                    // Fall back to name-based attribute matching
                    if (attrName != null) {
                        String rawValue = null;
                        if (attrType == 0x01 || attrType == 0x02 || attrType == 0x03) {
                            try {
                                rawValue = bxp.getAttributeValue(i);
                            } catch (Throwable ignored) {
                            }
                        }
                        applyByName(view, attrName, attrType, attrData,
                                rawValue, res);
                    }
                    break;
            }
        }

        // PF-noice-028 (2026-05-06): apply captured app:menu reflectively.
        if (menuResId != 0) {
            try {
                java.lang.reflect.Method inflateMenu = view.getClass().getMethod("inflateMenu", int.class);
                inflateMenu.invoke(view, menuResId);
                diag("[LayoutInflater] inflateMenu(0x" + Integer.toHexString(menuResId)
                        + ") OK on " + classNameOf(view));
            } catch (NoSuchMethodException nsme) {
                // Not all views with a "menu" attribute have inflateMenu (e.g.,
                // a Toolbar uses setMenu). Try Toolbar-style menu set.
                try {
                    java.lang.reflect.Method getMenu = view.getClass().getMethod("getMenu");
                    Object menu = getMenu.invoke(view);
                    if (menu != null) {
                        // Use MenuInflater to populate the Menu instance.
                        Class<?> menuInflater = Class.forName("android.view.MenuInflater");
                        Object mi = menuInflater.getConstructor(android.content.Context.class)
                                .newInstance(mContext);
                        java.lang.reflect.Method inflate = menuInflater.getMethod("inflate",
                                int.class, Class.forName("android.view.Menu"));
                        inflate.invoke(mi, menuResId, menu);
                        diag("[LayoutInflater] MenuInflater.inflate(0x" + Integer.toHexString(menuResId)
                                + ") OK on " + classNameOf(view));
                    }
                } catch (Throwable t2) {
                    diag("[LayoutInflater] menu inflate fallback failed on " + classNameOf(view)
                            + ": " + t2.getClass().getSimpleName() + ": " + t2.getMessage());
                }
            } catch (Throwable t) {
                diag("[LayoutInflater] inflateMenu failed on " + classNameOf(view)
                        + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    /**
     * Apply attribute by name (fallback when resource ID is unknown).
     */
    private void applyByName(View view, String name, int type, int data,
                              String rawValue, Resources res) {
        if ("text".equals(name) && view instanceof TextView) {
            String text = rawValue;
            if (type == 0x01 && res != null && data != 0) {
                text = res.getString(data);
            }
            if (text != null) ((TextView) view).setText(text);
        } else if ("textSize".equals(name) && view instanceof TextView) {
            float size = (type == 0x04) ? Float.intBitsToFloat(data) : data;
            if (size > 0) ((TextView) view).setTextSize(size);
        } else if ("textColor".equals(name) && view instanceof TextView) {
            ((TextView) view).setTextColor(data);
        } else if ("orientation".equals(name) && view instanceof LinearLayout) {
            ((LinearLayout) view).setOrientation(data);
        } else if (("src".equals(name) || "srcCompat".equals(name)) && view instanceof android.widget.ImageView) {
            if (type == 0x01 && data != 0) { // reference
                ((android.widget.ImageView) view).setImageResource(data);
            }
        } else if (("progress".equals(name) || "value".equals(name))
                && view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view).setProgress(resolveIntAttr(type, data, rawValue));
        } else if ("max".equals(name) && view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view).setMax(resolveIntAttr(type, data, rawValue));
        } else if ("min".equals(name) && view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view).setMin(resolveIntAttr(type, data, rawValue));
        } else if ("checked".equals(name) && view instanceof android.widget.CompoundButton) {
            ((android.widget.CompoundButton) view).setChecked(resolveBooleanAttr(type, data, rawValue));
        } else if ("contentDescription".equals(name)) {
            String text = rawValue;
            if (type == 0x01 && res != null && data != 0) {
                text = res.getString(data);
            }
            if (text != null) view.setContentDescription(text);
        } else if ("paddingStart".equals(name) || "paddingLeft".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(px, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        } else if ("paddingEnd".equals(name) || "paddingRight".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), px, view.getPaddingBottom());
        } else if ("paddingTop".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(view.getPaddingLeft(), px, view.getPaddingRight(), view.getPaddingBottom());
        } else if ("paddingBottom".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), px);
        } else if ("paddingHorizontal".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(px, view.getPaddingTop(), px, view.getPaddingBottom());
        } else if ("paddingVertical".equals(name)) {
            int px = resolveLayoutDimensionValue(type, data, res);
            view.setPadding(view.getPaddingLeft(), px, view.getPaddingRight(), px);
        }
    }

    private int resolveIntAttr(int type, int data, String rawValue) {
        if (type == 0x10 || type == 0x11 || type == 0x12) {
            return data;
        }
        if (type == 0x04) {
            return (int) Float.intBitsToFloat(data);
        }
        if (rawValue != null) {
            try {
                return (int) Float.parseFloat(rawValue);
            } catch (Throwable ignored) {
            }
        }
        return data;
    }

    private boolean resolveBooleanAttr(int type, int data, String rawValue) {
        if (type == 0x12) {
            return data != 0;
        }
        if (rawValue != null) {
            return "true".equals(rawValue) || "1".equals(rawValue);
        }
        return data != 0;
    }

    /**
     * Resolve a string attribute value, handling both raw strings and @string/ references.
     */
    private String resolveStringAttr(BinaryXmlParser parser, int index, Resources res) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);

        // If it's a reference to a string resource, resolve it
        if (type == 0x01 && res != null && data != 0) {
            String resolved = res.getString(data);
            if (resolved != null && !resolved.startsWith("string_")) {
                return resolved;
            }
        }

        // Otherwise use the raw string value
        return parser.getAttributeValue(index);
    }

    /**
     * Resolve a color attribute value, handling both inline colors and @color/ references.
     */
    private int resolveColorAttr(int type, int data, Resources res) {
        if (type == 0x01 && res != null && data != 0) {
            return res.getColor(data);
        }
        return data;
    }

    /**
     * Generate LayoutParams for a child from the parser's current attributes.
     * Reads layout_width, layout_height, layout_weight.
     */
    // layout attribute IDs
    private static final int ATTR_LAYOUT_GRAVITY = 0x010100b3;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_LEFT = 0x0101018b;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_TOP = 0x0101018c;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_RIGHT = 0x0101018d;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_BOTTOM = 0x0101018e;
    private static final int ATTR_LAYOUT_CENTER_IN_PARENT = 0x0101018f;
    private static final int ATTR_LAYOUT_CENTER_HORIZONTAL = 0x01010190;
    private static final int ATTR_LAYOUT_CENTER_VERTICAL = 0x01010191;

    private ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent, XmlPullParser parser) {
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        float weight = 0f;
        int layoutGravity = -1;
        int marginAll = Integer.MIN_VALUE;
        int marginLeft = Integer.MIN_VALUE;
        int marginTop = Integer.MIN_VALUE;
        int marginRight = Integer.MIN_VALUE;
        int marginBottom = Integer.MIN_VALUE;
        Resources res = (mContext != null) ? mContext.getResources() : null;

        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            // First pass: explicit layout_gravity
            for (int i = 0; i < count; i++) {
                int resId = bxp.getAttributeNameResource(i);
                String attrName = bxp.getAttributeName(i);
                int type = bxp.getAttributeValueType(i);
                int data = bxp.getAttributeValueData(i);

                if (resId == ATTR_LAYOUT_WIDTH) {
                    width = resolveLayoutDimensionValue(type, data, res);
                } else if (resId == ATTR_LAYOUT_HEIGHT) {
                    height = resolveLayoutDimensionValue(type, data, res);
                } else if (resId == ATTR_LAYOUT_WEIGHT) {
                    weight = Float.intBitsToFloat(data);
                } else if (resId == ATTR_LAYOUT_GRAVITY) {
                    layoutGravity = data;
                } else if ("layout_margin".equals(attrName)) {
                    marginAll = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginLeft".equals(attrName)
                        || "layout_marginStart".equals(attrName)) {
                    marginLeft = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginTop".equals(attrName)) {
                    marginTop = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginRight".equals(attrName)
                        || "layout_marginEnd".equals(attrName)) {
                    marginRight = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginBottom".equals(attrName)) {
                    marginBottom = resolveLayoutDimensionValue(type, data, res);
                }
            }
            // Second pass: map RelativeLayout attrs to gravity (for FrameLayout fallback)
            if (layoutGravity == -1) {
                int g = 0;
                for (int i = 0; i < count; i++) {
                    int resId = bxp.getAttributeNameResource(i);
                    int data = bxp.getAttributeValueData(i);
                    if (data == 0) continue; // false (true = -1 or nonzero)
                    if (resId == ATTR_LAYOUT_CENTER_IN_PARENT) g = 0x11; // CENTER
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_TOP) g |= 0x30; // TOP
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_BOTTOM) g |= 0x50; // BOTTOM
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_LEFT) g |= 0x03; // LEFT
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_RIGHT) g |= 0x05; // RIGHT
                    else if (resId == ATTR_LAYOUT_CENTER_HORIZONTAL) g |= 0x01; // CENTER_HORIZONTAL
                    else if (resId == ATTR_LAYOUT_CENTER_VERTICAL) g |= 0x10; // CENTER_VERTICAL
                }
                if (g != 0) layoutGravity = g;
                // Debug: log unrecognized layout attributes
                if (g == 0) {
                    for (int i = 0; i < count; i++) {
                        int resId = bxp.getAttributeNameResource(i);
                        String name = bxp.getAttributeName(i);
                        if (name != null && name.startsWith("layout_") && resId != ATTR_LAYOUT_WIDTH
                                && resId != ATTR_LAYOUT_HEIGHT && resId != ATTR_LAYOUT_WEIGHT) {
                            diag("[LayoutInflater] unhandled attr: " + name + " resId=0x" + Integer.toHexString(resId) + " data=" + bxp.getAttributeValueData(i));
                        }
                    }
                }
            }
        }

        // PF-noice-029 (2026-05-06): generate ConstraintLayout.LayoutParams
        // when parent is a ConstraintLayout, and populate its constraint
        // fields reflectively from the XML's app:layout_constraint* attrs.
        // Without this, ConstraintLayout falls back to default top-left
        // stacking and BottomNavigationView/FAB end up in the wrong place.
        if (isConstraintLayoutInstance(parent) && parser instanceof BinaryXmlParser) {
            ViewGroup.LayoutParams clp = generateConstraintLayoutParams(
                    parent, (BinaryXmlParser) parser, width, height,
                    marginAll, marginLeft, marginTop, marginRight, marginBottom);
            if (clp != null) return clp;
        }

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.weight = weight;
            if (layoutGravity >= 0) lp.gravity = layoutGravity;
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        if (parent instanceof android.widget.FrameLayout) {
            android.widget.FrameLayout.LayoutParams lp =
                new android.widget.FrameLayout.LayoutParams(width, height);
            if (layoutGravity >= 0) lp.gravity = layoutGravity;
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        if (parent instanceof android.widget.RelativeLayout && parser instanceof BinaryXmlParser) {
            android.widget.RelativeLayout.LayoutParams lp =
                new android.widget.RelativeLayout.LayoutParams(width, height);
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                int resId = bxp.getAttributeNameResource(i);
                int data = bxp.getAttributeValueData(i);
                // Map RelativeLayout rule attributes
                switch (resId) {
                    case 0x0101018b: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_LEFT, data != 0 ? -1 : 0); break;
                    case 0x0101018c: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP, data != 0 ? -1 : 0); break;
                    case 0x0101018d: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT, data != 0 ? -1 : 0); break;
                    case 0x0101018e: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM, data != 0 ? -1 : 0); break;
                    case 0x0101018f: lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT, data != 0 ? -1 : 0); break;
                    case 0x01010190: lp.addRule(android.widget.RelativeLayout.CENTER_HORIZONTAL, data != 0 ? -1 : 0); break;
                    case 0x01010191: lp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL, data != 0 ? -1 : 0); break;
                    case 0x01010182: lp.addRule(android.widget.RelativeLayout.BELOW, data); break;
                    case 0x01010183: lp.addRule(android.widget.RelativeLayout.ALIGN_LEFT, data); break;
                    case 0x01010184: lp.addRule(android.widget.RelativeLayout.ABOVE, data); break;
                    case 0x01010185: lp.addRule(android.widget.RelativeLayout.ALIGN_RIGHT, data); break;
                    case 0x01010186: lp.addRule(android.widget.RelativeLayout.ALIGN_TOP, data); break;
                    case 0x01010187: lp.addRule(android.widget.RelativeLayout.ALIGN_BOTTOM, data); break;
                    case 0x01010188: lp.addRule(android.widget.RelativeLayout.LEFT_OF, data); break;
                    case 0x01010189: lp.addRule(android.widget.RelativeLayout.RIGHT_OF, data); break;
                }
            }
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            applyMargins((ViewGroup.MarginLayoutParams) lp,
                    marginAll, marginLeft, marginTop, marginRight, marginBottom);
        }
        return lp;
    }

    private void applyMargins(ViewGroup.MarginLayoutParams lp, int all, int left, int top, int right, int bottom) {
        if (lp == null) return;
        if (all != Integer.MIN_VALUE) {
            lp.setMargins(all, all, all, all);
        }
        int resolvedLeft = left != Integer.MIN_VALUE ? left : lp.leftMargin;
        int resolvedTop = top != Integer.MIN_VALUE ? top : lp.topMargin;
        int resolvedRight = right != Integer.MIN_VALUE ? right : lp.rightMargin;
        int resolvedBottom = bottom != Integer.MIN_VALUE ? bottom : lp.bottomMargin;
        lp.setMargins(resolvedLeft, resolvedTop, resolvedRight, resolvedBottom);
    }

    private int resolveLayoutDimensionValue(int type, int data, Resources res) {
        if (type == 0x01 && res != null && data != 0) {
            try {
                return res.getDimensionPixelOffset(data);
            } catch (Throwable ignored) {
            }
        }
        return resolveLayoutDimension(data);
    }

    /**
     * Resolve a layout dimension value: MATCH_PARENT (-1), WRAP_CONTENT (-2), or px.
     */
    private int resolveLayoutDimension(int data) {
        if (data == -1 || data == -2) {
            return data; // MATCH_PARENT or WRAP_CONTENT
        }
        // Unpack dimension: low 4 bits = unit type, high bits = value (complex format)
        // Android complex dimension: bits 0-3 = unit (0=px, 1=dp, 2=sp, 3=pt, 4=in, 5=mm)
        //                            bits 4-7 = radix (0=23p0, 1=16p7, 2=8p15, 3=0p23)
        //                            bits 8-31 = mantissa
        int unitType = data & 0xF;
        int radix = (data >> 4) & 0x3;
        int mantissa = data >> 8;
        float value;
        switch (radix) {
            case 0: value = mantissa; break;              // 23p0
            case 1: value = mantissa / 128.0f; break;     // 16p7
            case 2: value = mantissa / 32768.0f; break;   // 8p15
            case 3: value = mantissa / 8388608.0f; break;  // 0p23
            default: value = mantissa; break;
        }
        float density = android.content.res.Resources.getSystem().getDisplayMetrics().density;
        switch (unitType) {
            case 0: return (int) value;                   // px
            case 1: return (int) (value * density + 0.5f); // dp
            case 2: return (int) (value * density + 0.5f); // sp (treat as dp for layout)
            default: return (int) (value * density + 0.5f); // fallback: treat as dp
        }
    }

    /**
     * Load binary layout XML bytes from the extracted APK res/ directory.
     */
    private byte[] loadLayoutXml(String layoutPath) {
        // Try to find the file in the APK's extracted directory
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                String resDir = null;
                android.app.ApkInfo info = server.getApkInfo();
                if (info != null) resDir = info.resDir;
                if (resDir == null) resDir = info != null ? info.extractDir : null;

                diag("[LayoutInflater] loadLayoutXml: resDir=" + resDir + " path=" + layoutPath);
                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    diag("[LayoutInflater] loadLayoutXml: trying " + xmlFile.getAbsolutePath() + " exists=" + xmlFile.exists());
                    if (!xmlFile.exists()) {
                        xmlFile = new java.io.File(resDir, layoutPath.startsWith("res/") ? layoutPath.substring(4) : layoutPath);
                        diag("[LayoutInflater] loadLayoutXml: trying " + xmlFile.getAbsolutePath() + " exists=" + xmlFile.exists());
                    }
                    if (xmlFile.exists()) {
                        return readFile(xmlFile);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private String layoutXmlPathFromName(String layoutName) {
        if (layoutName == null || layoutName.length() == 0) {
            return null;
        }
        String normalized = normalizeLayoutResourceName(layoutName);
        if (normalized == null) {
            return null;
        }
        return "res/" + normalized + ".xml";
    }

    private byte[] requireBinaryAxml(byte[] data, String source, int resource) {
        if (data == null) {
            return null;
        }
        if (isBinaryAxml(data)) {
            return data;
        }
        try {
            diag("[LayoutInflater] " + source + ": non-binary XML for 0x"
                    + Integer.toHexString(resource) + " (" + data.length
                    + " bytes), continuing");
            System.out.println(
                    "LAYOUT_INFLATER_AXML_NON_BINARY resource=0x"
                            + Integer.toHexString(resource)
                            + " source=" + safeMcdMarkerToken(source)
                            + " bytes=" + data.length);
        } catch (Throwable ignored) {
        }
        return null;
    }

    private boolean isBinaryAxml(byte[] data) {
        return data != null
                && data.length >= 4
                && (data[0] & 0xFF) == 0x03
                && (data[1] & 0xFF) == 0x00
                && (data[2] & 0xFF) == 0x08
                && (data[3] & 0xFF) == 0x00;
    }

    private static byte[] readFile(java.io.File file) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(4096);
            byte[] buffer = new byte[4096];
            while (true) {
                int n = fis.read(buffer);
                if (n < 0) {
                    break;
                }
                if (n > 0) {
                    out.write(buffer, 0, n);
                }
            }
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private static void markRealMcdXmlInflated(int resource, String layoutName, View view) {
        if (layoutName == null || view == null) {
            return;
        }
        if (!layoutName.startsWith("layout/fragment_home_dashboard_hero_section")
                && !layoutName.startsWith("layout/fragment_menu_section")
                && !layoutName.startsWith("layout/home_menu_guest_user")
                && !layoutName.startsWith("layout/fragment_promotion_section")
                && !layoutName.startsWith("layout/fragment_popular_section")
                && !layoutName.startsWith("layout/home_promotion_item")
                && !layoutName.startsWith("layout/home_popular_item_adapter")
                && !layoutName.startsWith("layout/fragment_order")
                && !layoutName.startsWith("layout/category_list_item")
                && !layoutName.startsWith("layout/new_plp_product_item")) {
            return;
        }
        try {
            System.out.println(
                    "MCD_REAL_XML_INFLATED layout=" + safeMcdMarkerToken(layoutName)
                            + " resource=0x" + Integer.toHexString(resource)
                            + " root=" + simpleClassNameOf(view));
        } catch (Throwable ignored) {
        }
    }

    /**
     * Build a compact description of a View tree for diagnostics.
     * Example: "FrameLayout(id=0x7f0a004b)[TextView, Button]"
     */
    private static String describeViewTree(View view, int depth) {
        if (view == null) return "null";
        if (depth > 4) return "..."; // prevent infinite recursion

        StringBuilder sb = new StringBuilder();
        sb.append(simpleClassNameOf(view));
        int id = view.getId();
        if (id != 0 && id != View.NO_ID) {
            sb.append("(id=0x");
            sb.append(Integer.toHexString(id));
            sb.append(')');
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int count = vg.getChildCount();
            if (count > 0) {
                sb.append('[');
                for (int i = 0; i < count; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(describeViewTree(vg.getChildAt(i), depth + 1));
                }
                sb.append(']');
            }
        }
        return sb.toString();
    }

    public View createView(String name, String prefix, AttributeSet attrs) {
        try {
            String fullName = (prefix != null) ? prefix + name : name;
            Class<?> cls = Class.forName(fullName);
            return (View) cls.getConstructor(Context.class).newInstance(mContext);
        } catch (Exception e) {
            return null;
        }
    }

    public View onCreateView(String name, AttributeSet attrs) { return null; }
    public View onCreateView(View parent, String name, AttributeSet attrs) { return null; }

    public Factory getFactory() { return mFactory; }
    public Factory2 getFactory2() { return mFactory2; }
    public Filter getFilter() { return null; }
    public void setFactory(Factory factory) { mFactory = factory; }
    public void setFactory(Object factory) { /* legacy compat */ }
    public void setFactory2(Factory2 factory) { mFactory2 = factory; }
    public void setFactory2(Object factory) { /* legacy compat */ }

    private Factory mFactory;
    private Factory2 mFactory2;

    public interface Factory {
        View onCreateView(String name, Context context, AttributeSet attrs);
    }
    public interface Factory2 extends Factory {
        View onCreateView(View parent, String name, Context context, AttributeSet attrs);
    }
    public void setFilter(Filter filter) {}

    /** Inner Filter interface for allowed class filtering during inflation. */
    public interface Filter {
        boolean onLoadClass(Class clazz);
    }
}
