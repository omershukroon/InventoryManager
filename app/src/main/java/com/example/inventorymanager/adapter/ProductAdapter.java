package com.example.inventorymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventorymanager.Models.Product;
import com.example.inventorymanager.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private List<Product> filteredProductList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.filteredProductList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredProductList.get(position);
        holder.productName.setText(product.getName());
        holder.productAmount.setText("Amount: " + product.getAmount());
        holder.productPrice.setText("Price: " + product.getPrice());
        holder.productBarcode.setText("Barcode: " + product.getBarcode());

        // Load the first image from Firebase Storage using Glide
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_image_icon)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.no_image_icon);
        }
    }

    @Override
    public int getItemCount() {
        return filteredProductList.size();
    }

    public void filter(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productAmount, productPrice, productBarcode;
        ImageView productImage;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productAmount = itemView.findViewById(R.id.product_amount);
            productPrice = itemView.findViewById(R.id.product_price);
            productBarcode = itemView.findViewById(R.id.product_barcode);
            productImage = itemView.findViewById(R.id.product_IMG);
        }
    }
}
