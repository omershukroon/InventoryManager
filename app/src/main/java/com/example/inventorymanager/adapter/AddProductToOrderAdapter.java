package com.example.inventorymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventorymanager.Models.Product;
import com.example.inventorymanager.R;

import java.util.ArrayList;
import java.util.List;

public class AddProductToOrderAdapter extends RecyclerView.Adapter<AddProductToOrderAdapter.ProductViewHolder> {
    private List<Product> productList;
    private List<Product> filteredProductList;
    private Context context;
    private List<Integer> productQuantities;

    public AddProductToOrderAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.filteredProductList = new ArrayList<>(productList);
        this.productQuantities = new ArrayList<>();

        // Initialize all product quantities to 0
        initializeQuantities(filteredProductList.size());
    }

    private void initializeQuantities(int size) {
        productQuantities.clear();
        for (int i = 0; i < size; i++) {
            productQuantities.add(0);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_product_order_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredProductList.get(position);

        // Set product details
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: " + product.getPrice());
        holder.productBarcode.setText(product.getBarcode());
        holder.txtQuantity.setText(String.valueOf(productQuantities.get(position)));

        // Load the product image using Glide
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_image_icon)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.no_image_icon);
        }

        // Set onClickListener for the plus button
        holder.btnPlus.setOnClickListener(v -> {
            int currentQuantity = productQuantities.get(position);
            productQuantities.set(position, currentQuantity + 1);
            holder.txtQuantity.setText(String.valueOf(productQuantities.get(position)));
        });

        // Set onClickListener for the minus button
        holder.btnMinus.setOnClickListener(v -> {
            int currentQuantity = productQuantities.get(position);
            if (currentQuantity > 0) {
                productQuantities.set(position, currentQuantity - 1);
                holder.txtQuantity.setText(String.valueOf(productQuantities.get(position)));
            }
        });
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

        // Re-initialize product quantities to match the filtered list size
        initializeQuantities(filteredProductList.size());

        notifyDataSetChanged();
    }

    public int getProductQuantity(String barcode) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getBarcode().equals(barcode)) {
                return productQuantities.get(i);
            }
        }
        return 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productBarcode, txtQuantity;
        ImageView productImage;
        Button btnMinus, btnPlus;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productBarcode = itemView.findViewById(R.id.product_barcode);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            productImage = itemView.findViewById(R.id.product_IMG);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}
