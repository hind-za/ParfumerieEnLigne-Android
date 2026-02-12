package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private TextView tvRegisterLink;

    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = new PrefsManager(this);

        // Si déjà connecté => MainActivity
        if (prefs.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnSignUp.setOnClickListener(v -> attemptRegister());

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        setupBottomNavigation();
    }

    private void attemptRegister() {
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String confirm = etConfirmPassword.getText() == null ? "" : etConfirmPassword.getText().toString();

        // Validation des champs
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Adresse email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 CORRECTION : Sauvegarder avec le mot de passe
        prefs.saveUser(username, "", email, password);

        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });

        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, CategoryActivity.class));
            finish();
        });

        navOrder.setOnClickListener(v -> {
            // 🔥 Vérification ajoutée
            if (prefs.isLogged()) {
                startActivity(new Intent(RegisterActivity.this, CartActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        navAccount.setOnClickListener(v -> {
            // Déjà sur Account
        });
    }

    private void setActiveNav(int position) {
        TextView textHome = findViewById(R.id.text_home);
        TextView textCategory = findViewById(R.id.text_category);
        TextView textOrder = findViewById(R.id.text_order);
        TextView textAccount = findViewById(R.id.text_account);

        int inactive = getResources().getColor(android.R.color.darker_gray);
        int active = getResources().getColor(android.R.color.holo_red_light);

        textHome.setTextColor(inactive);
        textCategory.setTextColor(inactive);
        textOrder.setTextColor(inactive);
        textAccount.setTextColor(inactive);

        switch (position) {
            case 0: textHome.setTextColor(active); break;
            case 1: textCategory.setTextColor(active); break;
            case 2: textOrder.setTextColor(active); break;
            case 3: textAccount.setTextColor(active); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActiveNav(3);
    }
}
