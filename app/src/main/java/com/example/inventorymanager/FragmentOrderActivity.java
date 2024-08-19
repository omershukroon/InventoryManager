package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanager.Fragment.ListOrderFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentOrderActivity extends AppCompatActivity {
    private ListOrderFragment listOrderFragment;
    private MaterialButton fragOrder_BTN_GoBack;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_order); // Ensure this layout has fragOrder_FRAME_list
        findView();
        initView();
    }

    private void findView() {
        fragOrder_BTN_GoBack = findViewById(R.id.fragOrder_BTN_GoBack);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    private void initView() {
        // Initialize the ListOrderFragment
        listOrderFragment = new ListOrderFragment();

        // Add the fragment to the FrameLayout with ID fragOrder_FRAME_list
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragOrder_FRAME_list, listOrderFragment)
                .commit();

        // Set up button click listeners
        fragOrder_BTN_GoBack.setOnClickListener(v -> changeActivity(0));
        floatingActionButton.setOnClickListener(v -> changeActivity(1));
    }

    private void changeActivity(int i) {
        Intent intent;
        if (i == 0) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, AddOrderActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
