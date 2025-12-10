package com.poly.realstate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.poly.realstate.models.ApiResponse;
import com.poly.realstate.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreationHomeActivity extends AppCompatActivity {

    EditText edtTitle, edtPrice, edtAddress, edtArea, edtRooms, edtDescription;
    Button btnSave, btnAddImage;
    ImageView imagePreview;

    Uri selectedImageUri = null;

    ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creationhome);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCreationhome);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        edtTitle = findViewById(R.id.edtHomeTitle);
        edtPrice = findViewById(R.id.edtHomePrice);
        edtAddress = findViewById(R.id.edtHomeAddress);
        edtArea = findViewById(R.id.edtHomeArea);
        edtRooms = findViewById(R.id.edtNbrChambres);
        edtDescription = findViewById(R.id.edtHomeDescription);
        btnSave = findViewById(R.id.btnHomeSave);
        btnAddImage = findViewById(R.id.btnAddImages);
        imagePreview = findViewById(R.id.imagePreview);

        // Image Picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imagePreview.setImageURI(selectedImageUri);
                    }
                }
        );

        btnAddImage.setOnClickListener(v -> pickImage());

        btnSave.setOnClickListener(v -> createHouse());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void createHouse() {

        String title = edtTitle.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String area = edtArea.getText().toString().trim();
        String rooms = edtRooms.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty() || address.isEmpty() ||
                area.isEmpty() || rooms.isEmpty() || description.isEmpty()) {

            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get ownerId from SharedPreferences
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int ownerId = sp.getInt("userId", -1);

        if (ownerId == -1) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build multipart
        File imageFile = createFileFromUri(selectedImageUri);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), reqFile);

        RequestBody ownerIdRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ownerId));
        RequestBody titleRB = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody priceRB = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody addressRB = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody areaRB = RequestBody.create(MediaType.parse("text/plain"), area);
        RequestBody roomsRB = RequestBody.create(MediaType.parse("text/plain"), rooms);
        RequestBody descRB = RequestBody.create(MediaType.parse("text/plain"), description);

        ApiService api = RetrofitClient.getInstance().getApi();

        Call<ApiResponse> call = api.createHouse(
                ownerIdRB,
                titleRB,
                descRB,
                priceRB,
                addressRB,
                areaRB,
                roomsRB,
                imagePart
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiRes = response.body();
                    Log.e("AAA", apiRes.getMessage());
                    Toast.makeText(CreationHomeActivity.this, apiRes.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", errorBody);
                        Toast.makeText(CreationHomeActivity.this, errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreationHomeActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Convert URI → File
    private File createFileFromUri(Uri uri) {
        File file = new File(getCacheDir(), getFileName(uri));

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    // Get real file name
    private String getFileName(Uri uri) {
        String name = "image.jpg";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            name = cursor.getString(nameIndex);
            cursor.close();
        }
        return name;
    }
}
