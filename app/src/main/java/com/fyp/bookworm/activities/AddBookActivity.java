package com.fyp.bookworm.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.MaterialModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    ImageView addBookImage;
    EditText addBookName, addBookCity, addBookPrice, addBookQuantity, addBookContactNo;
    Spinner addBookSpinner;
    Button addBookBtn;
    String[] categoriesList = {"Books", "Newspapers", "Thesis"};
    String category;
    Uri pathUri;
    ProgressDialog progressDialog;
    int IMAGE_PICKER_CODE = 12;
    DatabaseReference databaseReference;
    String uid;
    StorageReference storageReference;
    StorageReference sr;

    // Use ActivityResultLauncher to handle image picker
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    pathUri = result.getData().getData();
                    if (pathUri != null) {
                        Picasso.get().load(pathUri).into(addBookImage);
                    }
                }
            });

    // Handle permission request (if required for Android versions below 13)
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchImagePicker();
                } else {
                    Toast.makeText(AddBookActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        addBookImage = findViewById(R.id._addBookImage);
        addBookName = findViewById(R.id._addBookName);
        addBookCity = findViewById(R.id._addBookCity);
        addBookPrice = findViewById(R.id._addBookPrice);
        addBookQuantity = findViewById(R.id._addBookQuantity);
        addBookContactNo = findViewById(R.id._addBookContact);
        addBookSpinner = findViewById(R.id._addBookSpinner);
        addBookBtn = findViewById(R.id._addBookBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        storageReference = FirebaseStorage.getInstance().getReference("Books Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddBookActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoriesList);
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        addBookSpinner.setAdapter(arrayAdapter);

        addBookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = addBookSpinner.getSelectedItem().toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Image picker on click
        addBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImagePickerPermission();
            }
        });

        // Add book button click
        addBookBtn.setOnClickListener(view -> {
            String name = addBookName.getText().toString();
            String city = addBookCity.getText().toString();
            String price = addBookPrice.getText().toString();
            String quantity = addBookQuantity.getText().toString();
            String contactNo = addBookContactNo.getText().toString();

            if (name.isEmpty() || city.isEmpty() || price.isEmpty() || quantity.isEmpty() || contactNo.isEmpty()) {
                Toast.makeText(AddBookActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                UploadData();
            }
        });
    }

    // Method to check permissions
    private void checkImagePickerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // No permission needed for Android 13+
            launchImagePicker();
        } else {
            // For Android 12 and below, check permission
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    // Method to launch image picker
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Choose Image"));
    }

    private void UploadData() {
        String name = addBookName.getText().toString();
        String city = addBookCity.getText().toString();
        String price = addBookPrice.getText().toString();
        String quantity = addBookQuantity.getText().toString();
        String contactNo = addBookContactNo.getText().toString();

        if (pathUri != null) {
            progressDialog.show();
            sr = storageReference.child(pathUri.getLastPathSegment());
            sr.putFile(pathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            SaveData(name, city, price, quantity, contactNo, uri.toString());
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(e -> progressDialog.dismiss());
                }
            }).addOnFailureListener(e -> Toast.makeText(AddBookActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveData(String name, String city, String price, String quantity, String contactNo, String image) {
        String id = databaseReference.push().getKey();
        MaterialModel materialModel = new MaterialModel(image, name, city, price, quantity, contactNo, category, id, uid);
        if (id != null) {
            databaseReference.child(id).setValue(materialModel).addOnSuccessListener(unused -> {
                Toast.makeText(AddBookActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(AddBookActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
        }
    }
}