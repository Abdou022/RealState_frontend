package com.poly.realstate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.poly.realstate.models.LoginResponse;
import com.poly.realstate.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button btnConfirm;
    private TextView creerCompteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in); // assure-toi que c’est le bon layout

        // ===== Init views =====
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        btnConfirm = findViewById(R.id.btn_confirm);
        creerCompteBtn = findViewById(R.id.creer_compte_btn);

        // ===== Clic sur login =====
        btnConfirm.setOnClickListener(v -> loginUser());

        // ===== Clic sur créer compte =====
        creerCompteBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== Appel API =====
        Call<LoginResponse> call = RetrofitClient.getInstance().getApi().loginUser(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // ===== Sauvegarde dans SharedPreferences =====
                    SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("userId", loginResponse.getUser().getId());
                    editor.putString("fullName", loginResponse.getUser().getFullName());
                    editor.putString("email", loginResponse.getUser().getEmail());
                    editor.putString("phone", loginResponse.getUser().getPhone());
                    editor.putString("image", loginResponse.getUser().getImage());
                    editor.apply();

                    Toast.makeText(SignInActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // ===== Naviguer vers MainActivity =====
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                    finish();

                } else {
                    Toast.makeText(SignInActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
