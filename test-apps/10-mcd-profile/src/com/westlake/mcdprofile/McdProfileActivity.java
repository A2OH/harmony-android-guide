package com.westlake.mcdprofile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.westlake.engine.WestlakeLauncher;

public final class McdProfileActivity extends Activity {
    private static final String LIVE_MENU_URL =
            "https://dummyjson.com/recipes?limit=8&select=name,image,rating,reviewCount,mealType,cuisine,difficulty";
    private static final int MAX_ITEMS = 8;
    private static final int TAB_HOME = 0;
    private static final int TAB_MENU = 1;
    private static final int TAB_CART = 2;
    private static final int TAB_DEALS = 3;

    private volatile boolean renderDirty;
    private volatile boolean xmlInflated;
    private volatile boolean networkLoading;
    private volatile boolean restMatrixPassed;
    private volatile boolean storageRoundTrip;
    private volatile boolean checkedOut;
    private volatile int activeTab = TAB_MENU;
    private volatile int selectedIndex;
    private volatile int selectedPriceCents = 599;
    private volatile int cartCount;
    private volatile int cartTotalCents;
    private volatile int menuCount = 8;
    private volatile int menuOffset;
    private volatile int materialViewCount;
    private volatile int imageFetchCount;
    private volatile int adapterGetViewCount;
    private volatile String category = "Burgers";
    private volatile String selectedName = "Westlake Burger";
    private volatile String selectedMeta = "combo - 4.7 - pickup";
    private volatile String selectedPrice = "$5.99";
    private volatile String heroTitle = "Today only";
    private volatile String heroSubtitle = "Live menu loaded through Westlake";
    private volatile String lastAction = "Ready";
    private volatile String feedStatus = "Waiting for live menu";
    private volatile String restStatus = "REST pending";

    private volatile String row1Name = "Westlake Burger";
    private volatile String row1Meta = "combo - 4.7 - image 0 bytes";
    private volatile String row1Price = "$5.99";
    private volatile String row2Name = "Classic Fries";
    private volatile String row2Meta = "side - 4.6 - image 0 bytes";
    private volatile String row2Price = "$2.49";
    private volatile String row3Name = "Chicken Stack";
    private volatile String row3Meta = "sandwich - 4.8 - image 0 bytes";
    private volatile String row3Price = "$6.49";
    private volatile String row4Name = "Iced Coffee";
    private volatile String row4Meta = "drink - 4.5 - image 0 bytes";
    private volatile String row4Price = "$3.19";
    private volatile String row5Name = "Breakfast Muffin";
    private volatile String row5Meta = "breakfast - 4.4 - image 0 bytes";
    private volatile String row5Price = "$4.29";

    private volatile byte[] row1ImageData = new byte[0];
    private volatile byte[] row2ImageData = new byte[0];
    private volatile byte[] row3ImageData = new byte[0];
    private volatile byte[] row4ImageData = new byte[0];
    private volatile byte[] row5ImageData = new byte[0];
    private volatile int row1ImageHash;
    private volatile int row2ImageHash;
    private volatile int row3ImageHash;
    private volatile int row4ImageHash;
    private volatile int row5ImageHash;
    private volatile int row1ImageBytes;
    private volatile int row2ImageBytes;
    private volatile int row3ImageBytes;
    private volatile int row4ImageBytes;
    private volatile int row5ImageBytes;
    private volatile String row1ImageUrl = "";
    private volatile String row2ImageUrl = "";
    private volatile String row3ImageUrl = "";
    private volatile String row4ImageUrl = "";
    private volatile String row5ImageUrl = "";

    private TextView heroText;
    private TextView cartSummary;
    private ListView menuList;
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        McdProfileLog.mark("ACTIVITY_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        westlakeInitDefaults();
        super.onCreate(savedInstanceState);
        McdProfileLog.mark("ACTIVITY_ON_CREATE_OK", "activity=" + getClass().getName());
        inflateXmlUi();
        loadCartState();
        applySelection(0);
        updateRows();
        updateUi("created");
        fetchLiveMenu();
    }

