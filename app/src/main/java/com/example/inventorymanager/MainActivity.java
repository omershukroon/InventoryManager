package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private MaterialButton main_BTN_LogUot;
    private AppCompatImageButton main_BTN_ProductManager;
    private AppCompatImageButton main_BTN_ProductOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initView();

    }

    private void findView() {
        main_BTN_LogUot = findViewById(R.id.main_BTN_LogUot);
        main_BTN_ProductManager = findViewById(R.id.main_BTN_ProductManager);
        main_BTN_ProductOrder = findViewById(R.id.main_BTN_ProductOrder);

    }

    private void initView() {
        main_BTN_ProductManager.setOnClickListener(v -> changeActivity(0));
        main_BTN_ProductOrder.setOnClickListener(v -> changeActivity(1));
        main_BTN_LogUot.setOnClickListener(v -> logUot());
    }

    private void logUot() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // User is now signed out
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void changeActivity(int i) {
        if (i == 0) {
            Intent intent = new Intent(this, FragmentProductActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, FragmentOrderActivity.class);
            startActivity(intent);
        }
        finish();
    }
}