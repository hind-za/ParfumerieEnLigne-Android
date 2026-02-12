package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CategoryActivity extends AppCompatActivity {

    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        prefs = new PrefsManager(this);

        setupCategoryCards();
        setupBottomNavigation();
    }

    private void setupCategoryCards() {
        LinearLayout cardParfums = findViewById(R.id.card_parfums);
        LinearLayout cardMakeup = findViewById(R.id.card_makeup);
        LinearLayout cardSkincare = findViewById(R.id.card_skincare);

        cardParfums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, ParfumActivity.class));
            }
        });

        cardMakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, MakeupActivity.class));
            }
        });

        cardSkincare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, SkincareActivity.class));
            }
        });
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                finish();
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déjà sur cette page
            }
        });

        navOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.isLogged()) {
                    startActivity(new Intent(CategoryActivity.this, CartActivity.class));
                    finish();
                } else {
                    Toast.makeText(CategoryActivity.this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                }
            }
        });

        // 🔥 MODIFICATION ICI
        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.isLogged()) {
                    // ✅ Connecté → Aller au MainActivity qui affichera le ProfileFragment
                    Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                    intent.putExtra("show_profile", true); // Flag pour afficher le profil
                    startActivity(intent);
                    finish();
                } else {
                    // ❌ Non connecté → Aller au login
                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }
}