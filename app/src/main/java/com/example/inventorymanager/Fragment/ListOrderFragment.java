package com.example.inventorymanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Models.Order;
import com.example.inventorymanager.R;
import com.example.inventorymanager.adapter.OrderAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOrderFragment extends Fragment {

    private RecyclerView listOrder_RecyclerView;
    private androidx.appcompat.widget.SearchView listOrder_SearchView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredOrderList = new ArrayList<>();
    private DatabaseReference databaseRef;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_order_fragment, container, false);

        spinner = v.findViewById(R.id.listOrder_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.spinner_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        findView(v);
        initView();

        databaseRef = FirebaseDatabase.getInstance().getReference("Order Information");

        loadAllOrdersFromFirebase();
        listenForRealTimeUpdates();

        // Set up the spinner to filter the list based on the selected option
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                filterOrdersBasedOnSpinner(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return v;
    }

    private void findView(View v) {
        listOrder_RecyclerView = v.findViewById(R.id.listOrder_RecyclerView);
        listOrder_SearchView = v.findViewById(R.id.listOrder_SearchView);
    }

    private void initView() {
        listOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), filteredOrderList);
        listOrder_RecyclerView.setAdapter(orderAdapter);

        listOrder_SearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                orderAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderAdapter.filter(newText);
                return false;
            }
        });
    }

    private void loadAllOrdersFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                filteredOrderList.clear();
                filteredOrderList.addAll(orderList); // Initially show all orders
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void listenForRealTimeUpdates() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Order order = dataSnapshot.getValue(Order.class);
                if (order != null) {
                    orderList.add(order);
                    filterOrdersBasedOnSpinner(spinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Order updatedOrder = dataSnapshot.getValue(Order.class);
                if (updatedOrder != null) {
                    int index = getOrderIndex(updatedOrder.getOrderID());
                    if (index != -1) {
                        orderList.set(index, updatedOrder);
                        filterOrdersBasedOnSpinner(spinner.getSelectedItem().toString());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Order removedOrder = dataSnapshot.getValue(Order.class);
                if (removedOrder != null) {
                    int index = getOrderIndex(removedOrder.getOrderID());
                    if (index != -1) {
                        orderList.remove(index);
                        filterOrdersBasedOnSpinner(spinner.getSelectedItem().toString());
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void filterOrdersBasedOnSpinner(String selectedFilter) {
        filteredOrderList.clear();

        if (selectedFilter.equals("All Orders")) {
            filteredOrderList.addAll(orderList);
        } else if (selectedFilter.equals("Fulfilled")) {
            for (Order order : orderList) {
                if (order.isFulfilled()) {
                    filteredOrderList.add(order);
                }
            }
        } else if (selectedFilter.equals("Not Fulfilled")) {
            for (Order order : orderList) {
                if (!order.isFulfilled()) {
                    filteredOrderList.add(order);
                }
            }
        }

        orderAdapter.notifyDataSetChanged();
    }

    private int getOrderIndex(String orderID) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getOrderID().equals(orderID)) {
                return i;
            }
        }
        return -1;
    }
}
