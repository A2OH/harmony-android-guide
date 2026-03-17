package com.example.hello;

import android.app.Activity;
import android.os.Bundle;

public class HelloActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("=== REAL APK RUNNING ===");
        System.out.println("Hello from a REAL Android APK on Dalvik!");
        System.out.println("Package: " + getClass().getPackage().getName());
        System.out.println("Activity: " + getClass().getSimpleName());

        // Test basic Java stdlib
        int sum = 0;
        for (int i = 1; i <= 10; i++) sum += i;
        System.out.println("Sum 1..10 = " + sum);

        // Test HashMap
        java.util.HashMap<String, Integer> map = new java.util.HashMap<String, Integer>();
        map.put("burgers", 3);
        map.put("fries", 5);
        map.put("drinks", 2);
        System.out.println("Menu items: " + map.size());
        System.out.println("Burgers: " + map.get("burgers"));

        // Test Double.parseDouble (our new native)
        double price = Double.parseDouble("5.99");
        System.out.println("Price parsed: " + price);

        // Test Math (our new native)
        System.out.println("Math.sqrt(256) = " + Math.sqrt(256));

        System.out.println("=== REAL APK COMPLETE ===");
    }
}
