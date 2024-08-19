package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanager.Fragment.ListAddOrderFragment;
import com.example.inventorymanager.Models.Order;
import com.example.inventorymanager.Models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddOrderActivity extends AppCompatActivity {

    private ListAddOrderFragment listAddOrderFragment;
    private MaterialButton addOrder_BTN_GoBack;
    private ExtendedFloatingActionButton extendedFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        findView();
        initView();
    }

    private void findView() {
        addOrder_BTN_GoBack = findViewById(R.id.addOrder_BTN_GoBack);
        extendedFloatingActionButton = findViewById(R.id.extended_floating_action_button);
    }

    private void initView() {
        listAddOrderFragment = new ListAddOrderFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.addOrder_FRAME_list, listAddOrderFragment).commit();

        addOrder_BTN_GoBack.setOnClickListener(v -> changeActivity());
        extendedFloatingActionButton.setOnClickListener(v -> addOrderToDataBase());
    }

    private void addOrderToDataBase() {
        // Retrieve the selected products and their quantities
        List<Product> selectedProducts = listAddOrderFragment.getSelectedProducts();
        List<Integer> selectedQuantities = listAddOrderFragment.getSelectedQuantities();

        if (selectedProducts.isEmpty()) {
            // No products were selected
            return;
        }

        // Fetch the last order number from the database
        DatabaseReference lastOrderRef = FirebaseDatabase.getInstance().getReference("Order Information");
        lastOrderRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int lastOrderNumber = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order lastOrder = snapshot.getValue(Order.class);
                    if (lastOrder != null) {
                        lastOrderNumber = lastOrder.getOrderNumber();
                    }
                }

                // Create a new Order object with the incremented order number
                int newOrderNumber = lastOrderNumber + 1;
                double totalAmount = calculateTotalAmount(selectedProducts, selectedQuantities);

                Order newOrder = new Order(new ArrayList<>(selectedProducts), new ArrayList<>(selectedQuantities), false, totalAmount, null);
                newOrder.setOrderNumber(newOrderNumber);

                // Save the new order to the database
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order Information").push();
                newOrder.setOrderID(orderRef.getKey());

                orderRef.setValue(newOrder).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If order is successfully saved, navigate back
                        changeActivity();
                    } else {
                        // Handle error
                        // You can show an error message here
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    private double calculateTotalAmount(List<Product> products, List<Integer> quantities) {
        double totalAmount = 0;
        for (int i = 0; i < products.size(); i++) {
            totalAmount += products.get(i).getPrice() * quantities.get(i);
        }
        return totalAmount;
    }

    private void changeActivity() {
        Intent intent = new Intent(this, FragmentOrderActivity.class);
        startActivity(intent);
        finish();
    }
}
