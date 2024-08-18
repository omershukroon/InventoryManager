package com.example.inventorymanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventorymanager.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private MaterialButton add_btn_AddImage;
    private EditText add_ET_ProductName;
    private EditText add_ET_ProductPrice;
    private EditText add_ET_ProductQuantity;
    private EditText add_ET_ProductBarcode;
    private MaterialButton add_btn_AddProduct;
    private LinearLayout llImageContainer;
    private FloatingActionButton add_GoBack_floatingButton;
    private String imgURL = "";
    private FirebaseDatabase database;
    private String productID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        database = FirebaseDatabase.getInstance();
        findView();
        initViwe();

    }

    private void findView() {
        add_btn_AddImage = findViewById(R.id.add_btn_AddImage);
        add_ET_ProductName = findViewById(R.id.add_ET_ProductName);
        add_ET_ProductPrice = findViewById(R.id.add_ET_ProductPrice);
        add_ET_ProductQuantity = findViewById(R.id.add_ET_ProductQuantity);
        add_ET_ProductBarcode = findViewById(R.id.add_ET_ProductBarcode);
        add_btn_AddProduct = findViewById(R.id.add_btn_AddProduct);
        add_GoBack_floatingButton = findViewById(R.id.add_GoBack_floatingButton);
        llImageContainer = findViewById(R.id.llImageContainer);
    }

    private void initViwe() {

        add_btn_AddImage.setOnClickListener(v -> showImagePickerOptions());

        add_btn_AddProduct.setOnClickListener(v -> uploadProductInformation());

    }


    private void showImagePickerOptions() {
        // Show options to choose between camera and gallery
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Camera selected
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else if (which == 1) {
                // Gallery selected
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri = null;
            Bitmap imageBitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (imageBitmap != null) {
                addImageToScrollView(imageBitmap);
            }
        }
    }

    private void addImageToScrollView(Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.setMargins(10, 10, 10, 10);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);

        llImageContainer.addView(imageView);
    }


    private void uploadProductInformation() {
        String productName = add_ET_ProductName.getText().toString().trim();
        String productBarcode = add_ET_ProductBarcode.getText().toString().trim();
        int productAmount = Integer.parseInt(add_ET_ProductQuantity.getText().toString().trim());
        double productPrice = Double.parseDouble(add_ET_ProductPrice.getText().toString().trim());

        if (TextUtils.isEmpty(productName)) {
            add_ET_ProductName.setError("Product name is required");
            add_ET_ProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(productBarcode)) {
            add_ET_ProductBarcode.setError("Product barcode is required");
            add_ET_ProductBarcode.requestFocus();
            return;
        }

        if (productAmount <= 0) {
            add_ET_ProductQuantity.setError("Amount must be greater than 0");
            add_ET_ProductQuantity.requestFocus();
            return;
        }

        if (productPrice <= 0) {
            add_ET_ProductPrice.setError("Price must be greater than 0");
            add_ET_ProductPrice.requestFocus();
            return;
        }

        DatabaseReference databaseRef = database.getReference("Product Information").push();
        String productID = databaseRef.getKey();
        Product product = new Product(productName, productBarcode, imgURL , productAmount, productPrice,productID);

        databaseRef.setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product information uploaded successfully", Toast.LENGTH_SHORT).show();
                    changeActivity();
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to upload product information. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void changeActivity() {
    }

}