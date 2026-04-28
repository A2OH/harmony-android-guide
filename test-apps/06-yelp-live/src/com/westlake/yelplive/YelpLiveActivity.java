package com.westlake.yelplive;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.westlake.engine.WestlakeLauncher;

public final class YelpLiveActivity extends Activity {
    private static final String LIVE_FEED_URL =
            "https://dummyjson.com/recipes?limit=8&select=name,image,rating,reviewCount,mealType,cuisine,difficulty";
    private static final String SELECT_FIELDS =
            "select=name,image,rating,reviewCount,mealType,cuisine,difficulty";
    private static final int MAX_PLACES = 8;
    private static final int TAB_DISCOVER = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_DETAILS = 2;
    private static final int TAB_SAVED = 3;

    private volatile boolean renderDirty;
    private volatile boolean networkLoading;
    private volatile boolean saved;
    private volatile boolean openNowFilter = true;
    private volatile boolean deliveryFilter;
    private volatile boolean priceFilter;
    private volatile boolean sortByRating;
    private volatile int activeTab = TAB_DISCOVER;
    private volatile int queryMode;
    private volatile int placeCount;
    private volatile int placeIndex;
    private volatile int listOffset;
    private volatile int ratingTenths = 46;
    private volatile int reviewCount;
    private volatile int savedCount;
    private volatile int imageBytes;
    private volatile int imageWidth;
    private volatile int imageHeight;
    private volatile int imageHash;
    private volatile byte[] liveImageData = new byte[0];
    private volatile String placeName = "Tap Live feed";
    private volatile String cuisine = "Live internet data";
    private volatile String mealType = "Nearby";
    private volatile String difficulty = "Open";
    private volatile String imageUrl = "";
    private volatile String status = "No live data yet";
    private volatile String query = "pizza";
    private volatile String lastAction = "Ready";
    private volatile String category = "Restaurants";
    private volatile String detailAction = "Tap a result";
    private volatile String row1Name = "Loading nearby restaurants";
    private volatile String row1Meta = "Live internet feed";
    private volatile String row2Name = "Pizza, dinner, takeout";
    private volatile String row2Meta = "Tap anywhere to explore";
    private volatile String row3Name = "Open now near Westlake";
    private volatile String row3Meta = "Host bridge network";
    private volatile String row4Name = "Top rated nearby";
    private volatile String row4Meta = "Tap a row for details";
    private volatile String row5Name = "More places nearby";
    private volatile String row5Meta = "Swipe the list to scroll";
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

    private final java.util.ArrayList<String> names = new java.util.ArrayList<String>(MAX_PLACES);
    private final java.util.ArrayList<String> cuisines = new java.util.ArrayList<String>(MAX_PLACES);
    private final java.util.ArrayList<String> mealTypes = new java.util.ArrayList<String>(MAX_PLACES);
    private final java.util.ArrayList<String> difficulties = new java.util.ArrayList<String>(MAX_PLACES);
    private final java.util.ArrayList<String> imageUrls = new java.util.ArrayList<String>(MAX_PLACES);
    private final int[] ratings = new int[MAX_PLACES];
    private final int[] reviews = new int[MAX_PLACES];
    private final byte[][] placeImages = new byte[MAX_PLACES][];
    private final int[] placeImageHashes = new int[MAX_PLACES];
    private final int[] placeImageBytes = new int[MAX_PLACES];

