package com.westlake.materialyelp;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.westlake.engine.WestlakeLauncher;

public final class MaterialYelpActivity extends Activity {
    private static final int TAB_DISCOVER = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_DETAILS = 2;
    private static final int TAB_SAVED = 3;

    public volatile boolean renderDirty;
    public volatile boolean sortByRating = true;
    public volatile boolean deliveryFilter = true;
    public volatile boolean openNowFilter = true;
    public volatile boolean saved;
    public volatile boolean chineseMode = true;
    public volatile boolean imageLoading;
    public volatile int activeTab = TAB_SEARCH;
    public volatile int selectedIndex = 2;
    public volatile int savedCount;
    public volatile int ratingTenths = 47;
    public volatile int reviewCount = 186;
    public volatile int materialClassCount;
    public volatile int materialViewCount;
    public volatile int cardCount;
    public volatile int chipCount;
    public volatile int buttonCount;
    public volatile int sliderValue = 72;
    public volatile int imageFetchCount;
    public volatile String selectedCuisine = "中餐";
    public volatile String selectedPlace = "西湖川菜";
    public volatile String selectedMeta = "中餐 - 晚餐 - 4.7 - 外卖";
    public volatile String lastAction = "Material 金丝雀就绪";
    public volatile String searchText = "附近中餐";
    public volatile String row1Name = "拉面小馆";
    public volatile String row1Meta = "日料 - 晚餐 - 4.9 - 外卖";
    public volatile String row2Name = "咖喱鸡餐厅";
    public volatile String row2Meta = "南亚菜 - 午餐 - 4.8 - 外卖";
    public volatile String row3Name = "西湖川菜";
    public volatile String row3Meta = "中餐 - 晚餐 - 4.7 - 外卖";
    public volatile String row4Name = "烤肉串烧";
    public volatile String row4Meta = "烧烤 - 夜宵 - 4.7 - 到店";
    public volatile byte[] heroImageData = new byte[0];
    public volatile byte[] row1ImageData = new byte[0];
    public volatile byte[] row2ImageData = new byte[0];
    public volatile byte[] row3ImageData = new byte[0];
    public volatile byte[] row4ImageData = new byte[0];
    public volatile int heroImageHash;
    public volatile int row1ImageHash;
    public volatile int row2ImageHash;
    public volatile int row3ImageHash;
    public volatile int row4ImageHash;
    public volatile int heroImageBytes;
    public volatile int row1ImageBytes;
    public volatile int row2ImageBytes;
    public volatile int row3ImageBytes;
    public volatile int row4ImageBytes;

    private MaterialCardView[] cards;
    private Chip topRatedChip;
    private Chip deliveryChip;
    private Slider distanceSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MaterialYelpLog.mark("ACTIVITY_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        super.onCreate(savedInstanceState);
        MaterialYelpLog.mark("ACTIVITY_ON_CREATE_OK", "activity=" + getClass().getName());
        buildMaterialUi();
        fetchMaterialImages();
    }

