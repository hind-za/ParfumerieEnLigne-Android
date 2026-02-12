package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SkincareActivity extends AppCompatActivity {

    private PrefsManager prefs;

    private Product[] products = new Product[]{
            new Product(301, "La Roche-Posay Moisturizer", "Soin hydratant", 380.0,
                    R.drawable.moisturizer, "Skincare", 5, false, false, null, null),
            new Product(302, "THE ORDINARY Niacinamide 10%", "Sérum 30ML", 520.0,
                    R.drawable.serum, "Skincare", 5, false, false, null, null),
            new Product(303, "Cerave Gel Moussant", "Nettoyant 473ml", 220.0,
                    R.drawable.gel, "Skincare", 5, false, false, null, null),
            new Product(304, "BEAUTY OF JOSEON Relief Sun", "SPF 50ML", 310.0,
                    R.drawable.sunscreen, "Skincare", 5, false, false, null, null)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skincare);

        prefs = new PrefsManager(this);

        setupProductCards();
        setupAddToCartButtons();
        setupWishlistButtons();
        setupBottomNavigation();
    }

    private void setupProductCards() {
        int[] cardIds = {
                R.id.product_card_1,
                R.id.product_card_2,
                R.id.product_card_3,
                R.id.product_card_4
        };

        for (int i = 0; i < cardIds.length; i++) {
            LinearLayout card = findViewById(cardIds[i]);
            final Product product = products[i];

            if (card != null) {
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(SkincareActivity.this, ProductDetailActivity.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                });
            }
        }
    }

    private void setupAddToCartButtons() {
        int[] buttonIds = { R.id.btnAdd1, R.id.btnAdd2, R.id.btnAdd3, R.id.btnAdd4 };

        for (int i = 0; i < buttonIds.length; i++) {
            RelativeLayout btn = findViewById(buttonIds[i]);
            final Product product = products[i];

            if (btn != null) {
                btn.setOnClickListener(v -> {
                    CartManager.getInstance().addToCart(product);
                    Toast.makeText(SkincareActivity.this,
                            product.getName() + " ajouté au panier", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void setupWishlistButtons() {
        int[] wishlistIds = { R.id.btnWishlist1, R.id.btnWishlist2, R.id.btnWishlist3, R.id.btnWishlist4 };

        for (int i = 0; i < wishlistIds.length; i++) {
            ImageButton btn = findViewById(wishlistIds[i]);
            final Product product = products[i];

            if (btn != null) {
                btn.setImageResource(WishlistManager.isInWishlist(product) ?
                        R.drawable.ic_favorite : R.drawable.ic_favorite_border);

                btn.setOnClickListener(v -> {
                    if (WishlistManager.isInWishlist(product)) {
                        WishlistManager.remove(product);
                        btn.setImageResource(R.drawable.ic_favorite_border);
                        Toast.makeText(SkincareActivity.this,
                                product.getName() + " retiré des favoris", Toast.LENGTH_SHORT).show();
                    } else {
                        WishlistManager.add(product);
                        btn.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(SkincareActivity.this,
                                product.getName() + " ajouté aux favoris", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        if(navHome != null){
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(SkincareActivity.this, MainActivity.class));
                finish();
            });
        }

        if(navCategory != null){
            navCategory.setOnClickListener(v -> {
                startActivity(new Intent(SkincareActivity.this, CategoryActivity.class));
                finish();
            });
        }

        if(navOrder != null){
            navOrder.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    startActivity(new Intent(SkincareActivity.this, CartActivity.class));
                    finish();
                } else {
                    Toast.makeText(SkincareActivity.this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SkincareActivity.this, LoginActivity.class));
                }
            });
        }

        if(navAccount != null){
            navAccount.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    Intent intent = new Intent(SkincareActivity.this, MainActivity.class);
                    intent.putExtra("show_profile", true);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(SkincareActivity.this, LoginActivity.class));
                }
            });
        }
    }
}