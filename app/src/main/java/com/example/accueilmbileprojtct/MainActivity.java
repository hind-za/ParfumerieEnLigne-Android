package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private Set<String> sentNotifications = new HashSet<>();
    private int unreadNotifications = 0;
    private NotificationHelper notificationHelper;
    private FloatingActionButton fabChatbot;
    private LinearLayout navHome, navCategory, navOrder, navAccount;
    private ImageView icNotification, icCart;
    private ViewPager2 bannerViewPager;
    private View dot1, dot2, dot3;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    private RecyclerView productsRecycler;

    private FrameLayout fragmentContainer;
    private ScrollView mainContent;
    private LinearLayout header;

    private PrefsManager prefs;

    private EditText searchEditText;
    private CardView searchSuggestionsCard;
    private RecyclerView searchSuggestionsRecycler;
    private SearchSuggestionAdapter suggestionAdapter;
    private ProductDatabase productDatabase;

    private int[] bannerImages = {
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        icNotification = findViewById(R.id.ic_notification);
        TextView badge = findViewById(R.id.badge_notification);

        updateBadge();

        icNotification.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
        });

        handler = new Handler();

        requestNotificationPermission();
        notificationHelper = new NotificationHelper(this);

        setupBannerCarousel();
        setupProducts();

        prefs = new PrefsManager(this);
        productDatabase = ProductDatabase.getInstance();

        setupListeners();
        simulateNotifications();
        setupSearch();
        checkShowProfile();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void updateBadge() {
        TextView badge = findViewById(R.id.badge_notification);
        if (unreadNotifications > 0) {
            badge.setText(String.valueOf(unreadNotifications));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private void checkShowProfile() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_profile", false)) {
            showProfileFragment();
            setActiveNav(3);
        }
    }

    private void clearBadge() {
        TextView badge = findViewById(R.id.badge_notification);
        badge.setText("0");
        badge.setVisibility(View.GONE);
    }

    private void initViews() {
        bannerViewPager = findViewById(R.id.banner_viewpager);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        icNotification = findViewById(R.id.ic_notification);
        icCart = findViewById(R.id.ic_cart);
        fabChatbot = findViewById(R.id.fab_chatbot);

        productsRecycler = findViewById(R.id.products_recycler);

        fragmentContainer = findViewById(R.id.fragment_container);
        mainContent = findViewById(R.id.main_content);
        header = findViewById(R.id.header);

        searchEditText = findViewById(R.id.et_search);
        searchSuggestionsCard = findViewById(R.id.search_suggestions_card);
        searchSuggestionsRecycler = findViewById(R.id.search_suggestions_recycler);
    }

    private void setupSearch() {
        searchSuggestionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        suggestionAdapter = new SearchSuggestionAdapter(new ArrayList<>());
        searchSuggestionsRecycler.setAdapter(suggestionAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.isEmpty()) {
                    searchSuggestionsCard.setVisibility(View.GONE);
                } else {
                    List<Product> results = productDatabase.searchProducts(query);

                    if (results.isEmpty()) {
                        searchSuggestionsCard.setVisibility(View.GONE);
                    } else {
                        suggestionAdapter.updateSuggestions(results);
                        searchSuggestionsCard.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mainContent.setOnClickListener(v -> {
            searchSuggestionsCard.setVisibility(View.GONE);
            searchEditText.clearFocus();
        });
    }

    private void setupBannerCarousel() {
        BannerAdapter adapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(adapter);

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots(position);
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % bannerImages.length;
                bannerViewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void updateDots(int position) {
        dot1.setBackgroundResource(position == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot2.setBackgroundResource(position == 1 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot3.setBackgroundResource(position == 2 ? R.drawable.dot_active : R.drawable.dot_inactive);
    }

    private void setupProducts() {
        List<Product> products = new ArrayList<>();

        // 🔥 Produit 1 : YSL LIBRE - MOST POPULAR + EN PROMO
        products.add(new Product(1, "LIBRE Intense Eau de Parfum 90 ml",
                "Eau de parfum intense", 1709.0, R.drawable.ysllibre, "Parfum",
                5, true, true, "2200.0", null));

        // 🔥 Produit 2 : Miss Dior - EN PROMO
        products.add(new Product(2, "Miss Dior Absolutely Blooming",
                "Parfum emblématique", 1500.0, R.drawable.missdior, "Parfum",
                5, false, false, "2000.0", null));

        // 🔥 Produit 3 : Palette MARIO - MOST POPULAR
        products.add(new Product(3, "Makeup By MARIO Palette Ombres Shimmer",
                "Palette Ombres Shimmer", 390.0, R.drawable.eyeshadow, "Makeup",
                5, true, true, null, null));

        // 🔥 Produit 4 : La Roche-Posay - EN PROMO
        products.add(new Product(6, "La ROCHE-POSAY Moisturizer",
                "Soin anti-imperfections", 310.0, R.drawable.moisturizer, "Skincare",
                5, false, false, "380.0", null));

        productsRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        ProductAdapter adapter = new ProductAdapter(this, products, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }

            @Override
            public void onWishlistClick(Product product) {
                if (product.isFavorite()) {
                    WishlistManager.remove(product);
                    product.setFavorite(false);
                } else {
                    WishlistManager.add(product);
                    product.setFavorite(true);
                }
                productsRecycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onAddToCartClick(Product product) {
                CartManager.getInstance().addToCart(product);
                Toast.makeText(MainActivity.this, product.getName() + " ajouté au panier", Toast.LENGTH_SHORT).show();
            }
        });

        productsRecycler.setAdapter(adapter);
    }

    private void setupListeners() {
        icNotification.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
            unreadNotifications = 0;
            updateBadge();
        });

        icCart.setOnClickListener(v -> showToast("Cart"));

        fabChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        icCart.setOnClickListener(v -> {
            if (prefs.isLogged()) {
                startActivity(new Intent(this, CartActivity.class));
            } else {
                showToast("Veuillez vous connecter pour accéder au panier");
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        setupBottomNavListeners();
    }

    private void setupBottomNavListeners() {
        navHome = findViewById(R.id.nav_home);
        navCategory = findViewById(R.id.nav_category);
        navOrder = findViewById(R.id.nav_order);
        navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> {
            showMainContent();
            setActiveNav(0);
        });

        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryActivity.class));
            finish();
        });

        navOrder.setOnClickListener(v -> {
            if (prefs.isLogged()) {
                setActiveNav(2);
                startActivity(new Intent(this, CartActivity.class));
                finish();
            } else {
                showToast("Connectez-vous pour accéder au panier");
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        navAccount.setOnClickListener(v -> {
            if (prefs.isLogged()) {
                showProfileFragment();
                setActiveNav(3);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void showProfileFragment() {
        if (mainContent != null) {
            mainContent.setVisibility(View.GONE);
        }
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }
        if (header != null) {
            header.setVisibility(View.GONE);
        }
        searchSuggestionsCard.setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .commit();
    }

    private void showMainContent() {
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }
        if (mainContent != null) {
            mainContent.setVisibility(View.VISIBLE);
        }
        if (header != null) {
            header.setVisibility(View.VISIBLE);
        }
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void simulateNotifications() {
        runnable = new Runnable() {
            @Override
            public void run() {
                sendUniqueNotification("Promo spéciale", "Découvrez nos offres du jour !", "home");
                sendUniqueNotification("Promo", "Profitez de -20% sur tous les produits", "category");

                handler.postDelayed(this, 60000);
            }
        };
        handler.post(runnable);
    }

    private void sendUniqueNotification(String title, String message, String target) {
        String key = title + message;
        if (!sentNotifications.contains(key)) {
            notificationHelper.sendNotification(title, message, target);
            sentNotifications.add(key);

            unreadNotifications++;
            updateBadge();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentContainer != null && fragmentContainer.getVisibility() != View.VISIBLE) {
            setActiveNav(0);
        }

        if (handler != null && runnable != null) {
            handler.postDelayed(runnable, 3000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}