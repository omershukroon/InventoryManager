package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanager.Fragment.ListProductFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentProductActivity extends AppCompatActivity {
    private ListProductFragment listProductFragment;
    private MaterialButton fragProduct_BTN_GoBack;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_product);
        findView();
        initView();
    }

    private void findView() {
        fragProduct_BTN_GoBack = findViewById(R.id.fragProduct_BTN_GoBack);
        floatingActionButton = findViewById(R.id.floatingActionButton);

    }

    private void initView() {
        listProductFragment = new ListProductFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragProduct_FRAME_list, listProductFragment).commit();

        fragProduct_BTN_GoBack.setOnClickListener(v -> changeActivity(0));
        floatingActionButton.setOnClickListener(v -> changeActivity(1));

    }

    private void changeActivity(int i) {
        if (i==0){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        }else {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
        }
        finish();
    }
}