    public void westlakeInitDefaults() {
        McdProfileLog.mark("WESTLAKE_DEFAULTS_BEGIN", "scalars=true");
        if (activeTab == 0) activeTab = TAB_MENU;
        if (menuCount == 0) menuCount = MAX_ITEMS;
        if (category == null) category = "Burgers";
        if (selectedName == null) selectedName = "Westlake Burger";
        if (selectedMeta == null) selectedMeta = "combo - 4.7 - pickup";
        if (selectedPrice == null) selectedPrice = "$5.99";
        if (heroTitle == null) heroTitle = "Today only";
        if (heroSubtitle == null) heroSubtitle = "Live menu loaded through Westlake";
        if (lastAction == null) lastAction = "Ready";
        if (feedStatus == null) feedStatus = "Waiting for live menu";
        if (restStatus == null) restStatus = "REST pending";
        if (row1Name == null) row1Name = "Westlake Burger";
        if (row1Meta == null) row1Meta = "combo - 4.7 - image 0 bytes";
        if (row1Price == null) row1Price = "$5.99";
        if (row2Name == null) row2Name = "Classic Fries";
        if (row2Meta == null) row2Meta = "side - 4.6 - image 0 bytes";
        if (row2Price == null) row2Price = "$2.49";
        if (row3Name == null) row3Name = "Chicken Stack";
        if (row3Meta == null) row3Meta = "sandwich - 4.8 - image 0 bytes";
        if (row3Price == null) row3Price = "$6.49";
        if (row4Name == null) row4Name = "Iced Coffee";
        if (row4Meta == null) row4Meta = "drink - 4.5 - image 0 bytes";
        if (row4Price == null) row4Price = "$3.19";
        if (row5Name == null) row5Name = "Breakfast Muffin";
        if (row5Meta == null) row5Meta = "breakfast - 4.4 - image 0 bytes";
        if (row5Price == null) row5Price = "$4.29";
        if (row1ImageData == null) row1ImageData = new byte[0];
        if (row2ImageData == null) row2ImageData = new byte[0];
        if (row3ImageData == null) row3ImageData = new byte[0];
        if (row4ImageData == null) row4ImageData = new byte[0];
        if (row5ImageData == null) row5ImageData = new byte[0];
        updateRows();
        applySelection(selectedIndex);
        McdProfileLog.mark("WESTLAKE_DEFAULTS_OK", "items=5");
    }

    private void inflateXmlUi() {
        McdProfileLog.mark("XML_INFLATE_BEGIN", "layout=activity_mcd_profile");
        View root = LayoutInflater.from(this).inflate(R.layout.activity_mcd_profile, null);
        setContentView(root);
        xmlInflated = true;
        materialViewCount = countMaterialViews(root);
        bindXml(root);
        probeXmlLayout(root);
        McdProfileLog.mark("XML_INFLATE_OK",
                "root=" + className(root)
                        + " views=" + countViews(root)
                        + " materialViews=" + materialViewCount
                        + " source=compiled_apk_xml");
    }

