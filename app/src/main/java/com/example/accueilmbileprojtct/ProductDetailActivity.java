package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

public class ProductDetailActivity extends AppCompatActivity {

    private Product currentProduct;
    private Product[] recommendedProducts;
    private PrefsManager prefs;
    private GridLayout recommendationsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        prefs = new PrefsManager(this);
        recommendationsGrid = findViewById(R.id.recommendations_grid);

        currentProduct = getIntent().getParcelableExtra("product");

        if (currentProduct == null) {
            Toast.makeText(this, "Erreur: produit introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayProductDetails();
        loadRecommendations();
        setupAddToCartButton();
        setupBackButton();
        setupBottomNavigation();
    }

    private void displayProductDetails() {
        ImageView productImage = findViewById(R.id.product_detail_image);
        TextView productName = findViewById(R.id.product_detail_name);
        TextView productPrice = findViewById(R.id.product_detail_price);
        TextView productDescription = findViewById(R.id.product_detail_description);
        TextView productRating = findViewById(R.id.product_detail_rating);

        productImage.setImageResource(currentProduct.getImageResource());
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("%.0f MAD", currentProduct.getPrice()));

        String description = currentProduct.getDescription();
        if (description == null || description.isEmpty()) {
            description = "Découvrez ce produit exceptionnel.";
        }
        productDescription.setText(description);

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < currentProduct.getRating(); i++) {
            stars.append("⭐");
        }
        productRating.setText(stars.toString());
    }

    private void loadRecommendations() {
        String category = currentProduct.getCategory();

        if (category != null) {
            if (category.equals("Parfum")) {
                loadParfumRecommendations();
            } else if (category.equals("Skincare")) {
                loadSkincareRecommendations();
            } else if (category.equals("Makeup")) {
                loadMakeupRecommendations();
            } else {
                recommendedProducts = new Product[]{};
            }
        } else {
            recommendedProducts = new Product[]{};
        }

        displayRecommendations();
    }

    private void loadParfumRecommendations() {
        recommendedProducts = new Product[]{
                new Product(101, "Chanel N°5", "Parfum emblématique pour femme", 850.0,
                        R.drawable.ysllibre, "Parfum", 5, false, false, null, null),
                new Product(102, "Dior Sauvage", "Eau de toilette homme", 900.0,
                        R.drawable.parfum2, "Parfum", 5, false, false, null, null),
                new Product(103, "Yves Saint Laurent Libre", "Eau de parfum intense", 1708.66,
                        R.drawable.ysllibre, "Parfum", 5, true, false, null, null),
                new Product(104, "Gucci Bloom", "Eau de parfum floral", 1200.0,
                        R.drawable.parfum2, "Parfum", 5, false, true, "200 dh", "100K+ sold")
        };
    }

    private void loadSkincareRecommendations() {
        recommendedProducts = new Product[]{
                new Product(301, "La Roche-Posay Moisturizer", "Soin hydratant", 380.0,
                        R.drawable.moisturizer, "Skincare", 5, false, false, null, null),
                new Product(302, "THE ORDINARY Niacinamide 10%", "Sérum 30ML", 520.0,
                        R.drawable.serum, "Skincare", 5, false, false, null, null),
                new Product(303, "Cerave Gel Moussant", "Nettoyant 473ml", 220.0,
                        R.drawable.gel, "Skincare", 5, false, false, null, null),
                new Product(304, "BEAUTY OF JOSEON Relief Sun", "SPF 50ML", 310.0,
                        R.drawable.sunscreen, "Skincare", 5, false, false, null, null)
        };
    }

    private void loadMakeupRecommendations() {
        recommendedProducts = new Product[]{
                new Product(201, "Foundation SHEGLAM HD Pro 30ml", "Uniform color to the complexion", 450.0,
                        R.drawable.foundation, "Makeup", 5, false, false, null, null),
                new Product(202, "YVES SAINT LAURENT Rouge pur", "Intense Color Payoff", 320.0,
                        R.drawable.rougealevre, "Makeup", 5, false, false, null, null),
                new Product(203, "Maybelline SKY HIGH Mascara", "Mascara volumisant", 400.0,
                        R.drawable.mascara, "Makeup", 5, false, false, null, null),
                new Product(204, "Makeup By MARIO Palette", "Palette Ombres Shimmer", 390.0,
                        R.drawable.eyeshadow, "Makeup", 5, false, false, null, null)
        };
    }

    private void displayRecommendations() {
        recommendationsGrid.removeAllViews();

        if (recommendedProducts == null || recommendedProducts.length == 0) {
            TextView noProducts = new TextView(this);
            noProducts.setText("Aucune recommandation disponible");
            noProducts.setTextSize(14);
            noProducts.setTextColor(Color.parseColor("#999999"));
            noProducts.setPadding(16, 16, 16, 16);
            recommendationsGrid.addView(noProducts);
            return;
        }

        for (final Product product : recommendedProducts) {
            if (product.getId() == currentProduct.getId()) {
                continue;
            }

            LinearLayout productCard = createProductCard(product);
            recommendationsGrid.addView(productCard);
        }
    }

    private LinearLayout createProductCard(final Product product) {
        // Card container
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);

        // Style de la carte avec bordure et coins arrondis
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(8);
        cardBg.setStroke(1, Color.parseColor("#EEEEEE"));
        card.setBackground(cardBg);
        card.setElevation(4f);

        // Paramètres de layout pour la grille (2 colonnes)
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (screenWidth - dpToPx(48)) / 2; // 48dp = padding total (16*2 + 8*2)

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cardWidth;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        card.setLayoutParams(params);
        card.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        // Image du produit
        ImageView image = new ImageView(this);
        image.setImageResource(product.getImageResource());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(180)
        );
        imageParams.setMargins(0, 0, 0, dpToPx(12));
        image.setLayoutParams(imageParams);

        // Nom du produit
        TextView name = new TextView(this);
        name.setText(product.getName());
        name.setTextSize(13);
        name.setTextColor(Color.BLACK);
        name.setTypeface(null, Typeface.BOLD);
        name.setMaxLines(2);
        name.setEllipsize(android.text.TextUtils.TruncateAt.END);
        name.setPadding(0, 0, 0, dpToPx(8));

        // Étoiles de notation
        TextView stars = new TextView(this);
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < product.getRating(); i++) {
            starStr.append("⭐");
        }
        stars.setText(starStr.toString());
        stars.setTextSize(10);
        stars.setPadding(0, 0, 0, dpToPx(8));

        // Prix
        TextView price = new TextView(this);
        price.setText(String.format("%.0f MAD", product.getPrice()));
        price.setTextSize(16);
        price.setTypeface(null, Typeface.BOLD);
        price.setTextColor(Color.BLACK);
        price.setPadding(0, 0, 0, dpToPx(12));

        // Bouton "Add to cart"
        RelativeLayout addToCartBtn = new RelativeLayout(this);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.parseColor("#FF69B4"));
        btnBg.setCornerRadius(4);
        addToCartBtn.setBackground(btnBg);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(40)
        );
        addToCartBtn.setLayoutParams(btnParams);

        TextView btnText = new TextView(this);
        btnText.setText("Add to cart");
        btnText.setTextColor(Color.WHITE);
        btnText.setTextSize(12);
        btnText.setTypeface(null, Typeface.BOLD);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnText.setLayoutParams(textParams);
        addToCartBtn.addView(btnText);

        // Click sur le bouton
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartManager.getInstance().addToCart(product);
                Toast.makeText(ProductDetailActivity.this,
                        product.getName() + " ajouté au panier",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Click sur la carte pour voir les détails
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
//                finish();
            }
        });

        // Ajouter tous les éléments à la carte
        card.addView(image);
        card.addView(name);
        card.addView(stars);
        card.addView(price);
        card.addView(addToCartBtn);

        return card;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupAddToCartButton() {
        RelativeLayout addToCartBtn = findViewById(R.id.add_to_cart_btn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartManager.getInstance().addToCart(currentProduct);
                Toast.makeText(ProductDetailActivity.this,
                        currentProduct.getName() + " ajouté au panier",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBackButton() {
        RelativeLayout backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));
                finish();
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailActivity.this, CategoryActivity.class));
                finish();
            }
        });

        navOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.isLogged()) {
                    startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                    finish();
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "Connectez-vous pour accéder au panier", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
                }
            }
        });

        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.isLogged()) {
                    Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    intent.putExtra("show_profile", true);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
                }
            }
        });
    }
}