    private TextView titleView;
    private TextView statusView;
    private TextView cardView;
    private TextView listView;
    private Button loadButton;
    private Button nextButton;
    private Button searchButton;
    private Button detailsButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        YelpLiveLog.mark("ACTIVITY_ON_CREATE_ENTER", "saved=" + (savedInstanceState != null));
        super.onCreate(savedInstanceState);
        YelpLiveLog.mark("ACTIVITY_ON_CREATE_OK", "activity=" + getClass().getName());
        buildUi();
        updateUi("Ready");
        fetchLiveFeed();
    }

    private void buildUi() {
        YelpLiveLog.mark("XML_INFLATE_BEGIN", "layout=yelp_live_activity");
        View root = LayoutInflater.from(this).inflate(R.layout.yelp_live_activity, null);
        int viewCount = countViews(root);
        int textCount = countTextViews(root);
        YelpLiveLog.mark("XML_INFLATE_OK",
                "root=" + className(root) + " views=" + viewCount + " texts=" + textCount);

        setContentView(root);
        bindXmlViews(root);
        probeXmlLayout(root);
        YelpLiveLog.mark("UI_BUILD_OK",
                "surface=xml tabs=4 network=host_bridge views=" + viewCount
                        + " texts=" + textCount);
    }

    private void bindXmlViews(View root) {
        titleView = text(root, R.id.yelp_title, "yelp_title");
        statusView = text(root, R.id.yelp_status, "yelp_status");
        cardView = text(root, R.id.yelp_card, "yelp_card");
        listView = text(root, R.id.yelp_list, "yelp_list");

        loadButton = button(root, R.id.yelp_load, "yelp_load");
        nextButton = button(root, R.id.yelp_next, "yelp_next");
        searchButton = button(root, R.id.yelp_search, "yelp_search");
        detailsButton = button(root, R.id.yelp_details, "yelp_details");
        saveButton = button(root, R.id.yelp_save, "yelp_save");

        setClick(root, R.id.yelp_load, "yelp_load", new View.OnClickListener() {
            public void onClick(View view) { fetchLiveFeed(); }
        });
        setClick(root, R.id.yelp_next, "yelp_next", new View.OnClickListener() {
            public void onClick(View view) { nextPlace(); }
        });
        setClick(root, R.id.yelp_search, "yelp_search", new View.OnClickListener() {
            public void onClick(View view) { navigateSearch(); }
        });
        setClick(root, R.id.yelp_details, "yelp_details", new View.OnClickListener() {
            public void onClick(View view) { openDetails(); }
        });
        setClick(root, R.id.yelp_save, "yelp_save", new View.OnClickListener() {
            public void onClick(View view) { savePlace(); }
        });
        setClick(root, R.id.yelp_sort, "yelp_sort", new View.OnClickListener() {
            public void onClick(View view) { toggleSortRating(); }
        });
        setClick(root, R.id.yelp_open_now, "yelp_open_now", new View.OnClickListener() {
            public void onClick(View view) { toggleOpenNow(); }
        });
        setClick(root, R.id.yelp_delivery, "yelp_delivery", new View.OnClickListener() {
            public void onClick(View view) { toggleDelivery(); }
        });
        setClick(root, R.id.yelp_price, "yelp_price", new View.OnClickListener() {
            public void onClick(View view) { togglePrice(); }
        });
        setClick(root, R.id.yelp_pizza, "yelp_pizza", new View.OnClickListener() {
            public void onClick(View view) { selectPizza(); }
        });
        setClick(root, R.id.yelp_asian, "yelp_asian", new View.OnClickListener() {
            public void onClick(View view) { selectAsian(); }
        });
        setClick(root, R.id.yelp_dinner, "yelp_dinner", new View.OnClickListener() {
            public void onClick(View view) { selectDinner(); }
        });
        setClick(root, R.id.yelp_dessert, "yelp_dessert", new View.OnClickListener() {
            public void onClick(View view) { selectDessert(); }
        });
        setClick(root, R.id.yelp_discover, "yelp_discover", new View.OnClickListener() {
            public void onClick(View view) { navigateDiscover(); }
        });
        setClick(root, R.id.yelp_saved, "yelp_saved", new View.OnClickListener() {
            public void onClick(View view) { navigateSaved(); }
        });
        setClick(root, R.id.yelp_scroll_up, "yelp_scroll_up", new View.OnClickListener() {
            public void onClick(View view) { scrollListUp(); }
        });
        setClick(root, R.id.yelp_scroll_down, "yelp_scroll_down", new View.OnClickListener() {
            public void onClick(View view) { scrollListDown(); }
        });

        YelpLiveLog.mark("XML_BIND_OK",
                "title=" + (titleView != null)
                        + " status=" + (statusView != null)
                        + " card=" + (cardView != null)
                        + " list=" + (listView != null)
                        + " buttons=" + boundButtonCount());
    }

    private void probeXmlLayout(View root) {
        try {
            int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(1013, View.MeasureSpec.EXACTLY);
            root.measure(wSpec, hSpec);
            root.layout(0, 0, 480, 1013);
            YelpLiveLog.mark("XML_LAYOUT_PROBE_OK",
                    "target=480x1013"
                            + " measured=" + root.getMeasuredWidth() + "x" + root.getMeasuredHeight()
                            + " bounds=" + root.getLeft() + "," + root.getTop()
                            + "," + root.getRight() + "," + root.getBottom());
        } catch (Throwable t) {
            YelpLiveLog.mark("XML_LAYOUT_PROBE_FAIL",
                    "err=" + YelpLiveLog.token(t.getClass().getName())
                            + " msg=" + YelpLiveLog.token(shortMessage(t)));
        }
    }

    private TextView text(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof TextView) {
            return (TextView) view;
        }
        markXmlBindGap(label, view, "TextView");
        return null;
    }

    private Button button(View root, int id, String label) {
        View view = root.findViewById(id);
        if (view instanceof Button) {
            return (Button) view;
        }
        markXmlBindGap(label, view, "Button");
        return null;
    }

    private void setClick(View root, int id, String label, View.OnClickListener listener) {
        View view = root.findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
            view.setClickable(true);
            return;
        }
        markXmlBindGap(label, null, "clickable View");
    }

    private void markXmlBindGap(String label, View actual, String expected) {
        YelpLiveLog.mark("XML_BIND_GAP",
                "id=" + label + " expected=" + expected + " actual=" + className(actual));
    }

    private int boundButtonCount() {
        int count = 0;
        if (loadButton != null) count++;
        if (nextButton != null) count++;
        if (searchButton != null) count++;
        if (detailsButton != null) count++;
        if (saveButton != null) count++;
        return count;
    }

    private int countViews(View root) {
        if (root == null) return 0;
        int count = 1;
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countViews(group.getChildAt(i));
            }
        }
        return count;
    }

    private int countTextViews(View root) {
        if (root == null) return 0;
        int count = root instanceof TextView ? 1 : 0;
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countTextViews(group.getChildAt(i));
            }
        }
        return count;
    }

    private String className(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }

    private void fetchLiveFeed() {
        if (networkLoading) {
            YelpLiveLog.mark("NETWORK_FETCH_SKIP_OK", "reason=already_loading");
            return;
        }
        activeTab = TAB_DISCOVER;
        listOffset = 0;
        clearPlaceImages();
        updateRows();
        networkLoading = true;
        String feedUrl = liveFeedUrl();
        String source = query;
        status = "Loading live " + source + " feed";
        lastAction = "Loading";
        renderDirty = true;
        updateUi("Loading");
        YelpLiveLog.mark("NETWORK_FETCH_BEGIN", "url=" + YelpLiveLog.token(feedUrl));
        Thread worker = new Thread(new Runnable() {
            public void run() {
                fetchLiveFeedOnWorker(feedUrl, source);
            }
        }, "westlake-yelp-live-network");
        worker.start();
    }

    private void fetchLiveFeedOnWorker(String feedUrl, String source) {
        try {
            FetchResult feed = bridgeGet(feedUrl, 192 * 1024);
            String json = asciiString(feed.bytes);
            int parsed = parseRecipes(json);
            if (parsed <= 0) {
                throw new java.io.IOException("no live recipes parsed");
            }
            placeCount = parsed;
            if (sortByRating) {
                sortPlacesByRating();
            }
            updateRows();
            applyPlace(0);
            YelpLiveLog.mark("LIVE_JSON_OK",
                    "status=" + feed.status
                            + " bytes=" + feed.bytes.length
                            + " places=" + parsed
                            + " title=" + YelpLiveLog.token(placeName)
                            + " source=" + YelpLiveLog.token(source)
                            + " transport=host_bridge");

            int imageLimit = parsed < 6 ? parsed : 6;
            for (int i = 0; i < imageLimit; i++) {
                String rowImageUrl = thumbnailUrlFor(i);
                if (rowImageUrl == null || rowImageUrl.length() == 0) {
                    continue;
                }
                FetchResult image = bridgeGet(rowImageUrl, 128 * 1024);
                storePlaceImage(i, image.bytes);
                updateRows();
                if (i == placeIndex) {
                    applyPlace(i);
                }
                if (i == 0) {
                    YelpLiveLog.mark("LIVE_IMAGE_OK",
                            "status=" + image.status
                                    + " bytes=" + imageBytes
                                    + " bitmap=" + imageWidth + "x" + imageHeight
                                    + " hash=" + intHex(imageHash)
                                    + " transport=host_bridge");
                }
                YelpLiveLog.mark("LIVE_ROW_IMAGE_OK",
                        "index=" + i
                                + " status=" + image.status
                                + " bytes=" + image.bytes.length
                                + " hash=" + intHex(placeImageHashes[i])
                                + " transport=host_bridge");
            }
            networkLoading = false;
            status = "Live " + source + " results loaded";
            lastAction = "Live loaded";
            renderDirty = true;
            runOnUiThread(new Runnable() {
                public void run() { updateUi("Live loaded"); }
            });
            YelpLiveLog.mark("CARD_OK",
                    "name=" + YelpLiveLog.token(placeName)
                            + " rating=" + ratingText()
                            + " reviews=" + reviewCount
                            + " saved=" + savedCount);
        } catch (Throwable t) {
            networkLoading = false;
            status = shortMessage(t);
            lastAction = "Network failed";
            renderDirty = true;
            runOnUiThread(new Runnable() {
                public void run() { updateUi("Network failed"); }
            });
            YelpLiveLog.mark("NETWORK_FETCH_FAIL",
                    "err=" + YelpLiveLog.token(t.getClass().getName())
                            + " msg=" + YelpLiveLog.token(shortMessage(t)));
        }
    }

    private FetchResult bridgeGet(String url, int maxBytes) throws java.io.IOException {
        byte[] bytes = WestlakeLauncher.bridgeHttpGetBytes(url, maxBytes, 14000);
        int statusCode = WestlakeLauncher.bridgeHttpLastStatus();
        String error = WestlakeLauncher.bridgeHttpLastError();
        if (bytes == null || statusCode < 200 || statusCode >= 300) {
            YelpLiveLog.mark("NETWORK_BRIDGE_FAIL",
                    "status=" + statusCode
                            + " err=" + YelpLiveLog.token(error == null ? "unknown" : error)
                            + " url=" + YelpLiveLog.token(url));
            throw new java.io.IOException("bridge status=" + statusCode + " err=" + error);
        }
        YelpLiveLog.mark("NETWORK_BRIDGE_OK",
                "status=" + statusCode
                        + " bytes=" + bytes.length
                        + " url=" + YelpLiveLog.token(url));
        return new FetchResult(statusCode, url, bytes);
    }

    private int parseRecipes(String json) {
        names.clear();
        cuisines.clear();
        mealTypes.clear();
        difficulties.clear();
        imageUrls.clear();
        clearPlaceImages();
        int count = 0;
        int pos = 0;
        while (count < MAX_PLACES) {
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
            names.add(name);
            cuisines.add(nonEmpty(jsonString(obj, "\"cuisine\":\""), "Food"));
            mealTypes.add(nonEmpty(jsonArrayFirstString(obj, "\"mealType\":["), "Nearby"));
            difficulties.add(nonEmpty(jsonString(obj, "\"difficulty\":\""), "Open"));
            imageUrls.add(nonEmpty(jsonString(obj, "\"image\":\""), ""));
            ratings[count] = jsonTenths(obj, "\"rating\":", 46);
            reviews[count] = jsonInt(obj, "\"reviewCount\":", 0);
            count++;
            if (next < 0) break;
            pos = next;
        }
        return count;
    }

    private void sortPlacesByRating() {
        for (int i = 0; i < placeCount; i++) {
            for (int j = i + 1; j < placeCount; j++) {
                if (ratings[j] > ratings[i]) {
                    swapPlaces(i, j);
                }
            }
        }
    }

    private void swapPlaces(int a, int b) {
        String name = names.get(a);
        names.set(a, names.get(b));
        names.set(b, name);
        String cuisineValue = cuisines.get(a);
        cuisines.set(a, cuisines.get(b));
        cuisines.set(b, cuisineValue);
        String meal = mealTypes.get(a);
        mealTypes.set(a, mealTypes.get(b));
        mealTypes.set(b, meal);
        String diff = difficulties.get(a);
        difficulties.set(a, difficulties.get(b));
        difficulties.set(b, diff);
        String img = imageUrls.get(a);
        imageUrls.set(a, imageUrls.get(b));
        imageUrls.set(b, img);
        int rating = ratings[a];
        ratings[a] = ratings[b];
        ratings[b] = rating;
        int review = reviews[a];
        reviews[a] = reviews[b];
        reviews[b] = review;
    }

    private void updateRows() {
        int base = safeListOffset();
        row1Name = placeSummaryName(base, "Live restaurants loading");
        row1Meta = placeSummaryMeta(base, "Pulling from dummyjson.com");
        row2Name = placeSummaryName(base + 1, "Pizza and takeout nearby");
        row2Meta = placeSummaryMeta(base + 1, "Open now");
        row3Name = placeSummaryName(base + 2, "Dinner spots near Westlake");
        row3Meta = placeSummaryMeta(base + 2, "Delivery and pickup");
        row4Name = placeSummaryName(base + 3, "Top rated nearby");
        row4Meta = placeSummaryMeta(base + 3, "Tap a row for details");
        row5Name = placeSummaryName(base + 4, "More places nearby");
        row5Meta = placeSummaryMeta(base + 4, "Swipe the list to scroll");
        updateVisibleImages(base);
    }

    private String placeSummaryName(int index, String fallback) {
        if (index < 0 || index >= placeCount || index >= names.size()) {
            return fallback;
        }
        return nonEmpty(names.get(index), fallback);
    }

    private String placeSummaryMeta(int index, String fallback) {
        if (index < 0 || index >= placeCount || index >= cuisines.size()
                || index >= mealTypes.size()) {
            return fallback;
        }
        return nonEmpty(cuisines.get(index), "Food") + " | "
                + nonEmpty(mealTypes.get(index), "Nearby") + " | "
                + ratingText(ratings[index]) + filterSummarySuffix();
    }

    private void applyPlace(int index) {
        if (placeCount <= 0) return;
        int safe = index;
        if (safe < 0) safe = 0;
        if (safe >= placeCount) safe = placeCount - 1;
        placeIndex = safe;
        placeName = nonEmpty(names.get(safe), "Live Place");
        cuisine = nonEmpty(cuisines.get(safe), "Food");
        mealType = nonEmpty(mealTypes.get(safe), "Nearby");
        difficulty = nonEmpty(difficulties.get(safe), "Open");
        imageUrl = nonEmpty(imageUrls.get(safe), "");
        ratingTenths = ratings[safe] > 0 ? ratings[safe] : 46;
        reviewCount = reviews[safe];
        liveImageData = imageFor(safe);
        imageBytes = imageBytesFor(safe);
        imageHash = imageHashFor(safe);
        imageWidth = imageBytes > 0 ? 96 : 0;
        imageHeight = imageBytes > 0 ? 64 : 0;
    }

    private void nextPlace() {
        if (placeCount <= 0) {
            fetchLiveFeed();
            return;
        }
        applyPlace((placeIndex + 1) % placeCount);
        lastAction = "Next";
        status = "Showing live result " + (placeIndex + 1) + " of " + placeCount;
        renderDirty = true;
        updateUi("Next");
        YelpLiveLog.mark("NEXT_PLACE_OK",
                "index=" + placeIndex + " name=" + YelpLiveLog.token(placeName)
                        + " rating=" + ratingText());
    }

    private void openPlace0() {
        openPlaceAt(listOffset + 0);
    }

    private void openPlace1() {
        openPlaceAt(listOffset + 1);
    }

    private void openPlace2() {
        openPlaceAt(listOffset + 2);
    }

    private void openPlace3() {
        openPlaceAt(listOffset + 3);
    }

    private void openPlace4() {
        openPlaceAt(listOffset + 4);
    }

    private void savePlace0() {
        savePlaceAt(listOffset + 0);
    }

    private void savePlace1() {
        savePlaceAt(listOffset + 1);
    }

    private void savePlace2() {
        savePlaceAt(listOffset + 2);
    }

    private void savePlace3() {
        savePlaceAt(listOffset + 3);
    }

    private void savePlace4() {
        savePlaceAt(listOffset + 4);
    }

    private void openPlaceAt(int index) {
        if (placeCount <= 0) {
            fetchLiveFeed();
            return;
        }
        applyPlace(index);
        activeTab = TAB_DETAILS;
        detailAction = "Viewing details";
        lastAction = "Details";
        status = "Opened list result " + (placeIndex + 1) + " of " + placeCount;
        renderDirty = true;
        updateUi("Details");
        YelpLiveLog.mark("SELECT_PLACE_OK",
                "index=" + placeIndex + " name=" + YelpLiveLog.token(placeName)
                        + " rating=" + ratingText());
        YelpLiveLog.mark("NEXT_PLACE_OK",
                "index=" + placeIndex + " name=" + YelpLiveLog.token(placeName)
                        + " rating=" + ratingText() + " source=list_click");
        YelpLiveLog.mark("DETAILS_OPEN_OK",
                "name=" + YelpLiveLog.token(placeName) + " cuisine=" + YelpLiveLog.token(cuisine));
    }

    private void scrollListDown() {
        setListOffset(listOffset + 1, "down");
    }

    private void scrollListUp() {
        setListOffset(listOffset - 1, "up");
    }

    private void setListOffset(int requested, String direction) {
        int max = maxListOffset();
        int next = requested;
        if (next < 0) next = 0;
        if (next > max) next = max;
        listOffset = next;
        activeTab = TAB_SEARCH;
        updateRows();
        status = "Showing results " + (next + 1) + "-" + (next + 5 > placeCount ? placeCount : next + 5)
                + " of " + placeCount;
        lastAction = "Scroll " + direction;
        renderDirty = true;
        updateUi(lastAction);
        YelpLiveLog.mark("LIST_SCROLL_OK",
                "offset=" + listOffset
                        + " max=" + max
                        + " direction=" + YelpLiveLog.token(direction)
                        + " count=" + placeCount);
    }

    private void savePlaceAt(int index) {
        if (placeCount > 0) {
            applyPlace(index);
        }
        savePlace();
    }

    private void savePlace() {
        if (!saved) {
            savedCount++;
        }
        saved = true;
        activeTab = TAB_SAVED;
        lastAction = "Saved";
        status = "Saved for offline handoff";
        renderDirty = true;
        updateUi("Saved");
        YelpLiveLog.mark("SAVE_PLACE_OK",
                "name=" + YelpLiveLog.token(placeName) + " saved=" + savedCount);
        YelpLiveLog.mark("NAV_SAVED_OK", "saved=" + savedCount);
    }

    private void openDetails() {
        activeTab = TAB_DETAILS;
        detailAction = "Viewing details";
        lastAction = "Details";
        status = "Details from live feed";
        renderDirty = true;
        updateUi("Details");
        YelpLiveLog.mark("DETAILS_OPEN_OK",
                "name=" + YelpLiveLog.token(placeName) + " cuisine=" + YelpLiveLog.token(cuisine));
    }

    private void navigateDiscover() {
        activeTab = TAB_DISCOVER;
        lastAction = "Discover";
        renderDirty = true;
        updateUi("Discover");
        YelpLiveLog.mark("NAV_DISCOVER_OK", "tab=Discover");
    }

    private void navigateSearch() {
        activeTab = TAB_SEARCH;
        lastAction = "Search";
        status = "Query " + query + " over live feed";
        renderDirty = true;
        updateUi("Search");
        YelpLiveLog.mark("NAV_SEARCH_OK", "query=" + YelpLiveLog.token(query));
    }

    private void navigateDetails() {
        openDetails();
    }

    private void navigateSaved() {
        activeTab = TAB_SAVED;
        lastAction = "Saved tab";
        renderDirty = true;
        updateUi("Saved");
        YelpLiveLog.mark("NAV_SAVED_OK", "saved=" + savedCount);
    }

    private void selectPizza() {
        setQueryMode(0, "pizza", "Pizza");
    }

    private void selectAsian() {
        setQueryMode(1, "asian", "Asian");
    }

    private void selectDinner() {
        setQueryMode(2, "dinner", "Dinner");
    }

    private void selectDessert() {
        setQueryMode(3, "dessert", "Dessert");
    }

    private void setQueryMode(int mode, String nextQuery, String nextCategory) {
        queryMode = mode;
        query = nextQuery;
        category = nextCategory;
        activeTab = TAB_SEARCH;
        YelpLiveLog.mark("CATEGORY_SELECT_OK",
                "query=" + YelpLiveLog.token(query)
                        + " category=" + YelpLiveLog.token(category));
        fetchLiveFeed();
    }

    private void toggleOpenNow() {
        openNowFilter = !openNowFilter;
        filterChanged("open_now", openNowFilter);
    }

    private void toggleDelivery() {
        deliveryFilter = !deliveryFilter;
        filterChanged("delivery", deliveryFilter);
    }

    private void togglePrice() {
        priceFilter = !priceFilter;
        filterChanged("price", priceFilter);
    }

    private void toggleSortRating() {
        sortByRating = !sortByRating;
        if (placeCount > 1 && sortByRating) {
            sortPlacesByRating();
            applyPlace(0);
        }
        updateRows();
        filterChanged("top_rated", sortByRating);
    }

    private void filterChanged(String name, boolean enabled) {
        activeTab = TAB_SEARCH;
        status = "Filter " + name + " " + (enabled ? "on" : "off");
        lastAction = "Filter";
        updateRows();
        renderDirty = true;
        updateUi("Filter");
        YelpLiveLog.mark("FILTER_TOGGLE_OK",
                "name=" + YelpLiveLog.token(name) + " enabled=" + enabled);
    }

    private void callPlace() {
        detailAction = "Call " + placeName;
        lastAction = "Call";
        renderDirty = true;
        updateUi("Call");
        YelpLiveLog.mark("CALL_PLACE_OK", "name=" + YelpLiveLog.token(placeName));
    }

    private void directionsPlace() {
        detailAction = "Directions to " + placeName;
        lastAction = "Directions";
        renderDirty = true;
        updateUi("Directions");
        YelpLiveLog.mark("DIRECTIONS_PLACE_OK", "name=" + YelpLiveLog.token(placeName));
    }

    private void reviewPlace() {
        detailAction = "Review composer ready";
        lastAction = "Review";
        renderDirty = true;
        updateUi("Review");
        YelpLiveLog.mark("REVIEW_PLACE_OK", "name=" + YelpLiveLog.token(placeName));
    }

    private boolean consumeRenderDirty() {
        boolean dirty = renderDirty;
        renderDirty = false;
        return dirty;
    }

    private void updateUi(String action) {
        lastAction = action;
        if (titleView != null) {
            titleView.setText("Westlake Yelp Live");
        }
        if (statusView != null) {
            statusView.setText(status + " | " + action);
        }
        if (cardView != null) {
            cardView.setText(placeName + "\n" + cuisine + " - " + mealType
                    + "\n" + ratingText() + " stars | " + reviewCount + " reviews"
                    + "\nimage bytes " + imageBytes + " hash " + intHex(imageHash));
        }
        if (listView != null) {
            listView.setText(listSummary());
        }
    }

    private String listSummary() {
        StringBuilder out = new StringBuilder();
        out.append("Live feed count: ").append(placeCount)
                .append(" offset ").append(listOffset).append('\n');
        for (int i = 0; i < placeCount && i < 5; i++) {
            out.append(i + 1).append(". ").append(names.get(i)).append(" - ")
                    .append(cuisines.get(i)).append(" - ")
                    .append(ratingText(ratings[i])).append('\n');
        }
        return out.toString();
    }

    private String liveFeedUrl() {
        if (queryMode == 1) {
            return "https://dummyjson.com/recipes/tag/Asian?limit=8&" + SELECT_FIELDS;
        }
        if (queryMode == 2) {
            return "https://dummyjson.com/recipes/meal-type/dinner?limit=8&" + SELECT_FIELDS;
        }
        if (queryMode == 3) {
            return "https://dummyjson.com/recipes/search?q=cookie&limit=8&" + SELECT_FIELDS;
        }
        return "https://dummyjson.com/recipes/search?q=pizza&limit=8&" + SELECT_FIELDS;
    }

    private String filterSummarySuffix() {
        String suffix = "";
        if (deliveryFilter) {
            suffix += " | Delivery";
        }
        if (priceFilter) {
            suffix += " | $$";
        }
        if (openNowFilter) {
            suffix += " | Open";
        }
        return suffix;
    }

    private int safeListOffset() {
        int max = maxListOffset();
        if (listOffset < 0) listOffset = 0;
        if (listOffset > max) listOffset = max;
        return listOffset;
    }

    private int maxListOffset() {
        int max = placeCount - 5;
        return max > 0 ? max : 0;
    }

    private void clearPlaceImages() {
        for (int i = 0; i < MAX_PLACES; i++) {
            placeImages[i] = null;
            placeImageHashes[i] = 0;
            placeImageBytes[i] = 0;
        }
        liveImageData = new byte[0];
        imageBytes = 0;
        imageHash = 0;
        imageWidth = 0;
        imageHeight = 0;
        updateVisibleImages(0);
    }

    private void storePlaceImage(int index, byte[] bytes) {
        if (index < 0 || index >= MAX_PLACES) return;
        byte[] safe = bytes != null ? bytes : new byte[0];
        placeImages[index] = safe;
        placeImageHashes[index] = hashBytes(safe);
        placeImageBytes[index] = safe.length;
        if (index == placeIndex) {
            liveImageData = safe;
            imageBytes = safe.length;
            imageHash = placeImageHashes[index];
            imageWidth = safe.length > 0 ? 96 : 0;
            imageHeight = safe.length > 0 ? 64 : 0;
        }
    }

    private String thumbnailUrlFor(int index) {
        String seed = "westlake-" + index;
        if (index >= 0 && index < names.size()) {
            seed = seed + "-" + urlSeed(names.get(index));
        }
        return "https://picsum.photos/seed/" + seed + "/96/64";
    }

    private String urlSeed(String value) {
        if (value == null || value.length() == 0) {
            return "restaurant";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length() && out.length() < 32; i++) {
            char c = value.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                out.append((char) ('a' + (c - 'A')));
            } else if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                out.append(c);
            } else if (out.length() > 0 && out.charAt(out.length() - 1) != '-') {
                out.append('-');
            }
        }
        if (out.length() == 0) {
            return "restaurant";
        }
        return out.toString();
    }

    private void updateVisibleImages(int base) {
        row1ImageData = imageFor(base);
        row2ImageData = imageFor(base + 1);
        row3ImageData = imageFor(base + 2);
        row4ImageData = imageFor(base + 3);
        row5ImageData = imageFor(base + 4);
        row1ImageHash = imageHashFor(base);
        row2ImageHash = imageHashFor(base + 1);
        row3ImageHash = imageHashFor(base + 2);
        row4ImageHash = imageHashFor(base + 3);
        row5ImageHash = imageHashFor(base + 4);
        row1ImageBytes = imageBytesFor(base);
        row2ImageBytes = imageBytesFor(base + 1);
        row3ImageBytes = imageBytesFor(base + 2);
        row4ImageBytes = imageBytesFor(base + 3);
        row5ImageBytes = imageBytesFor(base + 4);
    }

    private byte[] imageFor(int index) {
        if (index < 0 || index >= MAX_PLACES) return new byte[0];
        byte[] bytes = placeImages[index];
        return bytes != null ? bytes : new byte[0];
    }

    private int imageHashFor(int index) {
        if (index < 0 || index >= MAX_PLACES) return 0;
        return placeImageHashes[index];
    }

    private int imageBytesFor(int index) {
        if (index < 0 || index >= MAX_PLACES) return 0;
        return placeImageBytes[index];
    }

    private String jsonString(String json, String key) {
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        StringBuilder out = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                out.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String jsonArrayFirstString(String json, String key) {
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();
        while (start < json.length() && json.charAt(start) != '"') start++;
        if (start >= json.length()) return null;
        start++;
        int end = json.indexOf('"', start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    private int jsonInt(String json, String key, int fallback) {
        int start = json.indexOf(key);
        if (start < 0) return fallback;
        start += key.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == ':')) {
            start++;
        }
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c < '0' || c > '9') break;
            end++;
        }
        if (end <= start) return fallback;
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private int jsonTenths(String json, String key, int fallback) {
        int start = json.indexOf(key);
        if (start < 0) return fallback;
        start += key.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        int whole = 0;
        int fraction = 0;
        if (start < json.length() && json.charAt(start) >= '0' && json.charAt(start) <= '9') {
            whole = json.charAt(start) - '0';
            start++;
        } else {
            return fallback;
        }
        if (start < json.length() && json.charAt(start) == '.'
                && start + 1 < json.length()
                && json.charAt(start + 1) >= '0' && json.charAt(start + 1) <= '9') {
            fraction = json.charAt(start + 1) - '0';
        }
        return whole * 10 + fraction;
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.length() == 0 ? fallback : value;
    }

    private String asciiString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return new String(chars);
    }

    private int hashBytes(byte[] bytes) {
        int hash = 0x4a17;
        if (bytes == null) return hash;
        for (int i = 0; i < bytes.length; i++) {
            hash = (hash * 33) ^ (bytes[i] & 0xff);
        }
        return hash;
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

    private String ratingText() {
        return ratingText(ratingTenths);
    }

    private String ratingText(int tenths) {
        if (tenths <= 0) tenths = 46;
        return (tenths / 10) + "." + (tenths % 10);
    }

    private String shortMessage(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null || msg.length() == 0) return t.getClass().getName();
        if (msg.length() > 80) return msg.substring(0, 80);
        return msg;
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
