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
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UpdateMaterialActivity extends AppCompatActivity {

    ImageView updateBookImage;
    EditText updateBookName, updateBookCity, updateBookPrice, updateBookQuantity, updateBookContactNo;
    Spinner updateBookSpinner;
    Button updateBookBtn;
    String[] categoriesList = {"Books", "Newspapers", "Thesis"};
    String category;
    Uri pathUri;
    ProgressDialog progressDialog;
    int IMAGE_PICKER_CODE = 12;
    MaterialModel materialModel;
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
                        Picasso.get().load(pathUri).into(updateBookImage);
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
                    Toast.makeText(UpdateMaterialActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_material);

        updateBookImage = findViewById(R.id._updateBookImage);
        updateBookName = findViewById(R.id._updateBookName);
        updateBookCity = findViewById(R.id._updateBookCity);
        updateBookPrice = findViewById(R.id._updateBookPrice);
        updateBookQuantity = findViewById(R.id._updateBookQuantity);
        updateBookContactNo = findViewById(R.id._updateBookContact);
        updateBookSpinner = findViewById(R.id._updateBookSpinner);
        updateBookBtn = findViewById(R.id._updateBookBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        storageReference = FirebaseStorage.getInstance().getReference("Books Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get and set data
        materialModel = new Gson().fromJson(getIntent().getStringExtra("Data"), MaterialModel.class);
        if (materialModel != null) {
            Picasso.get().load(materialModel.getImage()).into(updateBookImage);
            updateBookName.setText(materialModel.getName());
            updateBookCity.setText(materialModel.getCity());
            updateBookPrice.setText(materialModel.getPrice());
            updateBookQuantity.setText(materialModel.getQuantity());
            updateBookContactNo.setText(materialModel.getContactNo());
        }

        //set spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateMaterialActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoriesList);
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        updateBookSpinner.setAdapter(arrayAdapter);

        updateBookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = updateBookSpinner.getSelectedItem().toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImagePickerPermission();
            }
        });

        updateBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, city, price, quantity, contactNo;
                name = updateBookName.getText().toString();
                city = updateBookCity.getText().toString();
                price = updateBookPrice.getText().toString();
                quantity = updateBookQuantity.getText().toString();
                contactNo = updateBookContactNo.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(UpdateMaterialActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (city.isEmpty()) {
                    Toast.makeText(UpdateMaterialActivity.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                    return;
                } else if (price.isEmpty()) {
                    Toast.makeText(UpdateMaterialActivity.this, "Please Enter Price", Toast.LENGTH_SHORT).show();
                    return;
                } else if (quantity.isEmpty()) {
                    Toast.makeText(UpdateMaterialActivity.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                    return;
                } else if (contactNo.isEmpty()) {
                    Toast.makeText(UpdateMaterialActivity.this, "Please Enter Contact No", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (pathUri != null) {
                        progressDialog.show();
                        sr = storageReference.child(pathUri.getLastPathSegment());
                        sr.putFile(pathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                UpdateData(name, city, price, quantity, contactNo,materialModel.getId(), uri.toString());
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateMaterialActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                });
                    } else {
                        progressDialog.show();
                        UpdateData(name, city, price, quantity, contactNo,materialModel.getId(), "");
                    }
                }

            }
        });
        
    }

    private void UpdateData(String name, String city, String price, String quantity, String contactNo, String id, String image) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!image.isEmpty()) {
            hashMap.put("image", image);
        }
        hashMap.put("name", name);
        hashMap.put("city", city);
        hashMap.put("price", price);
        hashMap.put("quantity", quantity);
        hashMap.put("contactNo", contactNo);
        hashMap.put("category", category);

        databaseReference.child(id).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateMaterialActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMaterialActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
}