    private void buildMaterialUi() {
        try {
            ScrollView scroll = new ScrollView(this);
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(18, 18, 18, 18);
            scroll.addView(root, new ScrollView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            AppBarLayout appBar = new AppBarLayout(this);
            appBar.setExpanded(true, false);
            root.addView(appBar, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView title = new TextView(this);
            title.setText("美食点评 Material");
            title.setTextSize(24);
            title.setTextColor(0xffd32323);
            appBar.addView(title);

            TextInputLayout searchBox = new TextInputLayout(this);
            searchBox.setHint("搜索");
            searchBox.setHelperText("TextInputLayout + TextInputEditText + 中文");
            searchBox.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            searchBox.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            searchBox.setBoxBackgroundColor(0xffffffff);
            TextInputEditText edit = new TextInputEditText(this);
            edit.setText(searchText);
            edit.setSingleLine(true);
            searchBox.addView(edit, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            root.addView(searchBox);

            ChipGroup filters = new ChipGroup(this);
            filters.setSingleSelection(false);
            filters.setChipSpacing(8);
            topRatedChip = chip("好评", true);
            Chip openChip = chip("营业中", true);
            deliveryChip = chip("外卖", true);
            Chip priceChip = chip("人均 $$", false);
            filters.addView(topRatedChip);
            filters.addView(openChip);
            filters.addView(deliveryChip);
            filters.addView(priceChip);
            root.addView(filters);

            distanceSlider = new Slider(this);
            distanceSlider.setValueFrom(1f);
            distanceSlider.setValueTo(100f);
            distanceSlider.setValue(sliderValue);
            distanceSlider.addOnChangeListener(new Slider.OnChangeListener() {
                public void onValueChange(Slider slider, float value, boolean fromUser) {
                    sliderValue = (int) value;
                    markAction("SLIDER_CHANGE_OK", "value=" + sliderValue);
                }
            });
            root.addView(distanceSlider, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            cards = new MaterialCardView[] {
                    card(row1Name, row1Meta, 0),
                    card(row2Name, row2Meta, 1),
                    card(row3Name, row3Meta, 2),
                    card(row4Name, row4Meta, 3)
            };
            for (int i = 0; i < cards.length; i++) {
                root.addView(cards[i], new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            MaterialButton details = button("详情");
            details.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { navigateDetails(); }
            });
            MaterialButton save = button("收藏");
            save.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { savePlace(); }
            });
            actions.addView(details);
            actions.addView(save);
            root.addView(actions);

            BottomNavigationView nav = new BottomNavigationView(this);
            nav.setItemActiveIndicatorEnabled(true);
            nav.setLabelVisibilityMode(1);
            TextView navText = new TextView(this);
            navText.setText("发现     搜索     详情     收藏");
            navText.setTextSize(14);
            navText.setTextColor(0xff5f6368);
            nav.addView(navText);
            root.addView(nav, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 92));

            FloatingActionButton fab = new FloatingActionButton(this);
            fab.setSize(FloatingActionButton.SIZE_MINI);
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { savePlace(); }
            });
            FrameLayout frame = new FrameLayout(this);
            frame.addView(scroll);
            FrameLayout.LayoutParams fabParams = new FrameLayout.LayoutParams(96, 96);
            fabParams.leftMargin = 350;
            fabParams.topMargin = 586;
            frame.addView(fab, fabParams);

            setContentView(frame);

            materialClassCount = 9;
            materialViewCount = 1 + 1 + 1 + 1 + 4 + 4 + 2 + 1 + 1;
            chipCount = 4;
            buttonCount = 2;
            cardCount = cards.length;
            markClassSurface();
            MaterialYelpLog.mark("UI_BUILD_OK",
                    "surface=programmatic components=MaterialCardView,MaterialButton,ChipGroup,TextInputLayout,Slider,BottomNavigationView,FAB"
                            + " materialViews=" + materialViewCount);
            MaterialYelpLog.mark("LANGUAGE_OK", "locale=zh-Hans text=utf8");
            renderDirty = true;
        } catch (Throwable t) {
            MaterialYelpLog.mark("UI_BUILD_FAIL",
                    "err=" + MaterialYelpLog.token(t.getClass().getName())
                            + " msg=" + MaterialYelpLog.token(t.getMessage()));
            throw t;
        }
    }

    private Chip chip(String label, boolean checked) {
        Chip chip = new Chip(this);
        chip.setText(label);
        chip.setCheckable(true);
        chip.setChecked(checked);
        chip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Chip c = (Chip) view;
                c.toggle();
                String text = String.valueOf(c.getText());
                if (text.indexOf("Top") >= 0 || text.indexOf("好评") >= 0) {
                    sortByRating = c.isChecked();
                    markAction("FILTER_TOGGLE_OK", "name=top_rated enabled=" + sortByRating);
                } else if (text.indexOf("Delivery") >= 0 || text.indexOf("外卖") >= 0) {
                    deliveryFilter = c.isChecked();
                    markAction("FILTER_TOGGLE_OK", "name=delivery enabled=" + deliveryFilter);
                } else if (text.indexOf("Open") >= 0 || text.indexOf("营业") >= 0) {
                    openNowFilter = c.isChecked();
                    markAction("FILTER_TOGGLE_OK", "name=open_now enabled=" + openNowFilter);
                } else {
                    markAction("FILTER_TOGGLE_OK", "name=price enabled=" + c.isChecked());
                }
            }
        });
        return chip;
    }

    private MaterialButton button(String label) {
        MaterialButton button = new MaterialButton(this);
        button.setText(label);
        button.setCornerRadius(18);
        button.setBackgroundTintList(ColorStateList.valueOf(0xffd32323));
        return button;
    }

    private MaterialCardView card(String title, String meta, int index) {
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(18f);
        card.setCardElevation(4f);
        card.setStrokeWidth(index == selectedIndex ? 3 : 1);
        card.setStrokeColor(index == selectedIndex ? 0xffd32323 : 0xffe5dfe1);
        card.setChecked(index == selectedIndex);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(0xff1f1f1f);
        TextView metaView = new TextView(this);
        metaView.setText(meta);
        metaView.setTextSize(12);
        metaView.setTextColor(0xff6f6f6f);
        body.addView(titleView);
        body.addView(metaView);
        card.addView(body);
        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectPlace(index);
            }
        });
        return card;
    }

    private void markClassSurface() {
        String names = MaterialCardView.class.getName()
                + "," + MaterialButton.class.getName()
                + "," + ChipGroup.class.getName()
                + "," + Chip.class.getName()
                + "," + TextInputLayout.class.getName()
                + "," + TextInputEditText.class.getName()
                + "," + Slider.class.getName()
                + "," + BottomNavigationView.class.getName()
                + "," + FloatingActionButton.class.getName();
        MaterialYelpLog.mark("CLASS_SURFACE_OK",
                "count=" + materialClassCount + " classes=" + MaterialYelpLog.token(names));
    }

    private void selectPlace(int index) {
        selectedIndex = index;
        if (index == 0) {
            selectedPlace = row1Name;
            selectedMeta = row1Meta;
            ratingTenths = 49;
            reviewCount = 214;
        } else if (index == 1) {
            selectedPlace = row2Name;
            selectedMeta = row2Meta;
            ratingTenths = 48;
            reviewCount = 156;
        } else if (index == 2) {
            selectedPlace = row3Name;
            selectedMeta = row3Meta;
            ratingTenths = 47;
            reviewCount = 186;
        } else {
            selectedPlace = row4Name;
            selectedMeta = row4Meta;
            ratingTenths = 47;
            reviewCount = 119;
        }
        selectedCuisine = selectedMeta.indexOf("日料") >= 0 ? "日料"
                : selectedMeta.indexOf("南亚") >= 0 ? "南亚菜"
                : selectedMeta.indexOf("烧烤") >= 0 ? "烧烤" : "中餐";
        heroImageData = rowImageData(index);
        heroImageHash = rowImageHash(index);
        heroImageBytes = rowImageBytes(index);
        updateCardChecks();
        activeTab = TAB_DETAILS;
        markAction("SELECT_PLACE_OK",
                "index=" + selectedIndex + " name=" + MaterialYelpLog.token(selectedPlace));
    }

    private void updateCardChecks() {
        if (cards == null) {
            return;
        }
        for (int i = 0; i < cards.length; i++) {
            cards[i].setChecked(i == selectedIndex);
            cards[i].setStrokeWidth(i == selectedIndex ? 3 : 1);
        }
    }

    public void toggleSortRating() {
        sortByRating = !sortByRating;
        if (topRatedChip != null) {
            topRatedChip.setChecked(sortByRating);
        }
        markAction("FILTER_TOGGLE_OK", "name=top_rated enabled=" + sortByRating);
    }

    public void toggleDelivery() {
        deliveryFilter = !deliveryFilter;
        if (deliveryChip != null) {
            deliveryChip.setChecked(deliveryFilter);
        }
        markAction("FILTER_TOGGLE_OK", "name=delivery enabled=" + deliveryFilter);
    }

    public void selectAsian() {
        selectedCuisine = "中餐";
        searchText = "附近中餐";
        markAction("CATEGORY_SELECT_OK", "category=Chinese");
    }

    public void selectPizza() {
        selectedCuisine = "披萨";
        searchText = "附近披萨";
        markAction("CATEGORY_SELECT_OK", "category=Pizza");
    }

    public void selectPlace0() {
        selectPlace(0);
    }

    public void selectPlace1() {
        selectPlace(1);
    }

    public void selectPlace2() {
        selectPlace(2);
    }

    public void selectPlace3() {
        selectPlace(3);
    }

    public void navigateDiscover() {
        activeTab = TAB_DISCOVER;
        markAction("NAV_DISCOVER_OK", "tab=Discover");
    }

    public void navigateSearch() {
        activeTab = TAB_SEARCH;
        markAction("NAV_SEARCH_OK", "query=" + MaterialYelpLog.token(searchText));
    }

    public void navigateDetails() {
        activeTab = TAB_DETAILS;
        markAction("DETAILS_OPEN_OK", "name=" + MaterialYelpLog.token(selectedPlace));
    }

    public void navigateSaved() {
        activeTab = TAB_SAVED;
        markAction("NAV_SAVED_OK", "saved=" + savedCount);
    }

    public void savePlace() {
        saved = true;
        savedCount = 1;
        activeTab = TAB_SAVED;
        markAction("SAVE_PLACE_OK", "name=" + MaterialYelpLog.token(selectedPlace));
        MaterialYelpLog.mark("NAV_SAVED_OK", "saved=" + savedCount);
    }

    private void fetchMaterialImages() {
        imageLoading = true;
        lastAction = "图片加载中";
        renderDirty = true;
        MaterialYelpLog.mark("IMAGE_FETCH_BEGIN", "count=4 source=dummyjson-image transport=host_bridge");
        try {
            MaterialYelpLog.mark("IMAGE_FETCH_SYNC_ENTER", "thread=main");
            fetchMaterialImagesOnWorker();
            MaterialYelpLog.mark("IMAGE_FETCH_SYNC_RETURN", "count=" + imageFetchCount);
        } catch (Throwable t) {
            imageLoading = false;
            lastAction = "图片加载受限";
            renderDirty = true;
            MaterialYelpLog.mark("IMAGE_FETCH_SYNC_WARN",
                    "err=" + MaterialYelpLog.token(t.getClass().getName()));
        }
    }

    private void fetchMaterialImagesOnWorker() {
        MaterialYelpLog.mark("IMAGE_WORKER_ENTER", "mode=sync");
        try {
            for (int i = 0; i < 4; i++) {
                String url = primaryImageUrlFor(i);
                MaterialYelpLog.mark("IMAGE_REQUEST_BEGIN",
                        "index=" + i + " url=" + MaterialYelpLog.token(url));
                FetchResult image;
                try {
                    image = bridgeGet(url, 160 * 1024);
                } catch (Throwable primary) {
                    String fallbackUrl = fallbackImageUrlFor(i);
                    MaterialYelpLog.mark("IMAGE_PRIMARY_WARN",
                            "index=" + i
                                    + " err=" + MaterialYelpLog.token(primary.getClass().getName())
                                    + " fallback=" + MaterialYelpLog.token(fallbackUrl));
                    image = bridgeGet(fallbackUrl, 160 * 1024);
                }
                MaterialYelpLog.mark("IMAGE_REQUEST_RETURN",
                        "index=" + i + " bytes=" + image.bytes.length);
                storeRowImage(i, image.bytes);
                imageFetchCount++;
                MaterialYelpLog.mark("ROW_IMAGE_OK",
                        "index=" + i
                                + " status=" + image.status
                                + " bytes=" + image.bytes.length
                                + " hash=" + intHex(rowImageHash(i))
                                + " transport=host_bridge");
                renderDirty = true;
            }
            heroImageData = rowImageData(selectedIndex);
            heroImageHash = rowImageHash(selectedIndex);
            heroImageBytes = rowImageBytes(selectedIndex);
            imageLoading = false;
            lastAction = "图片已加载";
            renderDirty = true;
            MaterialYelpLog.mark("IMAGE_BRIDGE_OK",
                    "count=" + imageFetchCount
                            + " selectedBytes=" + heroImageBytes
                            + " transport=host_bridge");
        } catch (Throwable t) {
            imageLoading = false;
            lastAction = "图片加载受限";
            renderDirty = true;
            MaterialYelpLog.mark("IMAGE_BRIDGE_WARN",
                    "err=" + MaterialYelpLog.token(t.getClass().getName())
                            + " msg=" + MaterialYelpLog.token(shortMessage(t)));
        }
    }

    private String primaryImageUrlFor(int index) {
        return fallbackImageUrlFor(index);
    }

    private String fallbackImageUrlFor(int index) {
        if (index == 0) {
            return "https://dummyjson.com/image/160x104/d32323/ffffff?text=Ramen";
        }
        if (index == 1) {
            return "https://dummyjson.com/image/160x104/f59e0b/ffffff?text=Curry";
        }
        if (index == 2) {
            return "https://dummyjson.com/image/160x104/16a34a/ffffff?text=Sichuan";
        }
        return "https://dummyjson.com/image/160x104/7c3aed/ffffff?text=BBQ";
    }

    private FetchResult bridgeGet(String url, int maxBytes) throws java.io.IOException {
        byte[] bytes = WestlakeLauncher.bridgeHttpGetBytes(url, maxBytes, 14000);
        int statusCode = WestlakeLauncher.bridgeHttpLastStatus();
        String error = WestlakeLauncher.bridgeHttpLastError();
        if (bytes == null || statusCode < 200 || statusCode >= 300 || bytes.length == 0) {
            throw new java.io.IOException("bridge status=" + statusCode + " err=" + error);
        }
        MaterialYelpLog.mark("NETWORK_BRIDGE_OK",
                "status=" + statusCode
                        + " bytes=" + bytes.length
                        + " url=" + MaterialYelpLog.token(url));
        return new FetchResult(statusCode, url, bytes);
    }

    private void storeRowImage(int index, byte[] bytes) {
        byte[] safe = bytes != null ? bytes : new byte[0];
        int hash = hashBytes(safe);
        if (index == 0) {
            row1ImageData = safe;
            row1ImageHash = hash;
            row1ImageBytes = safe.length;
        } else if (index == 1) {
            row2ImageData = safe;
            row2ImageHash = hash;
            row2ImageBytes = safe.length;
        } else if (index == 2) {
            row3ImageData = safe;
            row3ImageHash = hash;
            row3ImageBytes = safe.length;
        } else if (index == 3) {
            row4ImageData = safe;
            row4ImageHash = hash;
            row4ImageBytes = safe.length;
        }
        if (index == selectedIndex) {
            heroImageData = safe;
            heroImageHash = hash;
            heroImageBytes = safe.length;
        }
    }

    private byte[] rowImageData(int index) {
        if (index == 0) return row1ImageData;
        if (index == 1) return row2ImageData;
        if (index == 2) return row3ImageData;
        if (index == 3) return row4ImageData;
        return new byte[0];
    }

    private int rowImageHash(int index) {
        if (index == 0) return row1ImageHash;
        if (index == 1) return row2ImageHash;
        if (index == 2) return row3ImageHash;
        if (index == 3) return row4ImageHash;
        return 0;
    }

    private int rowImageBytes(int index) {
        if (index == 0) return row1ImageBytes;
        if (index == 1) return row2ImageBytes;
        if (index == 2) return row3ImageBytes;
        if (index == 3) return row4ImageBytes;
        return 0;
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

    private String shortMessage(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null || msg.length() == 0) return t.getClass().getName();
        if (msg.length() > 80) return msg.substring(0, 80);
        return msg;
    }

    public void boostSlider() {
        sliderValue = sliderValue >= 90 ? 55 : sliderValue + 12;
        if (distanceSlider != null) {
            distanceSlider.setValue(sliderValue);
        }
        markAction("SLIDER_CHANGE_OK", "value=" + sliderValue);
    }

    public boolean consumeRenderDirty() {
        boolean dirty = renderDirty;
        renderDirty = false;
        return dirty;
    }

    private void markAction(String name, String detail) {
        lastAction = name;
        renderDirty = true;
        MaterialYelpLog.mark(name, detail);
    }

    private static final class FetchResult {
        final int status;
        final String url;
        final byte[] bytes;

        FetchResult(int status, String url, byte[] bytes) {
            this.status = status;
            this.url = url;
            this.bytes = bytes != null ? bytes : new byte[0];
        }
    }
}
