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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Complete MockDonalds app — menu, detail, cart, checkout, confirmation.
 * All using real Android Views, zero platform coupling beyond Canvas/Paint.
 */
public class MockApp {
    // ═══ Theme ═══
    static final int RED = 0xFFDA291C;
    static final int GOLD = 0xFFFFC72C;
    static final int BG = 0xFFFAFAFA;
    static final int WHITE = 0xFFFFFFFF;
    static final int DARK = 0xFF212121;
    static final int SECONDARY = 0xFF757575;
    static final int GREEN = 0xFF2E7D32;
    static final int DIVIDER = 0xFFE0E0E0;
    static final int ORANGE_BG = 0xFFFFF3E0;

    // ═══ State ═══
    static List<MenuItem> menu;
    static Map<Integer, Integer> cart = new HashMap<>(); // itemId → quantity
    static int orderNumber = 0;
    static Context ctx;
    static Activity hostActivity;
    static View menuView; // cached menu screen

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}

        menu = new ArrayList<>();
        menu.add(new MenuItem(1, "Big Mock Burger", "Two all-beef patties, special sauce, lettuce, cheese, pickles, onions on a sesame seed bun", 5.99, "Burgers"));
        menu.add(new MenuItem(2, "Quarter Mocker", "Fresh beef with pickles, onions, ketchup & mustard on a toasted bun", 4.99, "Burgers"));
        menu.add(new MenuItem(3, "MockChicken Deluxe", "Crispy chicken fillet with lettuce & mayo", 5.49, "Burgers"));
        menu.add(new MenuItem(4, "Mock Nuggets (6)", "Golden crispy chicken nuggets with your choice of dipping sauce", 3.49, "Sides"));
        menu.add(new MenuItem(5, "Mock Fries (L)", "Golden, crispy, irresistible french fries — large size", 2.99, "Sides"));
        menu.add(new MenuItem(6, "Mock Cola (L)", "Ice-cold refreshing cola — large cup", 1.99, "Drinks"));
        menu.add(new MenuItem(7, "Mock Shake", "Thick, creamy vanilla milkshake made with real ice cream", 3.99, "Drinks"));
        menu.add(new MenuItem(8, "Mock Flurry", "Creamy soft serve blended with cookie pieces", 2.49, "Desserts"));
        menu.add(new MenuItem(9, "Apple Mock Pie", "Warm baked apple pie with cinnamon in a crispy crust", 1.49, "Desserts"));
        menu.add(new MenuItem(10, "Chocolate Mock Cake", "Rich chocolate lava cake with fudge center", 2.99, "Desserts"));
    }

    // ═══ Navigation ═══
    static void show(final View view) {
        if (hostActivity == null) return;
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);
                hostActivity.setContentView(view);
            }
        });
    }

    // ═══ Helpers ═══
    static int dp(int d) { return (int)(d * ctx.getResources().getDisplayMetrics().density); }

    static GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    static GradientDrawable roundRectStroke(int color, int radiusDp, int strokeColor) {
        GradientDrawable d = roundRect(color, radiusDp);
        d.setStroke(dp(1), strokeColor);
        return d;
    }

    static TextView label(String text, int sizeSp, int color) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(sizeSp);
        tv.setTextColor(color);
        return tv;
    }

    static TextView boldLabel(String text, int sizeSp, int color) {
        TextView tv = label(text, sizeSp, color);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    static Button actionButton(String text, int bgColor, int textColor, View.OnClickListener listener) {
        Button btn = new Button(ctx);
        btn.setText(text);
        btn.setTextSize(16);
        btn.setTextColor(textColor);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setBackground(roundRect(bgColor, 24));
        btn.setPadding(dp(24), dp(14), dp(24), dp(14));
        btn.setElevation(dp(3));
        btn.setOnClickListener(listener);
        return btn;
    }

    static LinearLayout topBar(String title, boolean showBack, boolean showCart) {
        LinearLayout bar = new LinearLayout(ctx);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(RED);
        bar.setPadding(dp(12), dp(10), dp(16), dp(10));
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setElevation(dp(4));

        if (showBack) {
            Button back = new Button(ctx);
            back.setText("←");
            back.setTextSize(22);
            back.setTextColor(Color.WHITE);
            back.setBackgroundColor(Color.TRANSPARENT);
            back.setPadding(dp(8), 0, dp(12), 0);
            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showMenu(); }
            });
            bar.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        }

        TextView titleTv = boldLabel(title, showBack ? 18 : 20, GOLD);
        bar.addView(titleTv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        if (showCart) {
            LinearLayout cartBtn = new LinearLayout(ctx);
            cartBtn.setGravity(Gravity.CENTER);
            cartBtn.setPadding(dp(12), dp(6), dp(12), dp(6));
            cartBtn.setBackground(roundRect(GOLD, 16));
            cartBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showCart(); }
            });
            int count = getCartTotal();
            TextView cartText = boldLabel("\uD83D\uDED2 " + count, 13, DARK);
            cartBtn.addView(cartText);
            bar.addView(cartBtn);
        }

        return bar;
    }

    static String emoji(String cat) {
        if ("Burgers".equals(cat)) return "\uD83C\uDF54";
        if ("Sides".equals(cat)) return "\uD83C\uDF5F";
        if ("Drinks".equals(cat)) return "\uD83E\uDD64";
        if ("Desserts".equals(cat)) return "\uD83C\uDF70";
        return "\uD83C\uDF7D";
    }

    static int getCartTotal() {
        int total = 0;
        for (int q : cart.values()) total += q;
        return total;
    }

    static double getCartPrice() {
        double total = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            for (MenuItem m : menu) {
                if (m.id == e.getKey()) { total += m.price * e.getValue(); break; }
            }
        }
        return total;
    }

    static MenuItem findItem(int id) {
        for (MenuItem m : menu) if (m.id == id) return m;
        return null;
    }

    // ═══════════════════════════════════════════
    //  MENU SCREEN
    // ═══════════════════════════════════════════
    public static void showMenu() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        root.addView(topBar("\uD83C\uDF54 MockDonalds", false, true));

        // Category tabs
        LinearLayout tabs = new LinearLayout(ctx);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setPadding(dp(8), dp(8), dp(8), dp(4));
        tabs.setBackgroundColor(WHITE);
        final String[] cats = {"All", "Burgers", "Sides", "Drinks", "Desserts"};
        final String[] catEmoji = {"\uD83C\uDF7D", "\uD83C\uDF54", "\uD83C\uDF5F", "\uD83E\uDD64", "\uD83C\uDF70"};
        for (int i = 0; i < cats.length; i++) {
            final int idx = i;
            TextView tab = new TextView(ctx);
            tab.setText(catEmoji[i] + " " + cats[i]);
            tab.setTextSize(12);
            tab.setPadding(dp(10), dp(6), dp(10), dp(6));
            tab.setBackground(roundRect(i == 0 ? RED : 0xFFEEEEEE, 16));
            tab.setTextColor(i == 0 ? Color.WHITE : SECONDARY);
            tab.setTypeface(Typeface.DEFAULT_BOLD);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tlp.setMargins(dp(3), 0, dp(3), 0);
            tab.setLayoutParams(tlp);
            tab.setGravity(Gravity.CENTER);
            tab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showFilteredMenu(cats[idx]); }
            });
            tabs.addView(tab);
        }
        root.addView(tabs);

        // Divider
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));

        // Menu list
        final List<MenuItem> items = new ArrayList<>(menu);
        ListView list = new ListView(ctx);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setBackgroundColor(BG);
        list.setPadding(dp(8), dp(4), dp(8), dp(4));
        list.setAdapter(new BaseAdapter() {
            public int getCount() { return items.size(); }
            public Object getItem(int p) { return items.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                return menuCard(items.get(pos));
            }
        });
        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                showDetail(items.get(pos));
            }
        });
        root.addView(list, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // XML Layout Test button
        Button xmlBtn = new Button(ctx);
        xmlBtn.setText("\uD83D\uDCC4 Test XML Layout Inflation");
        xmlBtn.setTextSize(13);
        xmlBtn.setTextColor(SECONDARY);
        xmlBtn.setBackgroundColor(0xFFEEEEEE);
        xmlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { show(XmlTestHelper.testXmlInflation(ctx)); }
        });
        root.addView(xmlBtn);
        Button calcBtn = new Button(ctx);
        calcBtn.setText("\uD83E\uDDEE Test Calculator APK Layout");
        calcBtn.setTextSize(13);
        calcBtn.setTextColor(SECONDARY);
        calcBtn.setBackgroundColor(0xFFE3F2FD);
        calcBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { show(XmlTestHelper.testCalcLayout(ctx)); }
        });
        root.addView(calcBtn);
        menuView = root;
        Button helloBtn = new Button(ctx);
        helloBtn.setText("\uD83D\uDCF1 Test Hello APK (full standard Views)");
        helloBtn.setTextSize(13);
        helloBtn.setTextColor(WHITE);
        helloBtn.setBackgroundColor(GREEN);
        helloBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { show(XmlTestHelper.testApkLayout(ctx, "hello_layout.axml", "Hello APK")); }
        });
        root.addView(helloBtn);
        show(root);
        Button calcAppBtn = new Button(ctx);
        calcAppBtn.setText("\uD83E\uDDEE Run Calculator App (full APK)");
        calcAppBtn.setTextSize(14);
        calcAppBtn.setTextColor(WHITE);
        calcAppBtn.setBackground(roundRect(0xFF1565C0, 8));
        calcAppBtn.setPadding(dp(16), dp(12), dp(16), dp(12));
        calcAppBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { show(XmlTestHelper.loadCalculatorApp(ctx)); }
        });
        root.addView(calcAppBtn);
        Button realBtn = new Button(ctx);
        realBtn.setText("\uD83D\uDCF1 Inflate REAL Huawei Dialer Layout");
        realBtn.setTextSize(14);
        realBtn.setTextColor(WHITE);
        realBtn.setBackground(roundRect(0xFF6A1B9A, 8));
        realBtn.setPadding(dp(16), dp(12), dp(16), dp(12));
        realBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { show(XmlTestHelper.testApkLayout(ctx, "real_dialpad.axml", "Huawei Dialer (REAL APK)")); }
        });
        root.addView(realBtn);
        Button dialerBtn = new Button(ctx);
        dialerBtn.setText("\uD83D\uDCDE Dialer App (Full Functional)");
        dialerBtn.setTextSize(14);
        dialerBtn.setTextColor(WHITE);
        dialerBtn.setBackground(roundRect(0xFF1565C0, 8));
        dialerBtn.setPadding(dp(16), dp(12), dp(16), dp(12));
        dialerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                com.example.dialer.DialerEntry.launch(ctx);
            }
        });
        root.addView(dialerBtn);
    }

    static String activeCategory = "All";

    static void showFilteredMenu(String category) {
        activeCategory = category;

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        root.addView(topBar("\uD83C\uDF54 MockDonalds", false, true));

        // Category tabs
        LinearLayout tabs = new LinearLayout(ctx);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setPadding(dp(8), dp(8), dp(8), dp(4));
        tabs.setBackgroundColor(WHITE);
        final String[] cats = {"All", "Burgers", "Sides", "Drinks", "Desserts"};
        final String[] catEmoji = {"\uD83C\uDF7D", "\uD83C\uDF54", "\uD83C\uDF5F", "\uD83E\uDD64", "\uD83C\uDF70"};
        for (int i = 0; i < cats.length; i++) {
            final int idx = i;
            boolean active = cats[i].equals(category);
            TextView tab = new TextView(ctx);
            tab.setText(catEmoji[i] + " " + cats[i]);
            tab.setTextSize(12);
            tab.setPadding(dp(10), dp(6), dp(10), dp(6));
            tab.setBackground(roundRect(active ? RED : 0xFFEEEEEE, 16));
            tab.setTextColor(active ? Color.WHITE : SECONDARY);
            tab.setTypeface(Typeface.DEFAULT_BOLD);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tlp.setMargins(dp(3), 0, dp(3), 0);
            tab.setLayoutParams(tlp);
            tab.setGravity(Gravity.CENTER);
            tab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showFilteredMenu(cats[idx]); }
            });
            tabs.addView(tab);
        }
        root.addView(tabs);

        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));

        // Filtered items
        final List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem m : menu) {
            if ("All".equals(category) || m.category.equals(category)) filtered.add(m);
        }

        ListView list = new ListView(ctx);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setBackgroundColor(BG);
        list.setPadding(dp(8), dp(4), dp(8), dp(4));
        list.setAdapter(new BaseAdapter() {
            public int getCount() { return filtered.size(); }
            public Object getItem(int p) { return filtered.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                return menuCard(filtered.get(pos));
            }
        });
        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                showDetail(filtered.get(pos));
            }
        });
        root.addView(list, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        menuView = root;
        show(root);
    }

    static View menuCard(MenuItem item) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(4), dp(3), dp(4), dp(3));
        card.setLayoutParams(lp);
        card.setBackground(roundRectStroke(WHITE, 12, DIVIDER));
        card.setElevation(dp(1));

        // Emoji
        TextView em = new TextView(ctx);
        em.setText(emoji(item.category));
        em.setTextSize(28);
        em.setPadding(0, 0, dp(12), 0);
        card.addView(em);

        // Name + desc column
        LinearLayout info = new LinearLayout(ctx);
        info.setOrientation(LinearLayout.VERTICAL);
        info.addView(boldLabel(item.name, 16, DARK));
        TextView desc = label(item.description.length() > 40 ? item.description.substring(0, 40) + "..." : item.description, 12, SECONDARY);
        desc.setPadding(0, dp(2), 0, 0);
        info.addView(desc);
        card.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Price + add button
        LinearLayout priceCol = new LinearLayout(ctx);
        priceCol.setOrientation(LinearLayout.VERTICAL);
        priceCol.setGravity(Gravity.CENTER);
        priceCol.setPadding(dp(8), 0, 0, 0);
        priceCol.addView(boldLabel("$" + String.format("%.2f", item.price), 16, GREEN));

        // Quick add button
        TextView addBtn = new TextView(ctx);
        addBtn.setText("+");
        addBtn.setTextSize(18);
        addBtn.setTextColor(WHITE);
        addBtn.setTypeface(Typeface.DEFAULT_BOLD);
        addBtn.setGravity(Gravity.CENTER);
        addBtn.setBackground(roundRect(RED, 14));
        addBtn.setPadding(dp(10), dp(2), dp(10), dp(2));
        LinearLayout.LayoutParams abLp = new LinearLayout.LayoutParams(dp(32), dp(32));
        abLp.topMargin = dp(4);
        addBtn.setLayoutParams(abLp);
        final int itemId = item.id;
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cart.put(itemId, cart.containsKey(itemId) ? cart.get(itemId) + 1 : 1);
                showMenu(); // refresh to update cart badge
            }
        });
        priceCol.addView(addBtn);
        card.addView(priceCol);

        return card;
    }

    // ═══════════════════════════════════════════
    //  DETAIL SCREEN
    // ═══════════════════════════════════════════
    static void showDetail(final MenuItem item) {
        ScrollView scroll = new ScrollView(ctx);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);

        root.addView(topBar(item.name, true, true));

        // Hero
        LinearLayout hero = new LinearLayout(ctx);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER);
        hero.setPadding(dp(32), dp(40), dp(32), dp(32));
        hero.setBackgroundColor(ORANGE_BG);
        hero.addView(boldLabel(emoji(item.category), 72, DARK));
        TextView heroPrice = boldLabel("$" + String.format("%.2f", item.price), 36, GREEN);
        heroPrice.setPadding(0, dp(8), 0, 0);
        heroPrice.setGravity(Gravity.CENTER);
        hero.addView(heroPrice);
        root.addView(hero);

        // Description card
        LinearLayout descCard = new LinearLayout(ctx);
        descCard.setOrientation(LinearLayout.VERTICAL);
        descCard.setPadding(dp(20), dp(16), dp(20), dp(16));
        LinearLayout.LayoutParams dcLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dcLp.setMargins(dp(16), dp(16), dp(16), dp(8));
        descCard.setLayoutParams(dcLp);
        descCard.setBackground(roundRectStroke(WHITE, 12, DIVIDER));
        descCard.setElevation(dp(2));

        TextView descLabel = boldLabel("ABOUT THIS ITEM", 11, SECONDARY);
        descLabel.setLetterSpacing(0.15f);
        descCard.addView(descLabel);
        TextView descText = label(item.description, 15, DARK);
        descText.setPadding(0, dp(8), 0, dp(8));
        descText.setLineSpacing(dp(4), 1);
        descCard.addView(descText);
        descCard.addView(label("Category: " + item.category, 13, SECONDARY));
        root.addView(descCard);

        // Quantity selector
        LinearLayout qtyRow = new LinearLayout(ctx);
        qtyRow.setOrientation(LinearLayout.HORIZONTAL);
        qtyRow.setGravity(Gravity.CENTER);
        qtyRow.setPadding(dp(16), dp(16), dp(16), dp(8));

        final int[] qty = {1};
        final TextView qtyText = boldLabel("1", 22, DARK);
        qtyText.setPadding(dp(20), 0, dp(20), 0);

        Button minus = new Button(ctx);
        minus.setText("−");
        minus.setTextSize(20);
        minus.setTextColor(RED);
        minus.setBackground(roundRectStroke(WHITE, 20, RED));
        minus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (qty[0] > 1) { qty[0]--; qtyText.setText("" + qty[0]); }
            }
        });
        qtyRow.addView(minus, new LinearLayout.LayoutParams(dp(44), dp(44)));
        qtyRow.addView(qtyText);
        Button plus = new Button(ctx);
        plus.setText("+");
        plus.setTextSize(20);
        plus.setTextColor(WHITE);
        plus.setBackground(roundRect(RED, 20));
        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { qty[0]++; qtyText.setText("" + qty[0]); }
        });
        qtyRow.addView(plus, new LinearLayout.LayoutParams(dp(44), dp(44)));
        root.addView(qtyRow);

        // Add to Cart
        final MenuItem fItem = item;
        Button addBtn = actionButton(
            "\uD83D\uDED2  Add to Cart — $" + String.format("%.2f", item.price), RED, WHITE,
            new View.OnClickListener() {
                public void onClick(View v) {
                    int prev = cart.containsKey(fItem.id) ? cart.get(fItem.id) : 0;
                    cart.put(fItem.id, prev + qty[0]);
                    showMenu();
                }
            });
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(dp(16), dp(8), dp(16), dp(16));
        addBtn.setLayoutParams(btnLp);
        root.addView(addBtn);

        scroll.addView(root);
        show(scroll);
    }

    // ═══════════════════════════════════════════
    //  CART SCREEN
    // ═══════════════════════════════════════════
    static void showCart() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        root.addView(topBar("\uD83D\uDED2 Your Cart", true, false));

        if (cart.isEmpty()) {
            LinearLayout empty = new LinearLayout(ctx);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(32), dp(64), dp(32), dp(64));
            empty.addView(boldLabel("\uD83D\uDED2", 64, SECONDARY));
            TextView emptyText = boldLabel("Your cart is empty", 18, SECONDARY);
            emptyText.setPadding(0, dp(16), 0, dp(8));
            emptyText.setGravity(Gravity.CENTER);
            empty.addView(emptyText);
            empty.addView(label("Add items from the menu to get started", 14, SECONDARY));
            Button browseBtn = actionButton("Browse Menu", RED, WHITE, new View.OnClickListener() {
                public void onClick(View v) { showMenu(); }
            });
            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            blp.topMargin = dp(24);
            browseBtn.setLayoutParams(blp);
            empty.addView(browseBtn);
            root.addView(empty, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        } else {
            // Cart items
            ScrollView scroll = new ScrollView(ctx);
            LinearLayout itemsLayout = new LinearLayout(ctx);
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
            itemsLayout.setPadding(dp(12), dp(8), dp(12), dp(8));

            for (final Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                final MenuItem item = findItem(entry.getKey());
                if (item == null) continue;

                LinearLayout row = new LinearLayout(ctx);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(dp(14), dp(12), dp(14), dp(12));
                row.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.setMargins(dp(2), dp(3), dp(2), dp(3));
                row.setLayoutParams(rlp);
                row.setBackground(roundRectStroke(WHITE, 10, DIVIDER));

                row.addView(boldLabel(emoji(item.category), 24, DARK));

                LinearLayout info = new LinearLayout(ctx);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setPadding(dp(12), 0, 0, 0);
                info.addView(boldLabel(item.name, 15, DARK));
                info.addView(label("$" + String.format("%.2f", item.price) + " × " + entry.getValue(), 13, SECONDARY));
                row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                row.addView(boldLabel("$" + String.format("%.2f", item.price * entry.getValue()), 15, GREEN));

                // Remove
                TextView removeBtn = new TextView(ctx);
                removeBtn.setText("✕");
                removeBtn.setTextSize(16);
                removeBtn.setTextColor(RED);
                removeBtn.setPadding(dp(12), 0, 0, 0);
                removeBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        cart.remove(entry.getKey());
                        showCart();
                    }
                });
                row.addView(removeBtn);

                itemsLayout.addView(row);
            }
            scroll.addView(itemsLayout);
            root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            // Order summary
            LinearLayout summary = new LinearLayout(ctx);
            summary.setOrientation(LinearLayout.VERTICAL);
            summary.setBackgroundColor(WHITE);
            summary.setPadding(dp(20), dp(16), dp(20), dp(16));
            summary.setElevation(dp(4));

            LinearLayout totalRow = new LinearLayout(ctx);
            totalRow.setOrientation(LinearLayout.HORIZONTAL);
            totalRow.addView(boldLabel("Total", 18, DARK),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            totalRow.addView(boldLabel("$" + String.format("%.2f", getCartPrice()), 20, GREEN));
            summary.addView(totalRow);

            TextView itemCount = label(getCartTotal() + " item" + (getCartTotal() > 1 ? "s" : ""), 13, SECONDARY);
            itemCount.setPadding(0, dp(4), 0, dp(12));
            summary.addView(itemCount);

            Button checkoutBtn = actionButton(
                "Proceed to Checkout →", RED, WHITE,
                new View.OnClickListener() {
                    public void onClick(View v) { showCheckout(); }
                });
            LinearLayout.LayoutParams cblp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            checkoutBtn.setLayoutParams(cblp);
            summary.addView(checkoutBtn);
            root.addView(summary);
        }

        show(root);
    }

    // ═══════════════════════════════════════════
    //  CHECKOUT SCREEN
    // ═══════════════════════════════════════════
    static void showCheckout() {
        ScrollView scroll = new ScrollView(ctx);
        scroll.setBackgroundColor(BG);
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);

        // Top bar
        LinearLayout bar = new LinearLayout(ctx);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(RED);
        bar.setPadding(dp(8), dp(10), dp(16), dp(10));
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setElevation(dp(4));
        Button back = new Button(ctx);
        back.setText("←");
        back.setTextSize(22);
        back.setTextColor(Color.WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showCart(); }
        });
        bar.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        bar.addView(boldLabel("Checkout", 18, GOLD));
        root.addView(bar);

        // Order summary
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(16), dp(20), dp(16));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(dp(16), dp(16), dp(16), dp(8));
        card.setLayoutParams(clp);
        card.setBackground(roundRectStroke(WHITE, 12, DIVIDER));
        card.setElevation(dp(2));

        card.addView(boldLabel("ORDER SUMMARY", 12, SECONDARY));
        View divider = new View(ctx);
        divider.setBackgroundColor(DIVIDER);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        dlp.setMargins(0, dp(8), 0, dp(8));
        divider.setLayoutParams(dlp);
        card.addView(divider);

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            MenuItem item = findItem(entry.getKey());
            if (item == null) continue;
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, dp(4), 0, dp(4));
            row.addView(label(emoji(item.category) + " " + item.name + " ×" + entry.getValue(), 14, DARK),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(label("$" + String.format("%.2f", item.price * entry.getValue()), 14, DARK));
            card.addView(row);
        }

        View div2 = new View(ctx);
        div2.setBackgroundColor(DIVIDER);
        LinearLayout.LayoutParams d2lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        d2lp.setMargins(0, dp(12), 0, dp(8));
        div2.setLayoutParams(d2lp);
        card.addView(div2);

        LinearLayout totalRow = new LinearLayout(ctx);
        totalRow.setOrientation(LinearLayout.HORIZONTAL);
        totalRow.setPadding(0, dp(4), 0, 0);
        totalRow.addView(boldLabel("Total", 18, DARK),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        totalRow.addView(boldLabel("$" + String.format("%.2f", getCartPrice()), 22, GREEN));
        card.addView(totalRow);

        root.addView(card);

        // Place Order button
        Button placeBtn = actionButton(
            "\uD83C\uDF1F  Place Order", RED, WHITE,
            new View.OnClickListener() {
                public void onClick(View v) { placeOrder(); }
            });
        LinearLayout.LayoutParams pblp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pblp.setMargins(dp(16), dp(16), dp(16), dp(16));
        placeBtn.setLayoutParams(pblp);
        root.addView(placeBtn);

        scroll.addView(root);
        show(scroll);
    }

    // ═══════════════════════════════════════════
    //  ORDER CONFIRMATION
    // ═══════════════════════════════════════════
    static void placeOrder() {
        orderNumber++;
        final double total = getCartPrice();
        final int itemCount = getCartTotal();
        cart.clear();

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(WHITE);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(32), dp(64), dp(32), dp(32));

        // Success icon
        TextView check = new TextView(ctx);
        check.setText("✅");
        check.setTextSize(64);
        check.setGravity(Gravity.CENTER);
        root.addView(check);

        TextView title = boldLabel("Order Placed!", 28, DARK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(20), 0, dp(8));
        root.addView(title);

        TextView orderNum = label("Order #" + String.format("%04d", orderNumber), 18, SECONDARY);
        orderNum.setGravity(Gravity.CENTER);
        root.addView(orderNum);

        // Receipt card
        LinearLayout receipt = new LinearLayout(ctx);
        receipt.setOrientation(LinearLayout.VERTICAL);
        receipt.setGravity(Gravity.CENTER);
        receipt.setPadding(dp(24), dp(20), dp(24), dp(20));
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.setMargins(0, dp(24), 0, dp(24));
        receipt.setLayoutParams(rlp);
        receipt.setBackground(roundRectStroke(BG, 12, DIVIDER));

        receipt.addView(label(itemCount + " item" + (itemCount > 1 ? "s" : ""), 16, SECONDARY));
        TextView totalText = boldLabel("$" + String.format("%.2f", total), 32, GREEN);
        totalText.setPadding(0, dp(4), 0, dp(8));
        totalText.setGravity(Gravity.CENTER);
        receipt.addView(totalText);
        receipt.addView(label("Preparing your order...", 14, SECONDARY));
        root.addView(receipt);

        // Estimated time
        LinearLayout etaRow = new LinearLayout(ctx);
        etaRow.setGravity(Gravity.CENTER);
        etaRow.setPadding(0, 0, 0, dp(24));
        etaRow.addView(boldLabel("⏱ ", 16, DARK));
        etaRow.addView(label("Estimated: 5-10 minutes", 15, DARK));
        root.addView(etaRow);

        // New order button
        Button newOrder = actionButton("Start New Order", RED, WHITE, new View.OnClickListener() {
            public void onClick(View v) { showMenu(); }
        });
        LinearLayout.LayoutParams nolp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newOrder.setLayoutParams(nolp);
        root.addView(newOrder);

        show(root);
    }

    public List<MenuItem> getMenuItems() { return menu; }
}
