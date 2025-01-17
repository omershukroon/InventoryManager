package com.example.inventorymanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inventorymanager.Models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> imageUrls = new ArrayList<>();
    private Uri imageUri;
    private FirebaseStorage storage;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        findView();
        registerActivityResultLaunchers();
        initView();
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

    private void registerActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                imageUri = getImageUriFromBitmap(imageBitmap);
                                addImageToScrollView(imageBitmap);
                            }
                        }
                    } else {
                        Toast.makeText(AddProductActivity.this, "Camera operation canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            imageUri = result.getData().getData();
                            if (imageUri != null) {
                                try {
                                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    addImageToScrollView(imageBitmap);
                                } catch (IOException e) {
                                    Toast.makeText(AddProductActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(AddProductActivity.this, "Gallery operation canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initView() {
        add_btn_AddImage.setOnClickListener(v -> showImagePickerOptions());

        add_btn_AddProduct.setOnClickListener(v -> uploadProductInformation());

        add_GoBack_floatingButton.setOnClickListener(v -> changeActivity());
    }

    private void showImagePickerOptions() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Camera selected
                if (ContextCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    openCamera();
                }
            } else {
                // Gallery selected
                requestGalleryPermission();
            }
        });
        builder.show();
    }

    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, 102);
            } else {
                openGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                openGallery();
            }
        } else { // Below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProductImage", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = null;
            Bitmap imageBitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageUri = getImageUriFromBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (imageBitmap != null && imageUri != null) {
                addImageToScrollView(imageBitmap);
            }
        }
    }

    private void uploadImagesAndProductInfo(Product product) {
        if (imageUrls.isEmpty()) {
            uploadProductInformation(product); // If no images, directly upload product info
            return;
        }

        StorageReference storageRef = storage.getReference();
        List<String> uploadedImageUrls = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            Uri fileUri = Uri.parse(imageUrls.get(i));
            StorageReference imageRef = storageRef.child("products IMG/" + product.getName() + "/image_" + i + ".jpg");

            UploadTask uploadTask = imageRef.putFile(fileUri);
            int finalI = i;
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    uploadedImageUrls.add(uri.toString());
                    // When all images are uploaded, save the product information
                    if (uploadedImageUrls.size() == imageUrls.size()) {
                        product.getImages().clear();
                        product.getImages().addAll(uploadedImageUrls);
                        uploadProductInformation(product);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(AddProductActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void uploadProductInformation(Product product) {
        DatabaseReference databaseRef = database.getReference("Product Information").push();
        product.setProductID(databaseRef.getKey());

        databaseRef.setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddProductActivity.this, "Product information uploaded successfully", Toast.LENGTH_SHORT).show();
                changeActivity();
            } else {
                Toast.makeText(AddProductActivity.this, "Failed to upload product information. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AddProductActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void addImageToScrollView(Bitmap bitmap) {
        try {
            // Correct the orientation if necessary
            bitmap = correctImageOrientation(bitmap, imageUri);

            // Create a new ImageView
            ImageView imageView = new ImageView(this);

            // Set layout parameters with 100dp by 100dp
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDisplayMetrics().density * 200, // Width 100dp
                    (int) getResources().getDisplayMetrics().density * 200  // Height 100dp
            );
            layoutParams.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(layoutParams);

            // Set the corrected and resized bitmap to the ImageView
            imageView.setImageBitmap(bitmap);

            // Add the ImageView to the llImageContainer
            llImageContainer.addView(imageView);

            // Add the image URI to the list
            imageUrls.add(imageUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap correctImageOrientation(Bitmap bitmap, Uri imageUri) throws IOException {
        ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(imageUri));
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

        Product product = new Product(productName, productBarcode, new ArrayList<>(), productAmount, productPrice, null);
        uploadImagesAndProductInfo(product);
    }

    private void changeActivity() {
        Intent intent = new Intent(this, FragmentProductActivity.class);
        startActivity(intent);
        finish();
    }
}
