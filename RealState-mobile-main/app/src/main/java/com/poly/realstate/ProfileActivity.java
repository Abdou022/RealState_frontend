package com.poly.realstate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final String IMAGE_BASE_URL = "http://192.168.1.11:8000/uploads/profiles/";
    private static final String API_BASE_URL = "http://192.168.1.11:8000/users/"; // GET /users/{id}

    private ImageView profileImage;
    private TextView txtFullName, txtEmail, txtPhone;
    private Button btnEditProfile;

    private int userId;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        profileImage = findViewById(R.id.profileImage);
        txtFullName = findViewById(R.id.txtFullNameValue);
        txtEmail = findViewById(R.id.txtEmailValue);
        txtPhone = findViewById(R.id.txtPhoneValue);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // SharedPreferences
        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1); // -1 si non trouvé

        if(userId != -1){
            fetchUserProfile(userId);
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }

        // Bouton Modifier Profil
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    // ----- Charger le profil depuis l'API -----
    private void fetchUserProfile(int id) {
        String url = API_BASE_URL + id;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String fullName = response.getString("fullName");
                        String email = response.getString("email");
                        String phone = response.getString("phone");
                        String image = response.getString("image");

                        txtFullName.setText(fullName);
                        txtEmail.setText(email);
                        txtPhone.setText(phone);

                        if(image != null && !image.isEmpty()){
                            Picasso.get()
                                    .load(IMAGE_BASE_URL + image)
                                    .placeholder(R.drawable.login)
                                    .into(profileImage);
                        }

                        // Sauvegarder dans le cache
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("full_name", fullName);
                        editor.putString("email", email);
                        editor.putString("phone", phone);
                        editor.putString("image", image);
                        editor.apply();

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Erreur parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ProfileActivity.this, "Erreur API: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    // ----- Afficher le popup de modification -----
    private void showEditProfileDialog() {
        // Inflater le layout du popup
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText edtFullName = dialogView.findViewById(R.id.edtFullName);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        Button btnSaveDialog = dialogView.findViewById(R.id.btnSaveDialog);

        // Pré-remplir les champs avec les valeurs actuelles
        edtFullName.setText(txtFullName.getText().toString());
        edtPhone.setText(txtPhone.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnSaveDialog.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            updateUserProfile(fullName, phone, password, dialog);
        });

        dialog.show();
    }

    // ----- Appeler l'API PUT pour mettre à jour le profil -----
    private void updateUserProfile(String fullName, String phone, String password, AlertDialog dialog){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fullName", fullName);
            jsonBody.put("phone", phone);
            if(!password.isEmpty()){
                jsonBody.put("plain_password", password);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        String url = API_BASE_URL + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();

                    // Mettre à jour les vues
                    txtFullName.setText(fullName);
                    txtPhone.setText(phone);

                    // Mettre à jour le cache
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("fullName", fullName);
                    editor.putString("phone", phone);
                    editor.apply();

                    dialog.dismiss();
                },
                error -> Toast.makeText(ProfileActivity.this, "Erreur API: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
