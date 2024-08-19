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

    // RecyclerView for displaying the list of products
    private RecyclerView listProduct_RecyclerView;
    // Adapter for managing and displaying the product data in the RecyclerView
    private ProductAdapter productAdapter;
    // List to store the products loaded from the database
    private List<Product> productList = new ArrayList<>();
    // SearchView for filtering products by user input
    private androidx.appcompat.widget.SearchView listProduct_SearchView;
    // Reference to the Firebase Realtime Database
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_list_product_fragment, container, false);

        // Initialize the views in the fragment
        findView(v);
        // Set up the RecyclerView and SearchView
        initView();
        // Initialize Firebase reference to the "Product Information" node
        databaseRef = FirebaseDatabase.getInstance().getReference("Product Information");
        // Load all products from Firebase into the list
        loadAllProductsFromFirebase();
        // Listen for real-time updates to the products in the database
        listenForRealTimeUpdates();


        return v;
    }

    // Method to find and assign views to their corresponding variables
    private void findView(View v) {
        listProduct_RecyclerView = v.findViewById(R.id.listProduct_RecyclerView);
        listProduct_SearchView = v.findViewById(R.id.listProduct_SearchView);
    }

    // Method to set up the RecyclerView and SearchView
    private void initView() {
        // Set the RecyclerView to use a linear layout manager (vertical list)
        listProduct_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize the ProductAdapter with the context and product list
        productAdapter = new ProductAdapter(getContext(), productList);
        // Set the adapter to the RecyclerView
        listProduct_RecyclerView.setAdapter(productAdapter);
        // Set up a listener for the SearchView to filter the product list based on user input
        listProduct_SearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter the product list when the user submits a search query
                productAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the product list as the user types in the SearchView
                productAdapter.filter(newText);
                return false;
            }
        });
    }

    // Method to load all products from Firebase into the product list
    private void loadAllProductsFromFirebase() {
        if (productList.size() != 0) {

            // Add a listener to retrieve data once from the database
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear the current product list
                    productList.clear();
                    // Iterate through all child nodes in the "Product Information" node
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Convert each child node to a Product object
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            // Add the product to the product list
                            productList.add(product);
                        }
                    }
                    // Notify the adapter that the data set has changed
                    productAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors during the data retrieval
                }
            });
        }
    }

    // Method to listen for real-time updates to the product data in Firebase
    private void listenForRealTimeUpdates() {
        // Add a ChildEventListener to the database reference to listen for changes
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // When a new product is added to the database
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    // Add the new product to the list
                    productList.add(product);
                    // Notify the adapter that an item has been inserted
                    productAdapter.notifyItemInserted(productList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // When an existing product is updated in the database
                Product updatedProduct = dataSnapshot.getValue(Product.class);
                if (updatedProduct != null) {
                    // Find the index of the updated product in the list
                    int index = getProductIndex(updatedProduct.getProductID());
                    if (index != -1) {
                        // Replace the old product data with the updated data
                        productList.set(index, updatedProduct);
                        // Notify the adapter that an item has been changed
                        productAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // When a product is removed from the database
                Product removedProduct = dataSnapshot.getValue(Product.class);
                if (removedProduct != null) {
                    // Find the index of the removed product in the list
                    int index = getProductIndex(removedProduct.getProductID());
                    if (index != -1) {
                        // Remove the product from the list
                        productList.remove(index);
                        // Notify the adapter that an item has been removed
                        productAdapter.notifyItemRemoved(index);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle if necessary (e.g., if the order of items in the list changes)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors during real-time data listening
            }
        });
    }

    // Helper method to find the index of a product in the list based on its product ID
    private int getProductIndex(String productID) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductID().equals(productID)) {
                return i;
            }
        }
        // Return -1 if the product is not found in the list
        return -1;
    }
}
