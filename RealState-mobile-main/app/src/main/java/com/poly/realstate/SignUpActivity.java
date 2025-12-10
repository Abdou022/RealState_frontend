package com.poly.realstate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.poly.realstate.models.UserResponse;
import com.poly.realstate.network.RetrofitClient;
import com.poly.realstate.ApiService;
import com.poly.realstate.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nomPrenomEditText, emailEditText, telEditText, passwordEditText;
    private ImageView profileImage;
    private Button btnCreerCompte;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // ===== Init views =====
        nomPrenomEditText = findViewById(R.id.nom_prénom);
        emailEditText = findViewById(R.id.email);
        telEditText = findViewById(R.id.tel);
        passwordEditText = findViewById(R.id.password);
        profileImage = findViewById(R.id.profileImage);
        btnCreerCompte = findViewById(R.id.btn_creerCompte);

        // ===== Choisir image =====
        profileImage.setOnClickListener(v -> openImageChooser());

        // ===== Clic sur créer compte =====
        btnCreerCompte.setOnClickListener(v -> registerUser());
    }

    // ===== Ouvrir la galerie =====
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    // ===== Fonction pour s’inscrire =====
    private void registerUser() {
        String name = nomPrenomEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = telEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== Convertir en RequestBody =====
        RequestBody namePart = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody emailPart = RequestBody.create(email, MediaType.parse("text/plain"));
        RequestBody phonePart = RequestBody.create(phone, MediaType.parse("text/plain"));
        RequestBody passwordPart = RequestBody.create(password, MediaType.parse("text/plain"));

        // ===== Convertir l'image en MultipartBody.Part =====
        String imagePath = FileUtils.getPath(this, selectedImageUri);
        if (imagePath == null) {
            Toast.makeText(this, "Impossible de récupérer l'image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // ===== Appel API =====
        ApiService api = RetrofitClient.getInstance().getApi();
        Call<UserResponse> call = api.registerUser(namePart, emailPart, phonePart, passwordPart, imagePart);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    // ===== Sauvegarde dans SharedPreferences =====
                    SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("userId", user.getId());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.putString("phone", user.getPhone());
                    editor.putString("image", user.getImage());
                    editor.apply();

                    Toast.makeText(SignUpActivity.this, user.getMessage(), Toast.LENGTH_SHORT).show();

                    // ===== Passer à l'activité suivante =====
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    finish();

                } else {
                    Toast.makeText(SignUpActivity.this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