    private void bindXml(View root) {
        require(root, R.id.mcd_root, android.widget.LinearLayout.class, "root");
        require(root, R.id.mcd_search_layout, TextInputLayout.class, "TextInputLayout");
        require(root, R.id.mcd_search_input, TextInputEditText.class, "TextInputEditText");
        require(root, R.id.mcd_category_group, ChipGroup.class, "ChipGroup");
        require(root, R.id.mcd_chip_breakfast, Chip.class, "ChipBreakfast");
        require(root, R.id.mcd_chip_burgers, Chip.class, "ChipBurgers");
        require(root, R.id.mcd_chip_chicken, Chip.class, "ChipChicken");
        require(root, R.id.mcd_chip_drinks, Chip.class, "ChipDrinks");
        require(root, R.id.mcd_hero_card, MaterialCardView.class, "MaterialCardView");
        require(root, R.id.mcd_hero_image, ImageView.class, "ImageView");
        require(root, R.id.mcd_checkout_button, MaterialButton.class, "MaterialButton");
        require(root, R.id.mcd_bottom_nav, BottomNavigationView.class, "BottomNavigationView");
        View hero = require(root, R.id.mcd_hero_text, TextView.class, "heroText");
        if (hero instanceof TextView) heroText = (TextView) hero;
        View cart = require(root, R.id.mcd_cart_summary, TextView.class, "cartSummary");
        if (cart instanceof TextView) cartSummary = (TextView) cart;
        View list = require(root, R.id.mcd_menu_list, ListView.class, "ListView");
        if (list instanceof ListView) menuList = (ListView) list;

        setClick(root, R.id.mcd_chip_breakfast, new View.OnClickListener() {
            public void onClick(View view) { selectBreakfast(); }
        });
        setClick(root, R.id.mcd_chip_burgers, new View.OnClickListener() {
            public void onClick(View view) { selectBurgers(); }
        });
        setClick(root, R.id.mcd_chip_chicken, new View.OnClickListener() {
            public void onClick(View view) { selectChicken(); }
        });
        setClick(root, R.id.mcd_chip_drinks, new View.OnClickListener() {
            public void onClick(View view) { selectDrinks(); }
        });
        setClick(root, R.id.mcd_checkout_button, new View.OnClickListener() {
            public void onClick(View view) { checkout(); }
        });

        adapter = new MenuAdapter();
        if (menuList != null) {
            menuList.setAdapter(adapter);
            menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    selectMenuPosition(position);
                    McdProfileLog.mark("ADAPTER_ITEM_CLICK_OK",
                            "position=" + position + " id=" + id + " count=" + adapter.getCount());
                }
            });
        }
        McdProfileLog.mark("XML_BIND_OK",
                "list=" + (menuList != null) + " adapter=" + className(adapter)
                        + " materialViews=" + materialViewCount);
    }

    private View require(View root, int id, Class expected, String label) {
        View view = root != null ? root.findViewById(id) : null;
        if (view == null && "root".equals(label) && expected.isInstance(root)) {
            view = root;
        }
        if (view == null || !expected.isInstance(view)) {
            view = findByClass(root, expected, occurrenceFor(label));
        }
        if (view == null || !expected.isInstance(view)) {
            McdProfileLog.mark("XML_TAG_WARN",
                    "id=" + label + " expected=" + McdProfileLog.token(expected.getName())
                            + " actual=" + McdProfileLog.token(className(view)));
            return null;
        }
        McdProfileLog.mark("XML_TAG_OK",
                "tag=" + label + " class=" + view.getClass().getName());
        return view;
    }

    private int occurrenceFor(String label) {
        if ("ChipBurgers".equals(label)) return 1;
        if ("ChipChicken".equals(label)) return 2;
        if ("ChipDrinks".equals(label)) return 3;
        if ("cartSummary".equals(label)) return 3;
        return 0;
    }

    private View findByClass(View view, Class expected, int occurrence) {
        if (view == null || expected == null) return null;
        int[] seen = new int[] { 0 };
        return findByClassWalk(view, expected, occurrence, seen);
    }

    private View findByClassWalk(View view, Class expected, int occurrence, int[] seen) {
        if (expected.isInstance(view)) {
            if (seen[0] == occurrence) return view;
            seen[0]++;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View found = findByClassWalk(group.getChildAt(i), expected, occurrence, seen);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void setClick(View root, int id, View.OnClickListener listener) {
        View view = root.findViewById(id);
        if (view != null) {
            view.setClickable(true);
            view.setOnClickListener(listener);
        }
    }

    private void probeXmlLayout(View root) {
        String stage = "begin";
        try {
            int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(1013, View.MeasureSpec.EXACTLY);
            stage = "measure";
            root.measure(wSpec, hSpec);
            stage = "layout";
            root.layout(0, 0, 480, 1013);
            if (adapter != null && menuList != null) {
                for (int i = 0; i < 5 && i < adapter.getCount(); i++) {
                    stage = "adapter_" + i;
                    adapter.getView(i, null, menuList);
                }
            }
            McdProfileLog.mark("XML_LAYOUT_PROBE_OK",
                    "target=480x1013 measured=" + root.getMeasuredWidth()
                            + "x" + root.getMeasuredHeight());
        } catch (Throwable t) {
            McdProfileLog.mark("XML_LAYOUT_PROBE_WARN",
                    "stage=" + McdProfileLog.token(stage)
                            + " err=" + McdProfileLog.token(t.getClass().getName())
                            + " msg=" + McdProfileLog.token(t.getMessage()));
            logProbeFrames("XML_LAYOUT_PROBE_FRAME", t);
        }
    }

    private void logProbeFrames(String prefix, Throwable t) {
        try {
            StackTraceElement[] frames = t.getStackTrace();
            for (int i = 0; i < 8 && i < frames.length; i++) {
                StackTraceElement frame = frames[i];
                McdProfileLog.mark(prefix,
                        "i=" + i
                                + " at=" + McdProfileLog.token(frame.getClassName()
                                        + "." + frame.getMethodName()
                                        + ":" + frame.getLineNumber()));
            }
        } catch (Throwable ignored) {
        }
    }

    private void loadCartState() {
        try {
            SharedPreferences prefs = getSharedPreferences("mcd_profile_cart", MODE_PRIVATE);
            cartCount = prefs.getInt("cartCount", 0);
            cartTotalCents = prefs.getInt("cartTotalCents", 0);
            prefs.edit()
                    .putInt("cartCount", cartCount)
                    .putInt("cartTotalCents", cartTotalCents)
                    .putString("lastCategory", category)
                    .apply();
            storageRoundTrip = prefs.getInt("cartCount", -1) == cartCount;
            McdProfileLog.mark("STORAGE_PREFS_OK",
                    "cartCount=" + cartCount + " totalCents=" + cartTotalCents);
        } catch (Throwable t) {
            McdProfileLog.mark("STORAGE_PREFS_WARN",
                    "err=" + McdProfileLog.token(t.getClass().getName()));
        }
    }

    private void saveCartState() {
        try {
            SharedPreferences prefs = getSharedPreferences("mcd_profile_cart", MODE_PRIVATE);
            prefs.edit()
                    .putInt("cartCount", cartCount)
                    .putInt("cartTotalCents", cartTotalCents)
                    .putString("selectedName", selectedName)
                    .apply();
            storageRoundTrip = prefs.getInt("cartTotalCents", -1) == cartTotalCents;
            McdProfileLog.mark("STORAGE_CART_OK",
                    "cartCount=" + cartCount + " totalCents=" + cartTotalCents);
        } catch (Throwable t) {
            McdProfileLog.mark("STORAGE_CART_WARN",
                    "err=" + McdProfileLog.token(t.getClass().getName()));
        }
    }

    private void fetchLiveMenu() {
        if (networkLoading) {
            return;
        }
        networkLoading = true;
        feedStatus = "Loading live menu";
        lastAction = "Network loading";
        renderDirty = true;
        McdProfileLog.mark("NETWORK_FETCH_BEGIN", "url=" + McdProfileLog.token(LIVE_MENU_URL));
        Thread worker = new Thread(new Runnable() {
            public void run() { fetchLiveMenuOnWorker(); }
        }, "westlake-mcd-profile-network");
        worker.start();
    }

    private void fetchLiveMenuOnWorker() {
        try {
            FetchResult feed = bridgeGet(LIVE_MENU_URL, 192 * 1024);
            int parsed = parseRecipes(asciiString(feed.bytes));
            if (parsed <= 0) {
                throw new java.io.IOException("no menu parsed");
            }
            menuCount = parsed;
            updateRows();
            applySelection(0);
            McdProfileLog.mark("LIVE_JSON_OK",
                    "status=" + feed.status + " bytes=" + feed.bytes.length
                            + " items=" + parsed + " transport=host_bridge");
            int limit = parsed > 0 ? 1 : 0;
            for (int i = 0; i < limit; i++) {
                String url = imageUrlAt(i);
                if (url == null || url.length() == 0) {
                    continue;
                }
                FetchResult image = bridgeGet(url, 64 * 1024);
                storeImage(i, image.bytes);
                imageFetchCount++;
                updateRows();
                McdProfileLog.mark("ROW_IMAGE_OK",
                        "index=" + i + " status=" + image.status
                                + " bytes=" + image.bytes.length
                                + " hash=" + intHex(imageHashAt(i))
                                + " transport=host_bridge");
            }
            runRestContractProbe();
            networkLoading = false;
            feedStatus = "Live menu ready";
            heroTitle = "Live menu";
            heroSubtitle = "JSON, images, cart, navigation";
            lastAction = "Live loaded";
            notifyAdapter("live_loaded");
            renderDirty = true;
            McdProfileLog.mark("IMAGE_BRIDGE_OK",
                    "count=" + imageFetchCount + " transport=host_bridge");
            McdProfileLog.mark("READY_FOR_OHOS_PORT_OK",
                    "surface=controlled_mcd_profile xml=true network=true storage=true");
        } catch (Throwable t) {
            networkLoading = false;
            feedStatus = shortMessage(t);
            lastAction = "Network limited";
            renderDirty = true;
            McdProfileLog.mark("NETWORK_FETCH_FAIL",
                    "err=" + McdProfileLog.token(t.getClass().getName())
                            + " msg=" + McdProfileLog.token(shortMessage(t)));
        }
    }

    private FetchResult bridgeGet(String url, int maxBytes) throws java.io.IOException {
        byte[] bytes = WestlakeLauncher.bridgeHttpGetBytes(url, maxBytes, 14000);
        int status = WestlakeLauncher.bridgeHttpLastStatus();
        String error = WestlakeLauncher.bridgeHttpLastError();
        if (bytes == null || status < 200 || status >= 300 || bytes.length == 0) {
            throw new java.io.IOException("bridge status=" + status + " err=" + error);
        }
        McdProfileLog.mark("NETWORK_BRIDGE_OK",
                "status=" + status + " bytes=" + bytes.length
                        + " url=" + McdProfileLog.token(url));
        return new FetchResult(status, bytes);
    }

    private void runRestContractProbe() {
        String base = "http://127.0.0.1:8767/rest";
        try {
            String headers = "{\"X-Westlake-Probe\":\"mcd-profile\",\"Content-Type\":\"application/json\"}";
            WestlakeLauncher.BridgeHttpResponse post = WestlakeLauncher.bridgeHttpRequest(
                    base + "/echo", "POST", headers,
                    utf8("{\"cart\":\"combo\",\"qty\":2}"), 4096, 6000, true);
            if (post == null || post.status != 200 || asciiString(post.body).indexOf("POST") < 0) {
                throw new java.io.IOException("post failed");
            }
            McdProfileLog.mark("REST_POST_OK",
                    "status=" + post.status + " bytes=" + post.body.length
                            + " protocol=2 transport=host_bridge");

            WestlakeLauncher.BridgeHttpResponse head = WestlakeLauncher.bridgeHttpRequest(
                    base + "/ping", "HEAD", "{}", new byte[0], 1024, 6000, true);
            if (head == null || head.status != 200) {
                throw new java.io.IOException("head failed");
            }
            McdProfileLog.mark("REST_HEAD_OK",
                    "status=" + head.status + " bytes=" + head.body.length);

            WestlakeLauncher.BridgeHttpResponse status = WestlakeLauncher.bridgeHttpRequest(
                    base + "/status/418", "GET", "{}", new byte[0], 4096, 6000, true);
            if (status == null || status.status != 418) {
                throw new java.io.IOException("status failed");
            }
            restMatrixPassed = true;
            restStatus = "REST POST/HEAD/status ready";
            McdProfileLog.mark("REST_MATRIX_OK",
                    "post=200 head=200 status=418 transport=host_bridge");
        } catch (Throwable t) {
            restStatus = "REST limited";
            McdProfileLog.mark("REST_MATRIX_WARN",
                    "err=" + McdProfileLog.token(t.getClass().getName())
                            + " msg=" + McdProfileLog.token(shortMessage(t)));
        }
    }

    private int parseRecipes(String json) {
        if (json == null) return 0;
        int count = 0;
        int pos = 0;
        while (count < MAX_ITEMS) {
            int nameAt = json.indexOf("\"name\":\"", pos);
            if (nameAt < 0) break;
            int objStart = json.lastIndexOf('{', nameAt);
            int next = json.indexOf("\"name\":\"", nameAt + 8);
            String obj = next > nameAt ? json.substring(objStart, next) : json.substring(objStart);
            String name = jsonString(obj, "\"name\":\"");
            if (name == null || name.length() == 0) {
                pos = nameAt + 8;
                continue;
            }
            setParsedItem(count, mcdName(name, count),
                    nonEmpty(jsonString(obj, "\"cuisine\":\""), "menu")
                            + " - " + ratingText(jsonTenths(obj, "\"rating\":", 46)),
                    defaultPrice(count),
                    nonEmpty(jsonString(obj, "\"image\":\""), ""));
            count++;
            if (next < 0) break;
            pos = next;
        }
        return count;
    }

    private String mcdName(String source, int index) {
        String base;
        if (index == 0) base = "Signature Burger";
        else if (index == 1) base = "Crispy Chicken";
        else if (index == 2) base = "Breakfast Stack";
        else if (index == 3) base = "Cafe Combo";
        else if (index == 4) base = "Family Box";
        else base = "Menu Item " + (index + 1);
        return base + " - " + trim(source, 20);
    }

    private int defaultPrice(int index) {
        if (index == 0) return 699;
        if (index == 1) return 649;
        if (index == 2) return 529;
        if (index == 3) return 399;
        if (index == 4) return 999;
        if (index == 5) return 349;
        if (index == 6) return 299;
        return 449;
    }

    private void storeImage(int index, byte[] bytes) {
        if (index < 0 || index >= MAX_ITEMS) return;
        byte[] safe = bytes != null ? bytes : new byte[0];
        int hash = hashBytes(safe);
        if (index == 0) {
            row1ImageData = safe; row1ImageBytes = safe.length; row1ImageHash = hash;
        } else if (index == 1) {
            row2ImageData = safe; row2ImageBytes = safe.length; row2ImageHash = hash;
        } else if (index == 2) {
            row3ImageData = safe; row3ImageBytes = safe.length; row3ImageHash = hash;
        } else if (index == 3) {
            row4ImageData = safe; row4ImageBytes = safe.length; row4ImageHash = hash;
        } else if (index == 4) {
            row5ImageData = safe; row5ImageBytes = safe.length; row5ImageHash = hash;
        }
    }

    private void updateRows() {
        if (row1Name == null) row1Name = "Westlake Burger";
        if (row2Name == null) row2Name = "Classic Fries";
        if (row3Name == null) row3Name = "Chicken Stack";
        if (row4Name == null) row4Name = "Iced Coffee";
        if (row5Name == null) row5Name = "Breakfast Muffin";
        row1Meta = appendImageBytes(baseMeta(row1Meta, "combo - 4.7"), row1ImageBytes);
        row2Meta = appendImageBytes(baseMeta(row2Meta, "side - 4.6"), row2ImageBytes);
        row3Meta = appendImageBytes(baseMeta(row3Meta, "sandwich - 4.8"), row3ImageBytes);
        row4Meta = appendImageBytes(baseMeta(row4Meta, "drink - 4.5"), row4ImageBytes);
        row5Meta = appendImageBytes(baseMeta(row5Meta, "breakfast - 4.4"), row5ImageBytes);
        if (row1Price == null) row1Price = "$5.99";
        if (row2Price == null) row2Price = "$2.49";
        if (row3Price == null) row3Price = "$6.49";
        if (row4Price == null) row4Price = "$3.19";
        if (row5Price == null) row5Price = "$4.29";
    }

    private String rowName(int row) {
        if (row == 0) return row1Name;
        if (row == 1) return row2Name;
        if (row == 2) return row3Name;
        if (row == 3) return row4Name;
        if (row == 4) return row5Name;
        return "Menu item";
    }

    private String rowMeta(int row) {
        if (row == 0) return row1Meta;
        if (row == 1) return row2Meta;
        if (row == 2) return row3Meta;
        if (row == 3) return row4Meta;
        if (row == 4) return row5Meta;
        return "menu - image 0 bytes";
    }

    private void applySelection(int index) {
        if (index < 0) index = 0;
        if (index >= 5) index = 4;
        selectedIndex = index;
        selectedName = rowName(index);
        selectedMeta = rowMeta(index);
        selectedPriceCents = priceCentsAt(index);
        selectedPrice = money(selectedPriceCents);
        lastAction = "Selected " + selectedName;
        renderDirty = true;
        updateUi("select");
    }

    private void setParsedItem(int index, String name, String meta, int cents, String imageUrl) {
        String price = money(cents);
        if (index == 0) {
            row1Name = name; row1Meta = meta; row1Price = price; row1ImageUrl = imageUrl;
        } else if (index == 1) {
            row2Name = name; row2Meta = meta; row2Price = price; row2ImageUrl = imageUrl;
        } else if (index == 2) {
            row3Name = name; row3Meta = meta; row3Price = price; row3ImageUrl = imageUrl;
        } else if (index == 3) {
            row4Name = name; row4Meta = meta; row4Price = price; row4ImageUrl = imageUrl;
        } else if (index == 4) {
            row5Name = name; row5Meta = meta; row5Price = price; row5ImageUrl = imageUrl;
        }
    }

    private String imageUrlAt(int index) {
        if (index == 0) return row1ImageUrl;
        if (index == 1) return row2ImageUrl;
        if (index == 2) return row3ImageUrl;
        if (index == 3) return row4ImageUrl;
        if (index == 4) return row5ImageUrl;
        return "";
    }

    private int imageHashAt(int index) {
        if (index == 0) return row1ImageHash;
        if (index == 1) return row2ImageHash;
        if (index == 2) return row3ImageHash;
        if (index == 3) return row4ImageHash;
        if (index == 4) return row5ImageHash;
        return 0;
    }

    private int imageBytesAt(int index) {
        if (index == 0) return row1ImageBytes;
        if (index == 1) return row2ImageBytes;
        if (index == 2) return row3ImageBytes;
        if (index == 3) return row4ImageBytes;
        if (index == 4) return row5ImageBytes;
        return 0;
    }

    private int priceCentsAt(int index) {
        if (index == 0) return centsFromPrice(row1Price, 599);
        if (index == 1) return centsFromPrice(row2Price, 249);
        if (index == 2) return centsFromPrice(row3Price, 649);
        if (index == 3) return centsFromPrice(row4Price, 319);
        if (index == 4) return centsFromPrice(row5Price, 429);
        return 599;
    }

    private int centsFromPrice(String price, int fallback) {
        if (price == null || price.length() == 0) return fallback;
        int dollars = 0;
        int cents = 0;
        boolean afterDot = false;
        int centDigits = 0;
        for (int i = 0; i < price.length(); i++) {
            char c = price.charAt(i);
            if (c == '.') {
                afterDot = true;
            } else if (c >= '0' && c <= '9') {
                if (afterDot) {
                    if (centDigits < 2) {
                        cents = cents * 10 + (c - '0');
                        centDigits++;
                    }
                } else {
                    dollars = dollars * 10 + (c - '0');
                }
            }
        }
        if (centDigits == 1) cents *= 10;
        int value = dollars * 100 + cents;
        return value > 0 ? value : fallback;
    }

    private String baseMeta(String value, String fallback) {
        if (value == null || value.length() == 0) return fallback;
        int imageAt = value.indexOf(" - image ");
        return imageAt >= 0 ? value.substring(0, imageAt) : value;
    }

    private String appendImageBytes(String meta, int bytes) {
        return meta + " - image " + bytes + " bytes";
    }

    private void updateUi(String reason) {
        if (heroText != null) {
            heroText.setText(heroTitle + "\n" + heroSubtitle + "\n" + feedStatus);
        }
        if (cartSummary != null) {
            cartSummary.setText("Cart " + cartCount + " - " + money(cartTotalCents));
        }
        notifyAdapter(reason);
    }

    private void notifyAdapter(String reason) {
        try {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                McdProfileLog.mark("ADAPTER_NOTIFY_OK",
                        "reason=" + McdProfileLog.token(reason)
                                + " count=" + adapter.getCount()
                                + " getView=" + adapterGetViewCount);
            }
        } catch (Throwable t) {
            McdProfileLog.mark("ADAPTER_NOTIFY_WARN",
                    "err=" + McdProfileLog.token(t.getClass().getName()));
        }
    }

    public void selectMenuPosition(int position) {
        activeTab = TAB_MENU;
        applySelection(menuOffset + position);
        McdProfileLog.mark("SELECT_ITEM_OK",
                "position=" + position + " name=" + McdProfileLog.token(selectedName));
    }

    public void selectMenu0() { selectMenuPosition(0); }
    public void selectMenu1() { selectMenuPosition(1); }
    public void selectMenu2() { selectMenuPosition(2); }
    public void selectMenu3() { selectMenuPosition(3); }
    public void selectMenu4() { selectMenuPosition(4); }

    public void addSelectedToCart() {
        cartCount++;
        cartTotalCents += selectedPriceCents;
        checkedOut = false;
        activeTab = TAB_CART;
        lastAction = "Added " + selectedName;
        saveCartState();
        updateUi("cart_add");
        renderDirty = true;
        McdProfileLog.mark("CART_ADD_OK",
                "count=" + cartCount + " totalCents=" + cartTotalCents
                        + " item=" + McdProfileLog.token(selectedName));
    }

    public void checkout() {
        checkedOut = true;
        activeTab = TAB_CART;
        lastAction = "Checkout staged";
        McdProfileLog.mark("CHECKOUT_OK",
                "count=" + cartCount + " totalCents=" + cartTotalCents
                        + " storage=" + storageRoundTrip);
        renderDirty = true;
    }

    public void navigateHome() { activeTab = TAB_HOME; markAction("NAV_HOME_OK", "tab=home"); }
    public void navigateMenu() { activeTab = TAB_MENU; markAction("NAV_MENU_OK", "tab=menu"); }
    public void navigateCart() { activeTab = TAB_CART; markAction("NAV_CART_OK", "count=" + cartCount); }
    public void navigateDeals() { activeTab = TAB_DEALS; markAction("NAV_DEALS_OK", "network=" + imageFetchCount); }

    public void selectBreakfast() { category = "Breakfast"; menuOffset = 0; updateRows(); markAction("CATEGORY_OK", "name=Breakfast"); }
    public void selectBurgers() { category = "Burgers"; menuOffset = 0; updateRows(); markAction("CATEGORY_OK", "name=Burgers"); }
    public void selectChicken() { category = "Chicken"; menuOffset = 1; updateRows(); markAction("CATEGORY_OK", "name=Chicken"); }
    public void selectDrinks() { category = "Drinks"; menuOffset = 3; updateRows(); markAction("CATEGORY_OK", "name=Drinks"); }

    public void scrollMenuDown() {
        if (menuOffset + 5 < menuCount) {
            menuOffset++;
        }
        updateRows();
        markAction("LIST_SCROLL_OK", "direction=down offset=" + menuOffset);
    }

    public void scrollMenuUp() {
        if (menuOffset > 0) {
            menuOffset--;
        }
        updateRows();
        markAction("LIST_SCROLL_OK", "direction=up offset=" + menuOffset);
    }

    public void refreshMenu() {
        fetchLiveMenu();
        markAction("REFRESH_OK", "network=true");
    }

    public boolean consumeRenderDirty() {
        boolean dirty = renderDirty;
        renderDirty = false;
        return dirty;
    }

    private void markAction(String name, String detail) {
        lastAction = name;
        renderDirty = true;
        updateUi(name);
        McdProfileLog.mark(name, detail);
    }

    private int countMaterialViews(View view) {
        if (view == null) return 0;
        int count = view.getClass().getName().startsWith("com.google.android.material.") ? 1 : 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countMaterialViews(group.getChildAt(i));
            }
        }
        return count;
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

    private String className(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }

    private String jsonString(String obj, String key) {
        int start = obj.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        StringBuilder out = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < obj.length(); i++) {
            char c = obj.charAt(i);
            if (escape) {
                out.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                return out.toString();
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private int jsonTenths(String obj, String key, int fallback) {
        int start = obj.indexOf(key);
        if (start < 0) return fallback;
        start += key.length();
        int end = start;
        while (end < obj.length()) {
            char c = obj.charAt(end);
            if ((c < '0' || c > '9') && c != '.') break;
            end++;
        }
        try {
            float value = Float.parseFloat(obj.substring(start, end));
            return (int) (value * 10f + 0.5f);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private String ratingText(int tenths) {
        return (tenths / 10) + "." + (tenths % 10);
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.length() == 0 ? fallback : value;
    }

    private String asciiString(byte[] bytes) {
        if (bytes == null) return "";
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xff;
            chars[i] = (char) (b < 128 ? b : '_');
        }
        return new String(chars);
    }

    private byte[] utf8(String value) {
        if (value == null || value.length() == 0) {
            return new byte[0];
        }
        byte[] out = new byte[value.length() * 4];
        int offset = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < 0x80) {
                out[offset++] = (byte) c;
            } else if (c < 0x800) {
                out[offset++] = (byte) (0xc0 | (c >> 6));
                out[offset++] = (byte) (0x80 | (c & 0x3f));
            } else if (c >= 0xd800 && c <= 0xdbff && i + 1 < value.length()) {
                char low = value.charAt(i + 1);
                if (low >= 0xdc00 && low <= 0xdfff) {
                    int codePoint = 0x10000 + (((c - 0xd800) << 10) | (low - 0xdc00));
                    out[offset++] = (byte) (0xf0 | (codePoint >> 18));
                    out[offset++] = (byte) (0x80 | ((codePoint >> 12) & 0x3f));
                    out[offset++] = (byte) (0x80 | ((codePoint >> 6) & 0x3f));
                    out[offset++] = (byte) (0x80 | (codePoint & 0x3f));
                    i++;
                } else {
                    out[offset++] = '?';
                }
            } else {
                out[offset++] = (byte) (0xe0 | (c >> 12));
                out[offset++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                out[offset++] = (byte) (0x80 | (c & 0x3f));
            }
        }
        byte[] exact = new byte[offset];
        System.arraycopy(out, 0, exact, 0, offset);
        return exact;
    }

    private int hashBytes(byte[] bytes) {
        int h = 0x811c9dc5;
        if (bytes != null) {
            for (int i = 0; i < bytes.length; i++) {
                h ^= (bytes[i] & 0xff);
                h *= 0x01000193;
            }
        }
        return h;
    }

    private String intHex(int value) {
        char[] out = new char[8];
        for (int i = 7; i >= 0; i--) {
            int n = value & 0xf;
            out[i] = (char) (n < 10 ? ('0' + n) : ('a' + n - 10));
            value >>>= 4;
        }
        return new String(out);
    }

    private String money(int cents) {
        int dollars = cents / 100;
        int rem = cents < 0 ? -cents % 100 : cents % 100;
        return "$" + dollars + "." + (rem < 10 ? "0" : "") + rem;
    }

    private String trim(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max);
    }

    private String shortMessage(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null || msg.length() == 0) return t.getClass().getName();
        return msg.length() > 80 ? msg.substring(0, 80) : msg;
    }

    private final class MenuAdapter extends BaseAdapter {
        public int getCount() { return menuCount < 5 ? menuCount : 5; }
        public Object getItem(int position) { return rowName(position); }
        public long getItemId(int position) { return position; }

        public View getView(int position, View convertView, ViewGroup parent) {
            adapterGetViewCount++;
            LinearLayout row = new LinearLayout(McdProfileActivity.this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8, 8, 8, 8);

            ImageView image = new ImageView(McdProfileActivity.this);
            image.setBackgroundColor(0xffffe08a);
            row.addView(image, new LinearLayout.LayoutParams(72, 72));

            LinearLayout text = new LinearLayout(McdProfileActivity.this);
            text.setOrientation(LinearLayout.VERTICAL);
            TextView title = new TextView(McdProfileActivity.this);
            title.setText(rowName(position));
            title.setTextSize(15);
            title.setTextColor(0xff27251f);
            TextView meta = new TextView(McdProfileActivity.this);
            meta.setText(rowMeta(position));
            meta.setTextSize(11);
            meta.setTextColor(0xff6f665f);
            TextView price = new TextView(McdProfileActivity.this);
            price.setText(money(priceCentsAt(position)));
            price.setTextSize(12);
            price.setTextColor(0xffa82020);
            text.addView(title);
            text.addView(meta);
            text.addView(price);
            row.addView(text, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            McdProfileLog.mark("ADAPTER_GET_VIEW_OK",
                    "position=" + position + " imageBytes=" + imageBytesAt(position));
            return row;
        }
    }

    private static final class FetchResult {
        final int status;
        final byte[] bytes;

        FetchResult(int status, byte[] bytes) {
            this.status = status;
            this.bytes = bytes != null ? bytes : new byte[0];
        }
    }
}
