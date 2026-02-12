package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etLoginEmail, etLoginPassword;
    private MaterialButton btnSignIn;
    private TextView tvLoginLink, tvForgot;

    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = new PrefsManager(this);

        // Si déjà connecté -> MainActivity
        if (prefs.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Init views
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tvForgot = findViewById(R.id.tvForgot);

        btnSignIn.setOnClickListener(v -> attemptLogin());

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Fonction 'mot de passe oublié' non implémentée", Toast.LENGTH_SHORT).show()
        );

        setupBottomNavigation();
    }

    private void attemptLogin() {
        String email = etLoginEmail.getText() == null ? "" : etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText() == null ? "" : etLoginPassword.getText().toString();

        // Validation des champs
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Veuillez remplir les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation format email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 CORRECTION : Vérifier email ET mot de passe
        if (prefs.verifyCredentials(email, password)) {
            // ✅ Connexion réussie
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // ❌ Identifiants incorrects
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show();
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CategoryActivity.class));
            finish();
        });

        navOrder.setOnClickListener(v -> {
            // 🔥 Vérification ajoutée
            if (prefs.isLogged()) {
                startActivity(new Intent(LoginActivity.this, CartActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
            }
        });

        navAccount.setOnClickListener(v -> {
            // Déjà sur la page login
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