package com.example.inventorymanager.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.AddProductActivity;
import com.example.inventorymanager.FragmentProductActivity;
import com.example.inventorymanager.Models.Product;
import com.example.inventorymanager.R;
import com.example.inventorymanager.adapter.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private SearchView listProduct_SearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_product_fragment, container, false);

        findView(v);
        initView();
        // Load products from Firebase
        loadProductsFromFirebase();

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
        listProduct_SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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


    private void loadProductsFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Product Information");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
