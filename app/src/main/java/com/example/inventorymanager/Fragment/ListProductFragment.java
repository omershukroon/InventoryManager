package com.example.inventorymanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Models.Product;
import com.example.inventorymanager.R;
import com.example.inventorymanager.adapter.ProductAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListProductFragment extends Fragment {

    private RecyclerView listProduct_RecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private androidx.appcompat.widget.SearchView listProduct_SearchView;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_product_fragment, container, false);

        findView(v);
        initView();
        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("Product Information");
        // Load all products from Firebase initially
        loadAllProductsFromFirebase();
        // Listen for real-time updates
        listenForRealTimeUpdates();

        return v;
    }

    private void findView(View v) {
        listProduct_RecyclerView = v.findViewById(R.id.listProduct_RecyclerView);
        listProduct_SearchView = v.findViewById(R.id.listProduct_SearchView);
    }

    private void initView() {
        listProduct_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(getContext(), productList);

        listProduct_RecyclerView.setAdapter(productAdapter);
        listProduct_SearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return false;
            }
        });
    }

    private void loadAllProductsFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged(); // Update the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void listenForRealTimeUpdates() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    productList.add(product);
                    productAdapter.notifyItemInserted(productList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Product updatedProduct = dataSnapshot.getValue(Product.class);
                if (updatedProduct != null) {
                    int index = getProductIndex(updatedProduct.getProductID());
                    if (index != -1) {
                        productList.set(index, updatedProduct);
                        productAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Product removedProduct = dataSnapshot.getValue(Product.class);
                if (removedProduct != null) {
                    int index = getProductIndex(removedProduct.getProductID());
                    if (index != -1) {
                        productList.remove(index);
                        productAdapter.notifyItemRemoved(index);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private int getProductIndex(String productID) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductID().equals(productID)) {
                return i;
            }
        }
        return -1;
    }
}
