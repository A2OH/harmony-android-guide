package com.example.mockdonalds;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity {
    private List<MenuItem> menuItems;
    private static final int RED_PRIMARY = 0xFFDA291C;
    private static final int GOLD_ACCENT = 0xFFFFC72C;
    private static final int BG_WHITE = 0xFFFAFAFA;
    private static final int TEXT_DARK = 0xFF212121;
    private static final int TEXT_SECONDARY = 0xFF757575;
    private static final int GREEN_PRICE = 0xFF2E7D32;
    private static final int DIVIDER = 0xFFE0E0E0;
    private static int cartCount = 0;
    private static TextView cartBadge;
    private static View menuRootView; // saved separately from shimRootView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try { super.onCreate(savedInstanceState); } catch (Exception e) {}

        Context ctx = getHostContext();

        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1, "Big Mock Burger", "Two all-beef patties, special sauce, lettuce, cheese", 5.99, "Burgers"));
        menuItems.add(new MenuItem(2, "Quarter Mocker", "Fresh beef with pickles, onions, ketchup & mustard", 4.99, "Burgers"));
        menuItems.add(new MenuItem(3, "Mock Nuggets (6)", "Crispy golden nuggets with dipping sauce", 3.49, "Sides"));
        menuItems.add(new MenuItem(4, "Mock Fries (L)", "Golden, crispy, irresistible french fries", 2.99, "Sides"));
        menuItems.add(new MenuItem(5, "Mock Cola (L)", "Refreshing ice-cold cola", 1.99, "Drinks"));
        menuItems.add(new MenuItem(6, "Mock Shake", "Thick, creamy vanilla milkshake", 3.99, "Drinks"));
        menuItems.add(new MenuItem(7, "Mock Flurry", "Soft serve with cookie pieces", 2.49, "Desserts"));
        menuItems.add(new MenuItem(8, "Apple Mock Pie", "Warm baked apple pie with cinnamon", 1.49, "Desserts"));

        final Context fCtx = ctx;

        // Root layout
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG_WHITE);

        // ═══ Top bar ═══
        LinearLayout topBar = new LinearLayout(ctx);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(RED_PRIMARY);
        topBar.setPadding(dp(ctx,16), dp(ctx,12), dp(ctx,16), dp(ctx,12));
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        // Logo text
        TextView logo = new TextView(ctx);
        logo.setText("M  MockDonalds");
        logo.setTextSize(22);
        logo.setTextColor(GOLD_ACCENT);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        topBar.addView(logo, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Cart badge
        LinearLayout cartLayout = new LinearLayout(ctx);
        cartLayout.setGravity(Gravity.CENTER);
        cartLayout.setPadding(dp(ctx,12), dp(ctx,6), dp(ctx,12), dp(ctx,6));
        GradientDrawable cartBg = new GradientDrawable();
        cartBg.setColor(GOLD_ACCENT);
        cartBg.setCornerRadius(dp(ctx,16));
        cartLayout.setBackground(cartBg);
        cartBadge = new TextView(ctx);
        cartBadge.setText("Cart (" + cartCount + ")");
        cartBadge.setTextSize(14);
        cartBadge.setTextColor(TEXT_DARK);
        cartBadge.setTypeface(Typeface.DEFAULT_BOLD);
        cartLayout.addView(cartBadge);
        topBar.addView(cartLayout);

        root.addView(topBar);

        // ═══ Category header ═══
        TextView catHeader = new TextView(ctx);
        catHeader.setText("OUR MENU");
        catHeader.setTextSize(13);
        catHeader.setTextColor(TEXT_SECONDARY);
        catHeader.setTypeface(Typeface.DEFAULT_BOLD);
        catHeader.setPadding(dp(ctx,16), dp(ctx,12), dp(ctx,16), dp(ctx,4));
        catHeader.setLetterSpacing(0.15f);
        root.addView(catHeader);

        // ═══ Menu list ═══
        ListView listView = new ListView(ctx);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setPadding(dp(ctx,8), 0, dp(ctx,8), 0);
        listView.setAdapter(new BaseAdapter() {
            public int getCount() { return menuItems.size(); }
            public Object getItem(int p) { return menuItems.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                return createMenuCard(fCtx, menuItems.get(pos));
            }
        });
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                showItemDetail(fCtx, menuItems.get(pos));
            }
        });
        root.addView(listView, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // Store and display
        menuRootView = root;
        setHostContentView(root);
        try { setContentView(root); } catch (Exception e) {}
    }

    private View createMenuCard(Context ctx, MenuItem item) {
        // Card container with rounded corners and shadow
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(ctx,16), dp(ctx,14), dp(ctx,16), dp(ctx,14));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(dp(ctx,4), dp(ctx,4), dp(ctx,4), dp(ctx,4));
        card.setLayoutParams(cardLp);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dp(ctx,12));
        cardBg.setStroke(1, DIVIDER);
        card.setBackground(cardBg);
        card.setElevation(dp(ctx,2));

        // Top row: name + price
        LinearLayout topRow = new LinearLayout(ctx);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        // Category emoji
        String emoji = getCategoryEmoji(item.category);
        TextView emojiView = new TextView(ctx);
        emojiView.setText(emoji);
        emojiView.setTextSize(16);
        emojiView.setPadding(0, 0, dp(ctx,12), 0);
        topRow.addView(emojiView);

        // Name
        TextView name = new TextView(ctx);
        name.setText(item.name);
        name.setTextSize(17);
        name.setTextColor(TEXT_DARK);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        topRow.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Price
        TextView price = new TextView(ctx);
        price.setText("$" + String.format("%.2f", item.price));
        price.setTextSize(17);
        price.setTextColor(GREEN_PRICE);
        price.setTypeface(Typeface.DEFAULT_BOLD);
        topRow.addView(price);

        card.addView(topRow);

        // Description
        TextView desc = new TextView(ctx);
        desc.setText(item.description);
        desc.setTextSize(13);
        desc.setTextColor(TEXT_SECONDARY);
        desc.setPadding(dp(ctx,36), dp(ctx,4), 0, 0);
        card.addView(desc);

        return card;
    }

    private void showItemDetail(final Context ctx, final MenuItem item) {
        ScrollView scroll = new ScrollView(ctx);
        scroll.setBackgroundColor(BG_WHITE);

        LinearLayout detail = new LinearLayout(ctx);
        detail.setOrientation(LinearLayout.VERTICAL);

        // Top bar with back
        LinearLayout topBar = new LinearLayout(ctx);
        topBar.setBackgroundColor(RED_PRIMARY);
        topBar.setPadding(dp(ctx,8), dp(ctx,8), dp(ctx,16), dp(ctx,8));
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        Button backBtn = new Button(ctx);
        backBtn.setText("←");
        backBtn.setTextSize(20);
        backBtn.setTextColor(Color.WHITE);
        backBtn.setBackgroundColor(Color.TRANSPARENT);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { goBackToMenu(); }
        });
        topBar.addView(backBtn, new LinearLayout.LayoutParams(dp(ctx,48), dp(ctx,48)));

        TextView title = new TextView(ctx);
        title.setText(item.name);
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        topBar.addView(title);
        detail.addView(topBar);

        // Hero section
        LinearLayout heroSection = new LinearLayout(ctx);
        heroSection.setOrientation(LinearLayout.VERTICAL);
        heroSection.setGravity(Gravity.CENTER);
        heroSection.setPadding(dp(ctx,32), dp(ctx,32), dp(ctx,32), dp(ctx,24));
        heroSection.setBackgroundColor(0xFFFFF3E0);

        TextView bigEmoji = new TextView(ctx);
        bigEmoji.setText(getCategoryEmoji(item.category));
        bigEmoji.setTextSize(32);
        bigEmoji.setGravity(Gravity.CENTER);
        heroSection.addView(bigEmoji);

        TextView priceTag = new TextView(ctx);
        priceTag.setText("$" + String.format("%.2f", item.price));
        priceTag.setTextSize(36);
        priceTag.setTextColor(GREEN_PRICE);
        priceTag.setTypeface(Typeface.DEFAULT_BOLD);
        priceTag.setGravity(Gravity.CENTER);
        priceTag.setPadding(0, dp(ctx,8), 0, 0);
        heroSection.addView(priceTag);

        detail.addView(heroSection);

        // Description card
        LinearLayout descCard = new LinearLayout(ctx);
        descCard.setOrientation(LinearLayout.VERTICAL);
        descCard.setPadding(dp(ctx,24), dp(ctx,20), dp(ctx,24), dp(ctx,20));
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descLp.setMargins(dp(ctx,16), dp(ctx,16), dp(ctx,16), dp(ctx,8));
        descCard.setLayoutParams(descLp);
        GradientDrawable descBg = new GradientDrawable();
        descBg.setColor(Color.WHITE);
        descBg.setCornerRadius(dp(ctx,12));
        descBg.setStroke(1, DIVIDER);
        descCard.setBackground(descBg);
        descCard.setElevation(dp(ctx,2));

        TextView descLabel = new TextView(ctx);
        descLabel.setText("ABOUT THIS ITEM");
        descLabel.setTextSize(12);
        descLabel.setTextColor(TEXT_SECONDARY);
        descLabel.setTypeface(Typeface.DEFAULT_BOLD);
        descLabel.setLetterSpacing(0.1f);
        descCard.addView(descLabel);

        TextView descText = new TextView(ctx);
        descText.setText(item.description);
        descText.setTextSize(16);
        descText.setTextColor(TEXT_DARK);
        descText.setPadding(0, dp(ctx,8), 0, 0);
        descText.setLineSpacing(dp(ctx,4), 1);
        descCard.addView(descText);

        TextView catText = new TextView(ctx);
        catText.setText("Category: " + item.category);
        catText.setTextSize(14);
        catText.setTextColor(TEXT_SECONDARY);
        catText.setPadding(0, dp(ctx,12), 0, 0);
        descCard.addView(catText);

        detail.addView(descCard);

        // Add to Cart button
        Button addBtn = new Button(ctx);
        addBtn.setText("Add to Cart - $" + String.format("%.2f", item.price));
        addBtn.setTextSize(18);
        addBtn.setTextColor(Color.WHITE);
        addBtn.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(RED_PRIMARY);
        btnBg.setCornerRadius(dp(ctx,28));
        addBtn.setBackground(btnBg);
        addBtn.setPadding(dp(ctx,24), dp(ctx,16), dp(ctx,24), dp(ctx,16));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(dp(ctx,16), dp(ctx,16), dp(ctx,16), dp(ctx,8));
        addBtn.setLayoutParams(btnLp);
        addBtn.setElevation(dp(ctx,4));
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cartCount++;
                if (cartBadge != null) cartBadge.setText("Cart (" + cartCount + ")");
                goBackToMenu();
            }
        });
        detail.addView(addBtn);

        scroll.addView(detail);
        setHostContentView(scroll);
        try { setContentView(scroll); } catch (Exception e) {} // shim path
    }

    private void goBackToMenu() {
        if (menuRootView == null) return;
        if (menuRootView.getParent() != null)
            ((ViewGroup) menuRootView.getParent()).removeView(menuRootView);
        try { setContentView(menuRootView); } catch (Exception e) {} // shim path
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            final Activity hostAct = (Activity) host.getField("instance").get(null);
            hostAct.runOnUiThread(new Runnable() {
                public void run() {
                    if (menuRootView.getParent() != null)
                        ((ViewGroup) menuRootView.getParent()).removeView(menuRootView);
                    hostAct.setContentView(menuRootView);
                }
            });
        } catch (Exception e) {}
    }

    private void setHostContentView(final View view) {
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            host.getField("shimRootView").set(null, view);
            final Activity hostAct = (Activity) host.getField("instance").get(null);
            hostAct.runOnUiThread(new Runnable() {
                public void run() { hostAct.setContentView(view); }
            });
        } catch (Exception e) {}
    }

    private Context getHostContext() {
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object inst = host.getField("instance").get(null);
            if (inst instanceof Context) return (Context) inst;
        } catch (Exception e) {}
        return this;
    }

    private String getCategoryEmoji(String category) {
        if ("Burgers".equals(category)) return "[B]";
        if ("Sides".equals(category)) return "[S]";
        if ("Drinks".equals(category)) return "[D]";
        if ("Desserts".equals(category)) return "[*]";
        return "[?]";
    }

    private static int dp(Context ctx, int dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density);
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
}
