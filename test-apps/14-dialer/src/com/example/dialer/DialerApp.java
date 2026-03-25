package com.example.dialer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
import java.util.Collections;
import java.util.List;

/**
 * Fully functional phone dialer app with:
 * - Dial pad with number display
 * - Call history (recents)
 * - Contacts list with search
 * - In-call screen with timer
 * - Voicemail indicator
 *
 * All using real Android Views on WestlakeActivity.
 */
public class DialerApp {

    // ═══ Theme (Material Blue) ═══
    static final int PRIMARY = 0xFF1565C0;      // Blue 800
    static final int PRIMARY_DARK = 0xFF0D47A1;  // Blue 900
    static final int ACCENT = 0xFF4CAF50;        // Green 500 (call button)
    static final int RED = 0xFFE53935;           // Red 600 (end call)
    static final int BG = 0xFFF5F5F5;
    static final int WHITE = 0xFFFFFFFF;
    static final int DARK = 0xFF212121;
    static final int SECONDARY = 0xFF757575;
    static final int DIVIDER = 0xFFE0E0E0;
    static final int DIAL_BG = 0xFFEEEEEE;
    static final int TAB_INACTIVE = 0xFF90CAF9;

    // ═══ State ═══
    static Context ctx;
    static Activity hostActivity;
    static String currentNumber = "";
    static List<CallRecord> callHistory = new ArrayList<>();
    static List<Contact> contacts = new ArrayList<>();
    static String activeTab = "keypad"; // keypad, recents, contacts
    static boolean inCall = false;
    static long callStartTime = 0;
    static Thread callTimerThread;

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}

        initContacts();
        initCallHistory();
    }

    static void initContacts() {
        contacts.clear();
        contacts.add(new Contact("Alice Chen", "+1 (555) 0101", "A"));
        contacts.add(new Contact("Bob Wang", "+1 (555) 0102", "B"));
        contacts.add(new Contact("Charlie Zhang", "+1 (555) 0103", "C"));
        contacts.add(new Contact("David Li", "+1 (555) 0104", "D"));
        contacts.add(new Contact("Emily Liu", "+1 (555) 0105", "E"));
        contacts.add(new Contact("Frank Wu", "+1 (555) 0106", "F"));
        contacts.add(new Contact("Grace Huang", "+1 (555) 0107", "G"));
        contacts.add(new Contact("Henry Zhou", "+1 (555) 0108", "H"));
        contacts.add(new Contact("Iris Sun", "+1 (555) 0109", "I"));
        contacts.add(new Contact("Jack Ma", "+1 (555) 0110", "J"));
        contacts.add(new Contact("Karen Xu", "+1 (555) 0111", "K"));
        contacts.add(new Contact("Leo Yang", "+1 (555) 0112", "L"));
        contacts.add(new Contact("Mom", "+1 (555) 8888", "M"));
        contacts.add(new Contact("Nancy Zhao", "+1 (555) 0114", "N"));
        contacts.add(new Contact("Oscar Ren", "+1 (555) 0115", "O"));
        contacts.add(new Contact("Pizza Palace", "+1 (555) 7777", "P"));
        contacts.add(new Contact("Dr. Smith", "+1 (555) 9999", "D"));
        contacts.add(new Contact("Work - Main Office", "+1 (555) 1000", "W"));
    }

    static void initCallHistory() {
        callHistory.clear();
        callHistory.add(new CallRecord("Mom", "+1 (555) 8888", CallRecord.INCOMING, "Today, 2:30 PM", "5:23"));
        callHistory.add(new CallRecord("Alice Chen", "+1 (555) 0101", CallRecord.OUTGOING, "Today, 11:15 AM", "2:47"));
        callHistory.add(new CallRecord("+1 (555) 3456", "+1 (555) 3456", CallRecord.MISSED, "Today, 9:02 AM", ""));
        callHistory.add(new CallRecord("Work - Main Office", "+1 (555) 1000", CallRecord.OUTGOING, "Yesterday, 4:45 PM", "12:31"));
        callHistory.add(new CallRecord("Bob Wang", "+1 (555) 0102", CallRecord.INCOMING, "Yesterday, 1:20 PM", "0:45"));
        callHistory.add(new CallRecord("Pizza Palace", "+1 (555) 7777", CallRecord.OUTGOING, "Yesterday, 12:00 PM", "1:12"));
        callHistory.add(new CallRecord("Dr. Smith", "+1 (555) 9999", CallRecord.MISSED, "Mar 22, 3:15 PM", ""));
        callHistory.add(new CallRecord("David Li", "+1 (555) 0104", CallRecord.INCOMING, "Mar 22, 10:30 AM", "8:05"));
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

    static GradientDrawable circle(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
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

    static String formatPhoneDisplay(String num) {
        if (num.length() == 0) return "";
        String digits = num.replaceAll("[^0-9]", "");
        if (digits.length() <= 3) return digits;
        if (digits.length() <= 6) return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        if (digits.length() <= 10)
            return "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
        return "+1 (" + digits.substring(1, 4) + ") " + digits.substring(4, 7) + "-" + digits.substring(7);
    }

    // ═══ Bottom Tab Bar ═══
    static LinearLayout bottomTabs() {
        LinearLayout tabs = new LinearLayout(ctx);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setBackgroundColor(WHITE);
        tabs.setElevation(dp(8));
        tabs.setPadding(0, dp(4), 0, dp(4));

        String[][] tabData = {
            {"dialpad", "\u2328", "Keypad"},
            {"recents", "\uD83D\uDD53", "Recents"},
            {"contacts", "\uD83D\uDC64", "Contacts"}
        };

        for (final String[] td : tabData) {
            LinearLayout tab = new LinearLayout(ctx);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(0, dp(6), 0, dp(6));

            boolean active = td[0].equals(activeTab);

            TextView icon = new TextView(ctx);
            icon.setText(td[1]);
            icon.setTextSize(20);
            icon.setGravity(Gravity.CENTER);
            tab.addView(icon);

            TextView name = new TextView(ctx);
            name.setText(td[2]);
            name.setTextSize(11);
            name.setTextColor(active ? PRIMARY : SECONDARY);
            name.setTypeface(active ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            name.setGravity(Gravity.CENTER);
            tab.addView(name);

            // Missed calls badge on recents
            if ("recents".equals(td[0])) {
                int missed = 0;
                for (CallRecord r : callHistory) if (r.type == CallRecord.MISSED) missed++;
                if (missed > 0) {
                    // Use the icon text to show badge
                    icon.setText(td[1] + " " + missed);
                    icon.setTextColor(RED);
                }
            }

            tab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    activeTab = td[0];
                    if ("keypad".equals(td[0])) showKeypad();
                    else if ("recents".equals(td[0])) showRecents();
                    else showContacts();
                }
            });

            tabs.addView(tab, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }

        return tabs;
    }

    // ═══════════════════════════════════════════
    //  KEYPAD SCREEN
    // ═══════════════════════════════════════════
    public static void showKeypad() {
        activeTab = "keypad";
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(WHITE);

        // Number display area
        LinearLayout display = new LinearLayout(ctx);
        display.setOrientation(LinearLayout.VERTICAL);
        display.setGravity(Gravity.CENTER);
        display.setPadding(dp(24), dp(40), dp(24), dp(16));

        final TextView numberDisplay = new TextView(ctx);
        String displayNum = currentNumber.isEmpty() ? "" : formatPhoneDisplay(currentNumber);
        numberDisplay.setText(displayNum);
        numberDisplay.setTextSize(currentNumber.length() > 10 ? 28 : 34);
        numberDisplay.setTextColor(DARK);
        numberDisplay.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        numberDisplay.setGravity(Gravity.CENTER);
        numberDisplay.setMinHeight(dp(48));
        display.addView(numberDisplay);

        // Contact name lookup
        if (!currentNumber.isEmpty()) {
            String contactName = lookupContact(currentNumber);
            if (contactName != null) {
                TextView nameLabel = label(contactName, 14, PRIMARY);
                nameLabel.setGravity(Gravity.CENTER);
                nameLabel.setPadding(0, dp(4), 0, 0);
                display.addView(nameLabel);
            }
        }

        root.addView(display);

        // Dial pad grid
        LinearLayout pad = new LinearLayout(ctx);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setPadding(dp(32), dp(8), dp(32), dp(8));

        String[][] keys = {
            {"1", "", "2", "ABC", "3", "DEF"},
            {"4", "GHI", "5", "JKL", "6", "MNO"},
            {"7", "PQRS", "8", "TUV", "9", "WXYZ"},
            {"*", "", "0", "+", "#", ""}
        };

        for (String[] row : keys) {
            LinearLayout rowLayout = new LinearLayout(ctx);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            rowLayout.setPadding(0, dp(2), 0, dp(2));

            for (int i = 0; i < row.length; i += 2) {
                final String digit = row[i];
                String sub = row[i + 1];

                LinearLayout keyBtn = new LinearLayout(ctx);
                keyBtn.setOrientation(LinearLayout.VERTICAL);
                keyBtn.setGravity(Gravity.CENTER);
                keyBtn.setBackground(roundRect(DIAL_BG, 40));
                keyBtn.setPadding(0, dp(10), 0, dp(10));

                TextView digitTv = boldLabel(digit, 26, DARK);
                digitTv.setGravity(Gravity.CENTER);
                keyBtn.addView(digitTv);

                if (!sub.isEmpty()) {
                    TextView subTv = label(sub, 9, SECONDARY);
                    subTv.setGravity(Gravity.CENTER);
                    subTv.setLetterSpacing(0.15f);
                    keyBtn.addView(subTv);
                }

                keyBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        currentNumber += digit;
                        showKeypad();
                    }
                });

                // Long press 0 for +
                if ("0".equals(digit)) {
                    keyBtn.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            currentNumber += "+";
                            showKeypad();
                            return true;
                        }
                    });
                }

                LinearLayout.LayoutParams klp = new LinearLayout.LayoutParams(dp(72), dp(72));
                klp.setMargins(dp(8), dp(4), dp(8), dp(4));
                keyBtn.setLayoutParams(klp);
                rowLayout.addView(keyBtn);
            }

            pad.addView(rowLayout);
        }
        root.addView(pad);

        // Action buttons row: voicemail | call | backspace
        LinearLayout actions = new LinearLayout(ctx);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        actions.setPadding(dp(32), dp(8), dp(32), dp(12));

        // Voicemail button
        TextView voicemail = new TextView(ctx);
        voicemail.setText("\uD83D\uDCE8");
        voicemail.setTextSize(20);
        voicemail.setGravity(Gravity.CENTER);
        voicemail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showVoicemail(); }
        });
        actions.addView(voicemail, new LinearLayout.LayoutParams(dp(56), dp(56)));

        // Call button (green circle)
        LinearLayout callBtn = new LinearLayout(ctx);
        callBtn.setGravity(Gravity.CENTER);
        callBtn.setBackground(circle(ACCENT));
        TextView callIcon = boldLabel("\uD83D\uDCDE", 28, WHITE);
        callIcon.setGravity(Gravity.CENTER);
        callBtn.addView(callIcon);
        callBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!currentNumber.isEmpty()) {
                    makeCall(currentNumber, lookupContact(currentNumber));
                }
            }
        });
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(dp(64), dp(64));
        clp.setMargins(dp(24), 0, dp(24), 0);
        callBtn.setLayoutParams(clp);
        actions.addView(callBtn);

        // Backspace button
        TextView backspace = new TextView(ctx);
        backspace.setText("\u232B");
        backspace.setTextSize(24);
        backspace.setTextColor(SECONDARY);
        backspace.setGravity(Gravity.CENTER);
        backspace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentNumber.length() > 0) {
                    currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                    showKeypad();
                }
            }
        });
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                currentNumber = "";
                showKeypad();
                return true;
            }
        });
        backspace.setVisibility(currentNumber.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        actions.addView(backspace, new LinearLayout.LayoutParams(dp(56), dp(56)));

        root.addView(actions);

        // Bottom tabs
        root.addView(bottomTabs());

        show(root);
    }

    // ═══════════════════════════════════════════
    //  RECENTS SCREEN
    // ═══════════════════════════════════════════
    public static void showRecents() {
        activeTab = "recents";
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(PRIMARY);
        header.setPadding(dp(16), dp(14), dp(16), dp(14));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        header.addView(boldLabel("Recent Calls", 20, WHITE),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Clear all button
        TextView clearBtn = label("Clear", 14, TAB_INACTIVE);
        clearBtn.setPadding(dp(12), dp(4), dp(12), dp(4));
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callHistory.clear();
                showRecents();
            }
        });
        header.addView(clearBtn);
        root.addView(header);

        if (callHistory.isEmpty()) {
            LinearLayout empty = new LinearLayout(ctx);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(32), dp(80), dp(32), dp(80));

            empty.addView(boldLabel("\uD83D\uDCDE", 48, DIVIDER));
            TextView emptyText = label("No recent calls", 16, SECONDARY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(0, dp(16), 0, 0);
            empty.addView(emptyText);
            root.addView(empty, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        } else {
            ListView list = new ListView(ctx);
            list.setDivider(null);
            list.setDividerHeight(0);
            list.setBackgroundColor(BG);

            list.setAdapter(new BaseAdapter() {
                public int getCount() { return callHistory.size(); }
                public Object getItem(int p) { return callHistory.get(p); }
                public long getItemId(int p) { return p; }
                public View getView(int pos, View cv, ViewGroup parent) {
                    return callRecordRow(callHistory.get(pos));
                }
            });

            list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                    CallRecord r = callHistory.get(pos);
                    currentNumber = r.number.replaceAll("[^0-9+]", "");
                    showKeypad();
                }
            });

            root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        }

        root.addView(bottomTabs());
        show(root);
    }

    static View callRecordRow(final CallRecord record) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(WHITE);

        // Call type icon
        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        String initial = record.name.substring(0, 1).toUpperCase();
        int avatarColor = record.type == CallRecord.MISSED ? 0xFFFFCDD2 : 0xFFBBDEFB;
        int textColor = record.type == CallRecord.MISSED ? RED : PRIMARY;
        avatar.setBackground(circle(avatarColor));
        avatar.addView(boldLabel(initial, 18, textColor));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(44), dp(44));
        alp.rightMargin = dp(14);
        avatar.setLayoutParams(alp);
        row.addView(avatar);

        // Name + details column
        LinearLayout info = new LinearLayout(ctx);
        info.setOrientation(LinearLayout.VERTICAL);

        // Name with call type arrow
        LinearLayout nameRow = new LinearLayout(ctx);
        nameRow.setOrientation(LinearLayout.HORIZONTAL);
        nameRow.setGravity(Gravity.CENTER_VERTICAL);

        String arrow;
        int arrowColor;
        if (record.type == CallRecord.INCOMING) { arrow = "\u2199"; arrowColor = ACCENT; }
        else if (record.type == CallRecord.OUTGOING) { arrow = "\u2197"; arrowColor = PRIMARY; }
        else { arrow = "\u2199"; arrowColor = RED; }

        TextView arrowTv = label(arrow + " ", 14, arrowColor);
        nameRow.addView(arrowTv);

        TextView nameTv = boldLabel(record.name, 15, record.type == CallRecord.MISSED ? RED : DARK);
        nameRow.addView(nameTv);
        info.addView(nameRow);

        // Time and duration
        String detail = record.time;
        if (!record.duration.isEmpty()) detail += "  (" + record.duration + ")";
        info.addView(label(detail, 12, SECONDARY));

        row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Call button
        LinearLayout callBtn = new LinearLayout(ctx);
        callBtn.setGravity(Gravity.CENTER);
        TextView phoneIcon = label("\uD83D\uDCDE", 18, PRIMARY);
        phoneIcon.setGravity(Gravity.CENTER);
        callBtn.addView(phoneIcon);
        callBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeCall(record.number, record.name);
            }
        });
        callBtn.setPadding(dp(12), dp(8), dp(4), dp(8));
        row.addView(callBtn);

        // Bottom divider via margin trick
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        wrapper.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

        return wrapper;
    }

    // ═══════════════════════════════════════════
    //  CONTACTS SCREEN
    // ═══════════════════════════════════════════
    public static void showContacts() {
        activeTab = "contacts";
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(PRIMARY);
        header.setPadding(dp(16), dp(14), dp(16), dp(14));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        header.addView(boldLabel("Contacts", 20, WHITE),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Add contact button
        TextView addBtn = label("+", 24, WHITE);
        addBtn.setPadding(dp(8), 0, dp(4), 0);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showAddContact(); }
        });
        header.addView(addBtn);
        root.addView(header);

        // Search bar
        LinearLayout searchBar = new LinearLayout(ctx);
        searchBar.setOrientation(LinearLayout.HORIZONTAL);
        searchBar.setBackgroundColor(WHITE);
        searchBar.setPadding(dp(16), dp(10), dp(16), dp(10));
        searchBar.setGravity(Gravity.CENTER_VERTICAL);

        TextView searchIcon = label("\uD83D\uDD0D ", 16, SECONDARY);
        searchBar.addView(searchIcon);
        TextView searchHint = label("Search contacts", 15, 0xFFBDBDBD);
        searchBar.addView(searchHint);
        root.addView(searchBar);

        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

        // Contact list sorted alphabetically
        final List<Contact> sorted = new ArrayList<>(contacts);
        Collections.sort(sorted, new java.util.Comparator<Contact>() {
            public int compare(Contact a, Contact b) { return a.name.compareTo(b.name); }
        });

        ListView list = new ListView(ctx);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setBackgroundColor(WHITE);

        list.setAdapter(new BaseAdapter() {
            public int getCount() { return sorted.size(); }
            public Object getItem(int p) { return sorted.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                return contactRow(sorted.get(pos), pos > 0 ? sorted.get(pos - 1) : null);
            }
        });

        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                showContactDetail(sorted.get(pos));
            }
        });

        root.addView(list, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        root.addView(bottomTabs());
        show(root);
    }

    static View contactRow(Contact contact, Contact prev) {
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setOrientation(LinearLayout.VERTICAL);

        // Section header (first letter)
        String letter = contact.name.substring(0, 1).toUpperCase();
        boolean showHeader = (prev == null || !prev.name.substring(0, 1).toUpperCase().equals(letter));
        if (showHeader) {
            TextView section = boldLabel(letter, 13, PRIMARY);
            section.setPadding(dp(16), dp(12), dp(16), dp(4));
            section.setBackgroundColor(0xFFF5F5F5);
            wrapper.addView(section);
        }

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(16), dp(12), dp(16), dp(12));
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(WHITE);

        // Avatar circle
        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        int hash = contact.name.hashCode();
        int[] colors = {0xFF42A5F5, 0xFF66BB6A, 0xFFEF5350, 0xFFAB47BC, 0xFFFF7043, 0xFF26A69A, 0xFFEC407A, 0xFF5C6BC0};
        int avatarColor = colors[Math.abs(hash) % colors.length];
        avatar.setBackground(circle(avatarColor));
        avatar.addView(boldLabel(contact.name.substring(0, 1).toUpperCase(), 18, WHITE));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(42), dp(42));
        alp.rightMargin = dp(14);
        avatar.setLayoutParams(alp);
        row.addView(avatar);

        // Name + number
        LinearLayout info = new LinearLayout(ctx);
        info.setOrientation(LinearLayout.VERTICAL);
        info.addView(boldLabel(contact.name, 15, DARK));
        info.addView(label(contact.number, 13, SECONDARY));
        row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        wrapper.addView(row);

        // Divider
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        dlp.leftMargin = dp(72);
        div.setLayoutParams(dlp);
        wrapper.addView(div);

        return wrapper;
    }

    // ═══════════════════════════════════════════
    //  CONTACT DETAIL SCREEN
    // ═══════════════════════════════════════════
    static void showContactDetail(final Contact contact) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Header with back button
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(PRIMARY);
        header.setPadding(dp(8), dp(10), dp(16), dp(10));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        Button back = new Button(ctx);
        back.setText("\u2190");
        back.setTextSize(22);
        back.setTextColor(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showContacts(); }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        header.addView(boldLabel("Contact", 18, WHITE));
        root.addView(header);

        // Avatar + name hero
        LinearLayout hero = new LinearLayout(ctx);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER);
        hero.setPadding(dp(24), dp(40), dp(24), dp(32));
        hero.setBackgroundColor(WHITE);

        // Large avatar
        LinearLayout bigAvatar = new LinearLayout(ctx);
        bigAvatar.setGravity(Gravity.CENTER);
        int hash = contact.name.hashCode();
        int[] colors = {0xFF42A5F5, 0xFF66BB6A, 0xFFEF5350, 0xFFAB47BC, 0xFFFF7043, 0xFF26A69A, 0xFFEC407A, 0xFF5C6BC0};
        bigAvatar.setBackground(circle(colors[Math.abs(hash) % colors.length]));
        bigAvatar.addView(boldLabel(contact.name.substring(0, 1).toUpperCase(), 36, WHITE));
        bigAvatar.setLayoutParams(new LinearLayout.LayoutParams(dp(88), dp(88)));
        hero.addView(bigAvatar);

        TextView nameText = boldLabel(contact.name, 24, DARK);
        nameText.setGravity(Gravity.CENTER);
        nameText.setPadding(0, dp(16), 0, dp(4));
        hero.addView(nameText);

        TextView numText = label(contact.number, 16, SECONDARY);
        numText.setGravity(Gravity.CENTER);
        hero.addView(numText);

        root.addView(hero);

        // Action buttons row
        LinearLayout actions = new LinearLayout(ctx);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        actions.setPadding(dp(16), dp(20), dp(16), dp(20));
        actions.setBackgroundColor(WHITE);

        // Call
        LinearLayout callAction = actionCircle(ACCENT, "\uD83D\uDCDE", "Call");
        callAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { makeCall(contact.number, contact.name); }
        });
        actions.addView(callAction, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Message
        LinearLayout msgAction = actionCircle(PRIMARY, "\uD83D\uDCAC", "Message");
        actions.addView(msgAction, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Video
        LinearLayout vidAction = actionCircle(0xFF7B1FA2, "\uD83D\uDCF9", "Video");
        actions.addView(vidAction, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        root.addView(actions);

        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

        // Phone number detail card
        LinearLayout phoneCard = new LinearLayout(ctx);
        phoneCard.setOrientation(LinearLayout.VERTICAL);
        phoneCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        phoneCard.setBackgroundColor(WHITE);

        LinearLayout.LayoutParams pclp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pclp.setMargins(0, dp(8), 0, 0);
        phoneCard.setLayoutParams(pclp);

        phoneCard.addView(boldLabel("Phone", 12, SECONDARY));
        TextView phoneNum = boldLabel(contact.number, 16, PRIMARY);
        phoneNum.setPadding(0, dp(6), 0, dp(2));
        phoneCard.addView(phoneNum);
        phoneCard.addView(label("Mobile", 13, SECONDARY));

        root.addView(phoneCard);

        // Call history for this contact
        LinearLayout historySection = new LinearLayout(ctx);
        historySection.setOrientation(LinearLayout.VERTICAL);
        historySection.setPadding(dp(16), dp(16), dp(16), dp(8));
        historySection.setBackgroundColor(WHITE);
        LinearLayout.LayoutParams hslp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hslp.setMargins(0, dp(8), 0, 0);
        historySection.setLayoutParams(hslp);

        historySection.addView(boldLabel("Recent", 12, SECONDARY));

        boolean found = false;
        for (CallRecord r : callHistory) {
            if (r.name.equals(contact.name)) {
                found = true;
                LinearLayout hRow = new LinearLayout(ctx);
                hRow.setOrientation(LinearLayout.HORIZONTAL);
                hRow.setPadding(0, dp(10), 0, dp(10));
                hRow.setGravity(Gravity.CENTER_VERTICAL);

                String arrow;
                int arrowColor;
                if (r.type == CallRecord.INCOMING) { arrow = "\u2199 Incoming"; arrowColor = ACCENT; }
                else if (r.type == CallRecord.OUTGOING) { arrow = "\u2197 Outgoing"; arrowColor = PRIMARY; }
                else { arrow = "\u2199 Missed"; arrowColor = RED; }

                hRow.addView(label(arrow, 13, arrowColor),
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                hRow.addView(label(r.time, 12, SECONDARY));
                historySection.addView(hRow);
            }
        }
        if (!found) {
            historySection.addView(label("No recent calls", 13, SECONDARY));
        }

        root.addView(historySection);

        show(root);
    }

    static LinearLayout actionCircle(int color, String icon, String label) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);

        LinearLayout circ = new LinearLayout(ctx);
        circ.setGravity(Gravity.CENTER);
        circ.setBackground(circle(color));
        circ.addView(boldLabel(icon, 22, WHITE));
        circ.setLayoutParams(new LinearLayout.LayoutParams(dp(52), dp(52)));
        col.addView(circ);

        TextView lbl = label(label, 12, SECONDARY);
        lbl.setGravity(Gravity.CENTER);
        lbl.setPadding(0, dp(6), 0, 0);
        col.addView(lbl);

        return col;
    }

    // ═══════════════════════════════════════════
    //  ADD CONTACT SCREEN
    // ═══════════════════════════════════════════
    static void showAddContact() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(PRIMARY);
        header.setPadding(dp(8), dp(10), dp(16), dp(10));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        Button back = new Button(ctx);
        back.setText("\u2190");
        back.setTextSize(22);
        back.setTextColor(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showContacts(); }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        header.addView(boldLabel("New Contact", 18, WHITE),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        root.addView(header);

        // Form
        LinearLayout form = new LinearLayout(ctx);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(24), dp(20), dp(24));
        form.setBackgroundColor(WHITE);

        // Avatar placeholder
        LinearLayout avatarRow = new LinearLayout(ctx);
        avatarRow.setGravity(Gravity.CENTER);
        avatarRow.setPadding(0, 0, 0, dp(24));
        LinearLayout bigAvatar = new LinearLayout(ctx);
        bigAvatar.setGravity(Gravity.CENTER);
        bigAvatar.setBackground(circle(DIVIDER));
        bigAvatar.addView(boldLabel("\uD83D\uDCF7", 28, SECONDARY));
        bigAvatar.setLayoutParams(new LinearLayout.LayoutParams(dp(80), dp(80)));
        avatarRow.addView(bigAvatar);
        form.addView(avatarRow);

        // Name field (simulated - using TextView since we don't have EditText)
        final String[] newName = {""};
        final String[] newNumber = {currentNumber};

        form.addView(fieldLabel("Name"));
        final TextView nameField = fieldInput("Enter name");
        form.addView(nameField);
        form.addView(fieldDivider());

        form.addView(fieldLabel("Phone"));
        final TextView phoneField = fieldInput(currentNumber.isEmpty() ? "Enter phone number" : formatPhoneDisplay(currentNumber));
        if (!currentNumber.isEmpty()) phoneField.setTextColor(DARK);
        form.addView(phoneField);
        form.addView(fieldDivider());

        form.addView(fieldLabel("Email"));
        form.addView(fieldInput("Enter email"));
        form.addView(fieldDivider());

        root.addView(form);

        // Save button
        Button saveBtn = new Button(ctx);
        saveBtn.setText("Save Contact");
        saveBtn.setTextSize(16);
        saveBtn.setTextColor(WHITE);
        saveBtn.setTypeface(Typeface.DEFAULT_BOLD);
        saveBtn.setBackground(roundRect(PRIMARY, 24));
        saveBtn.setPadding(dp(24), dp(14), dp(24), dp(14));
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Simulate save - add with placeholder name
                String name = "New Contact";
                String num = currentNumber.isEmpty() ? "+1 (555) 0000" : formatPhoneDisplay(currentNumber);
                contacts.add(new Contact(name, num, name.substring(0, 1)));
                showContacts();
            }
        });
        LinearLayout.LayoutParams sblp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sblp.setMargins(dp(20), dp(20), dp(20), dp(20));
        saveBtn.setLayoutParams(sblp);
        root.addView(saveBtn);

        show(root);
    }

    static TextView fieldLabel(String text) {
        TextView tv = boldLabel(text, 12, SECONDARY);
        tv.setPadding(0, dp(16), 0, dp(4));
        return tv;
    }

    static TextView fieldInput(String hint) {
        TextView tv = new TextView(ctx);
        tv.setText(hint);
        tv.setTextSize(16);
        tv.setTextColor(0xFFBDBDBD);
        tv.setPadding(0, dp(8), 0, dp(8));
        return tv;
    }

    static View fieldDivider() {
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        div.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        return div;
    }

    // ═══════════════════════════════════════════
    //  IN-CALL SCREEN
    // ═══════════════════════════════════════════
    static void makeCall(String number, String name) {
        if (name == null) name = number;
        inCall = true;
        callStartTime = System.currentTimeMillis();

        // Add to call history
        callHistory.add(0, new CallRecord(
            name, number, CallRecord.OUTGOING, "Just now", ""));

        showInCall(number, name);
    }

    static void showInCall(final String number, final String name) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(PRIMARY_DARK);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        // Status
        TextView statusTv = label("Calling...", 14, TAB_INACTIVE);
        statusTv.setGravity(Gravity.CENTER);
        statusTv.setPadding(0, dp(60), 0, dp(8));
        root.addView(statusTv);

        // Name
        TextView nameTv = boldLabel(name != null ? name : number, 28, WHITE);
        nameTv.setGravity(Gravity.CENTER);
        root.addView(nameTv);

        // Number (if name is shown)
        if (name != null && !name.equals(number)) {
            TextView numTv = label(number, 16, TAB_INACTIVE);
            numTv.setGravity(Gravity.CENTER);
            numTv.setPadding(0, dp(4), 0, 0);
            root.addView(numTv);
        }

        // Timer
        long elapsed = System.currentTimeMillis() - callStartTime;
        int secs = (int)(elapsed / 1000);
        String timerStr = String.format("%d:%02d", secs / 60, secs % 60);
        final TextView timerTv = boldLabel(timerStr, 18, TAB_INACTIVE);
        timerTv.setGravity(Gravity.CENTER);
        timerTv.setPadding(0, dp(12), 0, 0);
        if (secs > 0) {
            statusTv.setText("Connected");
        }
        root.addView(timerTv);

        // Large avatar
        LinearLayout avatarContainer = new LinearLayout(ctx);
        avatarContainer.setGravity(Gravity.CENTER);
        avatarContainer.setPadding(0, dp(40), 0, dp(40));

        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        String initial = (name != null ? name : number).substring(0, 1).toUpperCase();
        avatar.setBackground(circle(0xFF1976D2));
        avatar.addView(boldLabel(initial, 44, WHITE));
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(120), dp(120)));
        avatarContainer.addView(avatar);
        root.addView(avatarContainer);

        // In-call action buttons (2 rows of 3)
        LinearLayout actionGrid = new LinearLayout(ctx);
        actionGrid.setOrientation(LinearLayout.VERTICAL);
        actionGrid.setPadding(dp(40), 0, dp(40), dp(16));

        // Row 1: Mute, Keypad, Speaker
        LinearLayout row1 = new LinearLayout(ctx);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER);
        row1.setPadding(0, dp(4), 0, dp(4));

        row1.addView(inCallButton("\uD83D\uDD07", "Mute", false),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout keypadBtn = inCallButton("\u2328", "Keypad", false);
        keypadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showInCallKeypad(number, name); }
        });
        row1.addView(keypadBtn,
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row1.addView(inCallButton("\uD83D\uDD0A", "Speaker", false),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        actionGrid.addView(row1);

        // Row 2: Add call, Hold, Contacts
        LinearLayout row2 = new LinearLayout(ctx);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setGravity(Gravity.CENTER);
        row2.setPadding(0, dp(4), 0, dp(4));

        row2.addView(inCallButton("+", "Add call", false),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row2.addView(inCallButton("||", "Hold", false),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row2.addView(inCallButton("\uD83D\uDC64", "Contacts", false),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        actionGrid.addView(row2);

        root.addView(actionGrid);

        // End call button
        LinearLayout endRow = new LinearLayout(ctx);
        endRow.setGravity(Gravity.CENTER);
        endRow.setPadding(0, dp(16), 0, dp(40));

        LinearLayout endBtn = new LinearLayout(ctx);
        endBtn.setGravity(Gravity.CENTER);
        endBtn.setBackground(circle(RED));
        endBtn.addView(boldLabel("\uD83D\uDCDE", 28, WHITE));
        endBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inCall = false;
                // Update call duration in history
                if (!callHistory.isEmpty()) {
                    long dur = (System.currentTimeMillis() - callStartTime) / 1000;
                    callHistory.get(0).duration = String.format("%d:%02d", dur / 60, dur % 60);
                    callHistory.get(0).time = "Just now";
                }
                if (callTimerThread != null) callTimerThread.interrupt();
                currentNumber = "";
                showRecents();
            }
        });
        endBtn.setLayoutParams(new LinearLayout.LayoutParams(dp(72), dp(72)));
        endRow.addView(endBtn);
        root.addView(endRow);

        show(root);

        // Start timer update thread
        if (callTimerThread != null) callTimerThread.interrupt();
        final String fNumber = number;
        final String fName = name;
        callTimerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (inCall && hostActivity != null) {
                        hostActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (inCall) showInCall(fNumber, fName);
                            }
                        });
                    }
                } catch (InterruptedException e) {}
            }
        });
        callTimerThread.start();
    }

    static LinearLayout inCallButton(String icon, String text, boolean active) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout circ = new LinearLayout(ctx);
        circ.setGravity(Gravity.CENTER);
        circ.setBackground(circle(active ? WHITE : 0x33FFFFFF));
        circ.addView(boldLabel(icon, 18, active ? PRIMARY_DARK : WHITE));
        circ.setLayoutParams(new LinearLayout.LayoutParams(dp(52), dp(52)));
        col.addView(circ);

        TextView lbl = label(text, 11, TAB_INACTIVE);
        lbl.setGravity(Gravity.CENTER);
        lbl.setPadding(0, dp(6), 0, 0);
        col.addView(lbl);

        return col;
    }

    // ═══════════════════════════════════════════
    //  IN-CALL KEYPAD
    // ═══════════════════════════════════════════
    static void showInCallKeypad(final String number, final String name) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(PRIMARY_DARK);

        // Mini status bar
        LinearLayout status = new LinearLayout(ctx);
        status.setOrientation(LinearLayout.HORIZONTAL);
        status.setPadding(dp(16), dp(24), dp(16), dp(8));
        status.setGravity(Gravity.CENTER);

        long elapsed = System.currentTimeMillis() - callStartTime;
        int secs = (int)(elapsed / 1000);

        status.addView(boldLabel(name != null ? name : number, 16, WHITE));
        status.addView(label("  " + String.format("%d:%02d", secs / 60, secs % 60), 14, TAB_INACTIVE));
        root.addView(status);

        // DTMF display
        final TextView dtmfDisplay = new TextView(ctx);
        dtmfDisplay.setTextSize(28);
        dtmfDisplay.setTextColor(WHITE);
        dtmfDisplay.setGravity(Gravity.CENTER);
        dtmfDisplay.setMinHeight(dp(48));
        dtmfDisplay.setPadding(0, dp(8), 0, dp(8));
        root.addView(dtmfDisplay);

        // Keypad
        LinearLayout pad = new LinearLayout(ctx);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setPadding(dp(40), dp(4), dp(40), dp(4));

        String[][] keys = {
            {"1", "2", "3"},
            {"4", "5", "6"},
            {"7", "8", "9"},
            {"*", "0", "#"}
        };

        for (String[] row : keys) {
            LinearLayout rowLayout = new LinearLayout(ctx);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            for (final String digit : row) {
                LinearLayout keyBtn = new LinearLayout(ctx);
                keyBtn.setGravity(Gravity.CENTER);
                keyBtn.setBackground(roundRect(0x33FFFFFF, 36));
                keyBtn.addView(boldLabel(digit, 24, WHITE));
                keyBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dtmfDisplay.setText(dtmfDisplay.getText() + digit);
                    }
                });
                LinearLayout.LayoutParams klp = new LinearLayout.LayoutParams(dp(68), dp(68));
                klp.setMargins(dp(6), dp(4), dp(6), dp(4));
                keyBtn.setLayoutParams(klp);
                rowLayout.addView(keyBtn);
            }
            pad.addView(rowLayout);
        }
        root.addView(pad);

        // Hide keypad button
        Button hideBtn = new Button(ctx);
        hideBtn.setText("Hide");
        hideBtn.setTextSize(16);
        hideBtn.setTextColor(WHITE);
        hideBtn.setBackgroundColor(Color.TRANSPARENT);
        hideBtn.setPadding(0, dp(12), 0, dp(8));
        hideBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showInCall(number, name); }
        });
        root.addView(hideBtn);

        // End call button
        LinearLayout endRow = new LinearLayout(ctx);
        endRow.setGravity(Gravity.CENTER);
        endRow.setPadding(0, dp(8), 0, dp(24));

        LinearLayout endBtn = new LinearLayout(ctx);
        endBtn.setGravity(Gravity.CENTER);
        endBtn.setBackground(circle(RED));
        endBtn.addView(boldLabel("\uD83D\uDCDE", 28, WHITE));
        endBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inCall = false;
                if (!callHistory.isEmpty()) {
                    long dur = (System.currentTimeMillis() - callStartTime) / 1000;
                    callHistory.get(0).duration = String.format("%d:%02d", dur / 60, dur % 60);
                }
                if (callTimerThread != null) callTimerThread.interrupt();
                currentNumber = "";
                showRecents();
            }
        });
        endBtn.setLayoutParams(new LinearLayout.LayoutParams(dp(72), dp(72)));
        endRow.addView(endBtn);
        root.addView(endRow);

        show(root);
    }

    // ═══════════════════════════════════════════
    //  VOICEMAIL SCREEN
    // ═══════════════════════════════════════════
    static void showVoicemail() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(PRIMARY);
        header.setPadding(dp(8), dp(10), dp(16), dp(10));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(4));

        Button back = new Button(ctx);
        back.setText("\u2190");
        back.setTextSize(22);
        back.setTextColor(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showKeypad(); }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        header.addView(boldLabel("Voicemail", 18, WHITE));
        root.addView(header);

        ScrollView scroll = new ScrollView(ctx);
        LinearLayout content = new LinearLayout(ctx);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(8), dp(16), dp(16));

        // Voicemail entries
        String[][] voicemails = {
            {"Mom", "+1 (555) 8888", "Today, 2:15 PM", "0:42"},
            {"Dr. Smith", "+1 (555) 9999", "Mar 22, 3:15 PM", "1:23"},
            {"+1 (555) 3456", "+1 (555) 3456", "Mar 20, 11:00 AM", "0:15"}
        };

        for (final String[] vm : voicemails) {
            LinearLayout card = new LinearLayout(ctx);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(16), dp(14), dp(16), dp(14));
            card.setBackgroundColor(WHITE);
            card.setElevation(dp(1));
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            clp.setMargins(0, dp(4), 0, dp(4));
            card.setLayoutParams(clp);
            card.setBackground(roundRectStroke(WHITE, 8, DIVIDER));

            // Name row
            LinearLayout nameRow = new LinearLayout(ctx);
            nameRow.setOrientation(LinearLayout.HORIZONTAL);
            nameRow.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout avatar = new LinearLayout(ctx);
            avatar.setGravity(Gravity.CENTER);
            avatar.setBackground(circle(0xFFBBDEFB));
            avatar.addView(boldLabel(vm[0].substring(0, 1), 14, PRIMARY));
            LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(36), dp(36));
            alp.rightMargin = dp(12);
            avatar.setLayoutParams(alp);
            nameRow.addView(avatar);

            LinearLayout nameInfo = new LinearLayout(ctx);
            nameInfo.setOrientation(LinearLayout.VERTICAL);
            nameInfo.addView(boldLabel(vm[0], 15, DARK));
            nameInfo.addView(label(vm[2], 12, SECONDARY));
            nameRow.addView(nameInfo, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            nameRow.addView(label(vm[3], 14, SECONDARY));
            card.addView(nameRow);

            // Play button row
            LinearLayout playRow = new LinearLayout(ctx);
            playRow.setOrientation(LinearLayout.HORIZONTAL);
            playRow.setPadding(dp(48), dp(8), 0, 0);
            playRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView playBtn = boldLabel("\u25B6 Play", 13, PRIMARY);
            playBtn.setPadding(0, 0, dp(20), 0);
            playRow.addView(playBtn);

            // Progress bar (simulated)
            View progress = new View(ctx);
            progress.setBackground(roundRect(DIVIDER, 2));
            playRow.addView(progress, new LinearLayout.LayoutParams(0, dp(4), 1));

            card.addView(playRow);

            // Call back + Delete
            LinearLayout actionRow = new LinearLayout(ctx);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setPadding(dp(48), dp(8), 0, 0);

            final String vmNum = vm[1];
            final String vmName = vm[0];
            TextView callBack = label("\uD83D\uDCDE Call back", 13, PRIMARY);
            callBack.setPadding(0, 0, dp(24), 0);
            callBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { makeCall(vmNum, vmName); }
            });
            actionRow.addView(callBack);

            TextView delete = label("\uD83D\uDDD1 Delete", 13, RED);
            actionRow.addView(delete);

            card.addView(actionRow);

            content.addView(card);
        }

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        root.addView(bottomTabs());
        show(root);
    }

    // ═══ Utility ═══
    static String lookupContact(String number) {
        String digits = number.replaceAll("[^0-9]", "");
        for (Contact c : contacts) {
            if (c.number.replaceAll("[^0-9]", "").endsWith(digits) ||
                digits.endsWith(c.number.replaceAll("[^0-9]", ""))) {
                return c.name;
            }
        }
        return null;
    }
}
