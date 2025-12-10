package com.poly.realstate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ForgetPasswordActivity extends AppCompatActivity {

    Button btn_valider;
    TextInputEditText password_ed;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        password_ed = findViewById(R.id.password);
        btn_valider = findViewById(R.id.btn_valider);

        // Get email from SignInActivity (optional, used for context)
        userEmail = getIntent().getStringExtra("user_email");

        btn_valider.setOnClickListener(v -> {
            String newPassword = password_ed.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                password_ed.setError("Le mot de passe doit contenir au moins 6 caractères");
                password_ed.requestFocus();
                return;
            }

            // Here you would save the new password locally or via API
            // For now, just show success
            Toast.makeText(this, "Mot de passe mis à jour avec succès ✅", Toast.LENGTH_SHORT).show();

            // Finish and return to previous activity
            finish();
        });
    }
}
