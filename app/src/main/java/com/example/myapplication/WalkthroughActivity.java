package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class WalkthroughActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutIndicators;
    private AppCompatButton btnNext, btnSkip;
    private WalkthroughAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        viewPager = findViewById(R.id.viewPager);
        layoutIndicators = findViewById(R.id.layoutIndicators);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        setupWalkthroughItems();
        setupIndicators();
        setCurrentIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
//                if (position == adapter.getItemCount() - 1) {
//                    btnNext.setText("Get Started");
//                } else {
//                    btnNext.setText("Next");
//                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                // Go to the next slide
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                // We are on the last slide, go to Login
                navigateToLogin();
            }
        });

        btnSkip.setOnClickListener(v -> navigateToLogin());
    }

    private void setupWalkthroughItems() {
        List<WalkthroughItem> items = new ArrayList<>();

        items.add(new WalkthroughItem(R.drawable.walkthrough1, "Set Your Boundary", "Pin a location and define a radius. Receive precise triggers the moment you enter the designated zone."));
        items.add(new WalkthroughItem(R.drawable.walkthrough2, "Smart Notifications", "Low-latency push notifications triggered by GPS. Battery-optimized tracking ensures efficiency."));
        items.add(new WalkthroughItem(R.drawable.walkthrough3, "Fast Task Creation", "Enter a name and description, pick your spot on the map, and tap done. Three steps to complete setup."));

        adapter = new WalkthroughAdapter(items);
        viewPager.setAdapter(adapter);
    }

    private void navigateToLogin() {
        // This takes the user directly to the Login screen
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

        // finish() is important so they can't "Go Back" into the walkthrough
        finish();
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
            }
        }
    }

    // Small inner class to hold data
    // Move this at the bottom, but inside the main WalkthroughActivity class
    public static class WalkthroughItem {
        int image;
        String title, desc;
        public WalkthroughItem(int image, String title, String desc) {
            this.image = image;
            this.title = title;
            this.desc = desc;
        }
    }
}