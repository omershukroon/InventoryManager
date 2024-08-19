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
import com.example.inventorymanager.adapter.AddProductToOrderAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListAddOrderFragment extends Fragment {

    private RecyclerView listAddOrder_RecyclerView;
    private AddProductToOrderAdapter addProductToOrderAdapter;
    private List<Product> productList = new ArrayList<>();
    private androidx.appcompat.widget.SearchView listAddOrder_SearchView;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_add_order_fragment, container, false);

        findView(v);
        databaseRef = FirebaseDatabase.getInstance().getReference("Product Information");
        loadAllProductsFromFirebase();
        listenForRealTimeUpdates();
        initView();

        return v;
    }

    private void findView(View v) {
        listAddOrder_RecyclerView = v.findViewById(R.id.listAddOrder_RecyclerView);
        listAddOrder_SearchView = v.findViewById(R.id.listAddOrder_SearchView);
    }

    private void initView() {
        listAddOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addProductToOrderAdapter = new AddProductToOrderAdapter(getContext(), productList);
        listAddOrder_RecyclerView.setAdapter(addProductToOrderAdapter);
        listAddOrder_SearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addProductToOrderAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addProductToOrderAdapter.filter(newText);
                return false;
            }
        });
    }

    private void loadAllProductsFromFirebase() {
        if (productList.size() !=0){

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
                addProductToOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors during the data retrieval
            }
        });
        }
    }

    private void listenForRealTimeUpdates() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    productList.add(product);
                    addProductToOrderAdapter.notifyItemInserted(productList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Product updatedProduct = dataSnapshot.getValue(Product.class);
                if (updatedProduct != null) {
                    int index = getProductIndex(updatedProduct.getProductID());
                    if (index != -1) {
                        productList.set(index, updatedProduct);
                        addProductToOrderAdapter.notifyItemChanged(index);
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
                        addProductToOrderAdapter.notifyItemRemoved(index);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors during real-time data listening
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

    public List<Product> getSelectedProducts() {
        List<Product> selectedProducts = new ArrayList<>();
        for (Product product : productList) {
            int quantity = addProductToOrderAdapter.getProductQuantity(product.getBarcode());
            if (quantity > 0) {
                selectedProducts.add(product);
            }
        }
        return selectedProducts;
    }

    public List<Integer> getSelectedQuantities() {
        List<Integer> quantities = new ArrayList<>();
        for (Product product : productList) {
            int quantity = addProductToOrderAdapter.getProductQuantity(product.getBarcode());
            if (quantity > 0) {
                quantities.add(quantity);
            }
        }
        return quantities;
    }
}
