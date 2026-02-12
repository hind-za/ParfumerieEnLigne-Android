package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ParfumActivity extends AppCompatActivity {

    private PrefsManager prefs;

    private Product[] products = new Product[]{
            new Product(101, "Yves Saint Laurent Libre", "Eau de parfum intense", 1709.0,
                    R.drawable.ysllibre, "Parfum", 5, false, false, null, null),
            new Product(102, "Chanel Coco Eau de Parfum 50 ml", "Eau de parfum", 1200.0,
                    R.drawable.parfum2, "Parfum", 5, false, false, null, null),
            new Product(103, "Givenchy – L’Interdit Rouge Eau de Parfum", "Eau de parfum intense", 3000.0,
                    R.drawable.interdit, "Parfum", 5, false, false, null, null),
            new Product(104, "Miss Dior Absolutely Blooming", "Eau de parfum floral", 2000.0,
                    R.drawable.missdior, "Parfum", 5, false, false, null, null)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parfum);

        prefs = new PrefsManager(this);

        setupProductCards();
        setupAddToCartButtons();
        setupWishlistButtons();
        setupBottomNavigation(); // barre de nav corrigée
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
                    Intent intent = new Intent(ParfumActivity.this, ProductDetailActivity.class);
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
                    Toast.makeText(ParfumActivity.this,
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
                        Toast.makeText(ParfumActivity.this,
                                product.getName() + " retiré des favoris", Toast.LENGTH_SHORT).show();
                    } else {
                        WishlistManager.add(product);
                        btn.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(ParfumActivity.this,
                                product.getName() + " ajouté aux favoris", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void setupBottomNavigation() {
        // on récupère les boutons correctement
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        if(navHome != null){
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(ParfumActivity.this, MainActivity.class));
                finish();
            });
        }

        if(navCategory != null){
            navCategory.setOnClickListener(v -> {
                startActivity(new Intent(ParfumActivity.this, CategoryActivity.class));
                finish();
            });
        }

        if(navOrder != null){
            navOrder.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    startActivity(new Intent(ParfumActivity.this, CartActivity.class));
                    finish();
                } else {
                    Toast.makeText(ParfumActivity.this, "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ParfumActivity.this, LoginActivity.class));
                }
            });
        }

        if(navAccount != null){
            navAccount.setOnClickListener(v -> {
                if (prefs.isLogged()) {
                    Intent intent = new Intent(ParfumActivity.this, MainActivity.class);
                    intent.putExtra("show_profile", true);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(ParfumActivity.this, LoginActivity.class));
                }
            });
        }
    }
}
