import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.mockdonalds.*;
import java.util.List;

/**
 * Headless test runner for MockDonalds app.
 * Exercises: SQLite, ListView, Intent extras, Activity lifecycle, SharedPreferences.
 *
 * Run: java -cp build MockDonaldsRunner
 * Expected: 10 PASS, 0 FAIL
 */
public class MockDonaldsRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== MockDonalds End-to-End Test ===\n");

        try {
            // Initialize MiniServer
            MiniServer.init("com.example.mockdonalds");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // Launch MenuActivity
            Intent menuIntent = new Intent();
            menuIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.MenuActivity"));
            am.startActivity(null, menuIntent, -1);

            Activity menuAct = am.getResumedActivity();
            check("MenuActivity launched", menuAct instanceof MenuActivity);
            MenuActivity menu = (MenuActivity) menuAct;

            // Verify menu loaded from SQLite
            List<MenuItem> items = menu.getMenuItems();
            check("MenuActivity has 8 menu items", items.size() == 8);

            // Verify ListView populated
            View decor = menu.getWindow().getDecorView();
            ListView listView = findListView(decor);
            check("ListView populated", listView != null && listView.getChildCount() == 8);

            // Simulate clicking item 0 (Big Mock Burger)
            MenuItem firstItem = items.get(0);
            Intent detailIntent = new Intent();
            detailIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.ItemDetailActivity"));
            detailIntent.putExtra("item_id", firstItem.id);
            detailIntent.putExtra("item_name", firstItem.name);
            detailIntent.putExtra("item_price", firstItem.price);
            detailIntent.putExtra("item_description", firstItem.description);
            am.startActivity(menuAct, detailIntent, 100);

            Activity detailAct = am.getResumedActivity();
            check("ItemDetailActivity launched", detailAct instanceof ItemDetailActivity);
            ItemDetailActivity detail = (ItemDetailActivity) detailAct;
            check("Item name = Big Mock Burger, price = $5.99",
                    "Big Mock Burger".equals(detail.getItemName()) &&
                    Math.abs(detail.getItemPrice() - 5.99) < 0.01);

            // Simulate "Add to Cart" button click
            CartManager cart = new CartManager(detail);
            MenuItem itemToAdd = menu.getDbHelper().getItem(firstItem.id);
            int cartCount = cart.addToCart(itemToAdd);
            check("Add to Cart: count = 1", cartCount == 1);

            // Go to CartActivity
            detail.finish();
            Intent cartIntent = new Intent();
            cartIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.CartActivity"));
            am.startActivity(menuAct, cartIntent, 200);

            Activity cartAct = am.getResumedActivity();
            check("CartActivity shows 1 item, total = $5.99",
                    cartAct instanceof CartActivity &&
                    ((CartActivity) cartAct).getCartItems().size() == 1 &&
                    Math.abs(((CartActivity) cartAct).getCartManager().getCartTotal() - 5.99) < 0.01);

            // Checkout
            Intent checkoutIntent = new Intent();
            checkoutIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.CheckoutActivity"));
            checkoutIntent.putExtra("total", "$5.99");
            checkoutIntent.putExtra("item_count", 1);
            am.startActivity(cartAct, checkoutIntent, 300);

            Activity checkoutAct = am.getResumedActivity();
            check("Checkout: order saved",
                    checkoutAct instanceof CheckoutActivity &&
                    ((CheckoutActivity) checkoutAct).getOrderNumber() == 1);

            // Verify cart cleared
            CartManager cartAfter = new CartManager(checkoutAct);
            check("Cart cleared after checkout", cartAfter.getCartCount() == 0);

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
        System.exit(failed);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }

    private static ListView findListView(View root) {
        if (root instanceof ListView) return (ListView) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                ListView found = findListView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
}
