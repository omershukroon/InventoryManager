package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentOrderActivity extends AppCompatActivity {

    private MaterialButton fragOrder_BTN_GoBack;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_order);
        findView();
        initView();
    }

    private void findView() {
        fragOrder_BTN_GoBack = findViewById(R.id.fragOrder_BTN_GoBack);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    private void initView() {
        fragOrder_BTN_GoBack.setOnClickListener(v -> changeActivity(0));
                floatingActionButton.setOnClickListener(v -> changeActivity(1));
    }

    private void changeActivity(int i) {
        if (i == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
        }
        finish();
    }
}