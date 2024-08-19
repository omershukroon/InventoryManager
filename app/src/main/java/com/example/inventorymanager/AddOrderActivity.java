package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanager.Fragment.ListAddOrderFragment;
import com.example.inventorymanager.Models.Order;
import com.example.inventorymanager.Models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddOrderActivity extends AppCompatActivity {

    private ListAddOrderFragment listAddOrderFragment;
    private MaterialButton addOrder_BTN_GoBack;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);  // Correct layout file
        findView();
        initView();
    }

    private void findView() {
        addOrder_BTN_GoBack = findViewById(R.id.addOrder_BTN_GoBack);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    private void initView() {
        listAddOrderFragment = new ListAddOrderFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.addOrder_FRAME_list, listAddOrderFragment).commit();

        addOrder_BTN_GoBack.setOnClickListener(v -> changeActivity());
        floatingActionButton.setOnClickListener(v -> addOrderToDataBase());
    }

    private void addOrderToDataBase() {
        // Retrieve the selected products and their quantities
        List<Product> selectedProducts = listAddOrderFragment.getSelectedProducts();
        List<Integer> selectedQuantities = listAddOrderFragment.getSelectedQuantities();

        if (selectedProducts.isEmpty()) {
            // No products were selected
            return;
        }

        // Calculate total amount of the order
        double totalAmount = 0;
        for (int i = 0; i < selectedProducts.size(); i++) {
            totalAmount += selectedProducts.get(i).getPrice() * selectedQuantities.get(i);
        }

        // Create a new Order
        Order newOrder = new Order(new ArrayList<>(selectedProducts), new ArrayList<>(selectedQuantities), false, totalAmount, null);

        // Save the order to Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").push();
        newOrder.setOrderID(orderRef.getKey());

        orderRef.setValue(newOrder).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                changeActivity(); // Go back to the previous activity
            } else {
                // Handle error
            }
        });
    }

    private void changeActivity() {
        Intent intent = new Intent(this, FragmentOrderActivity.class);
        startActivity(intent);
        finish();
    }
}
