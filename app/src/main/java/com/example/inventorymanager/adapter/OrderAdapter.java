package com.example.inventorymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Models.Order;
import com.example.inventorymanager.Models.Product;
import com.example.inventorymanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private List<Order> filteredOrderList;
    private Context context;
    private FirebaseDatabase database;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.filteredOrderList = new ArrayList<>(orderList);
        this.database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = filteredOrderList.get(position);
        holder.orderNumber.setText("Order #" + order.getOrderNumber());
        holder.orderAmount.setText("Amount: $" + order.getOrderAmount());
        holder.orderFulfilledStatus.setText("Fulfilled: " + (order.isFulfilled() ? "Yes" : "No"));
        holder.orderFulfilledCheckBox.setChecked(order.isFulfilled());

        // Change the color based on the fulfillment status
        if (order.isFulfilled()) {
            holder.orderColorIsFulfilled.setBackgroundColor(ContextCompat.getColor(context, R.color.green_A400));
        } else {
            holder.orderColorIsFulfilled.setBackgroundColor(ContextCompat.getColor(context, R.color.red_A400));
        }

        holder.orderFulfilledCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the order's fulfillment status

            // Update the color immediately after changing the status
            if (isChecked) {
                holder.orderColorIsFulfilled.setBackgroundColor(ContextCompat.getColor(context, R.color.green_A400));
                updateProductQuantities(order, true); // Subtract quantities
            } else {
                holder.orderColorIsFulfilled.setBackgroundColor(ContextCompat.getColor(context, R.color.red_A400));
                updateProductQuantities(order, false); // Add quantities
            }
            order.setFulfilled(isChecked);

            holder.orderFulfilledStatus.setText("Fulfilled: " + (order.isFulfilled() ? "Yes" : "No"));
            // Update the order in the database
            DatabaseReference orderRef = database.getReference("Order Information").child(order.getOrderID());
            orderRef.setValue(order);
        });

        holder.productListContainer.removeAllViews();
        List<Product> products = order.getProductList();
        ArrayList<Integer> productQuantities = order.getProductQuantityList();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = productQuantities.get(i);

            View productView = LayoutInflater.from(context).inflate(R.layout.product_item, holder.productListContainer, false);
            TextView productName = productView.findViewById(R.id.product_name);
            TextView productAmount = productView.findViewById(R.id.product_amount);
            TextView productPrice = productView.findViewById(R.id.product_price);

            productName.setText(product.getName());
            productAmount.setText("Quantity: " + quantity);
            productPrice.setText("Price: $" + product.getPrice());

            holder.productListContainer.addView(productView);
        }
    }

    private void updateProductQuantities(Order order, boolean isFulfilled) {
        DatabaseReference productRef = database.getReference("Product Information");

        List<Product> products = order.getProductList();
        ArrayList<Integer> quantities = order.getProductQuantityList();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int orderQuantity = quantities.get(i);
            String productID = product.getProductID();

            productRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Product dbProduct = dataSnapshot.getValue(Product.class);
                    if (dbProduct != null) {
                        int currentQuantity = dbProduct.getAmount();
                        int updatedQuantity;

                        if (isFulfilled) {
                            updatedQuantity = currentQuantity - orderQuantity;
                            dbProduct.setAmount(updatedQuantity);
                        } else {
                            updatedQuantity = currentQuantity + orderQuantity;
                            dbProduct.setAmount(updatedQuantity);
                        }

                        // Update the product quantity in the database
                        productRef.child(productID).child("amount").setValue(updatedQuantity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors here
                }
            });
        }
    }

    public void filter(String query) {
        filteredOrderList.clear();
        if (query.isEmpty()) {
            filteredOrderList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                if (String.valueOf(order.getOrderNumber()).startsWith(query)) {
                    filteredOrderList.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredOrderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderAmount, orderFulfilledStatus;
        CheckBox orderFulfilledCheckBox;
        LinearLayout productListContainer;
        View orderColorIsFulfilled;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderAmount = itemView.findViewById(R.id.order_amount);
            orderFulfilledStatus = itemView.findViewById(R.id.order_fulfilled_status);
            orderFulfilledCheckBox = itemView.findViewById(R.id.orderItem_CheckBox_folfilled);
            productListContainer = itemView.findViewById(R.id.order_product_list_container);
            orderColorIsFulfilled = itemView.findViewById(R.id.order_color_isFulfilled);
        }
    }
}
