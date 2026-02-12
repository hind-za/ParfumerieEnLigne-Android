package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MakeupActivity extends AppCompatActivity {

    private PrefsManager prefs;

    private Product[] products = new Product[]{
            new Product(201, "Makeup By MARIO Palette Ombres Shimmer", "Palette Ombres Shimmer", 390.0,
                    R.drawable.eyeshadow, "Makeup", 5, false, false, null, null),
            new Product(202, "Foundation SHEGLAM HD Pro 30ml", "Fond de teint haute définition", 450.0,
                    R.drawable.foundation, "Makeup", 5, false, false, null, null),
            new Product(203, "YVES SAINT LAURENT Rouge Pur", "Rouge à lèvres iconique", 320.0,
                    R.drawable.rougealevre, "Makeup", 5, false, false, null, null),
            new Product(204, "MAYBELLINE SKY HIGH MASCARA", "Mascara volume intense", 280.0,
                    R.drawable.mascara, "Makeup", 5, false, false, null, null)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup);

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
                    Intent intent = new Intent(MakeupActivity.this, ProductDetailActivity.class);
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
                    Toast.makeText(MakeupActivity.this,
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
                        Toast.makeText(MakeupActivity.this,
                                product.getName() + " retiré des favoris", Toast.LENGTH_SHORT).show();
                    } else {
                        WishlistManager.add(product);
                        btn.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(MakeupActivity.this,
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
                startActivity(new Intent(MakeupActivity.this, MainActivity.class));
                finish();
            });
        }

        if(navCategory != null){
            navCategory.setOnClickListener(v -> {
                startActivity(new Intent(MakeupActivity.this, CategoryActivity.class));
                finish();
            });
        }

        if(navOrder != null){
            navOrder.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    startActivity(new Intent(MakeupActivity.this, CartActivity.class));
                    finish();
                } else {
                    Toast.makeText(MakeupActivity.this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MakeupActivity.this, LoginActivity.class));
                }
            });
        }

        if(navAccount != null){
            navAccount.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    Intent intent = new Intent(MakeupActivity.this, MainActivity.class);
                    intent.putExtra("show_profile", true);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(MakeupActivity.this, LoginActivity.class));
                }
            });
        }
    }
}