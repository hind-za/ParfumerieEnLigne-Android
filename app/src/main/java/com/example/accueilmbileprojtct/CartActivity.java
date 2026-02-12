package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private LinearLayout emptyCartLayout;
    //private ImageView icNotification, icCart;
    private LinearLayout summaryLayout;
    private ScrollView cartScrollView;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private Button btnCheckout;
    private List<CartItem> cartItems;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        prefs = new PrefsManager(this);

        initViews();
        setupListeners();
        loadCart();
    }

    private void initViews() {
        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        emptyCartLayout = findViewById(R.id.emptyCartLayout);
        summaryLayout = findViewById(R.id.summaryLayout);
        cartScrollView = findViewById(R.id.cartScrollView);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

//        icNotification = findViewById(R.id.ic_notification);
//        icCart = findViewById(R.id.ic_cart);

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void setupListeners() {
//        icNotification.setOnClickListener(v -> showToast("Notifications"));
//        icCart.setOnClickListener(v -> {
//            // Vérifier si on est déjà dans CartActivity
//            if (!(CartActivity.this instanceof CartActivity)) {
//                startActivity(new Intent(CartActivity.this, CartActivity.class));
//            } else {
//                Toast.makeText(CartActivity.this, "Vous êtes déjà dans le panier", Toast.LENGTH_SHORT).show();
//            }
//        });

        setupBottomNavListeners();
    }

    private void setupBottomNavListeners() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCategory = findViewById(R.id.nav_category);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navAccount = findViewById(R.id.nav_account);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, MainActivity.class));
            finish();
        });

        navCategory.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, CategoryActivity.class));
            finish();
        });

        navOrder.setOnClickListener(v -> setActiveNav(2));

        navAccount.setOnClickListener(v -> {
            if (prefs.isLogged()) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.putExtra("show_profile", true);
                startActivity(intent);
                finish();
            } else {
                startActivity(new Intent(CartActivity.this, LoginActivity.class));
                finish();
            }
        });

        setActiveNav(2);
    }

    private void loadCart() {
        cartItems = CartManager.getInstance().getCartItems();
        cartItemsContainer.removeAllViews();

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartItems();
        }
    }

    private void showEmptyCart() {
        emptyCartLayout.setVisibility(View.VISIBLE);
        cartScrollView.setVisibility(View.GONE);
        summaryLayout.setVisibility(View.GONE);
    }

    private void showCartItems() {
        emptyCartLayout.setVisibility(View.GONE);
        cartScrollView.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.VISIBLE);

        for (CartItem item : cartItems) {
            addCartItemView(item);
        }

        updateTotals();
    }

    private void addCartItemView(CartItem item) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_cart_simple, cartItemsContainer, false);

        ImageView image = itemView.findViewById(R.id.cartItemImage);
        TextView name = itemView.findViewById(R.id.cartItemName);
        TextView price = itemView.findViewById(R.id.cartItemPrice);
        TextView quantity = itemView.findViewById(R.id.tvQuantity);
        TextView total = itemView.findViewById(R.id.cartItemTotal);
        ImageButton btnDecrease = itemView.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = itemView.findViewById(R.id.btnIncrease);
        ImageButton btnDelete = itemView.findViewById(R.id.btnDelete);

        image.setImageResource(item.getProduct().getImageResource());
        name.setText(item.getProduct().getName());
        price.setText(String.format(Locale.FRENCH, "%.2f DH", item.getProduct().getPrice()));
        quantity.setText(String.valueOf(item.getQuantity()));
        total.setText(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()));

        int buttonColor = getResources().getColor(R.color.background_pink);
        btnIncrease.setBackgroundColor(buttonColor);
        btnDecrease.setBackgroundColor(buttonColor);
//

        btnIncrease.setPadding(16, 8, 16, 8);
        btnDecrease.setPadding(16, 8, 16, 8);

        btnDecrease.setOnClickListener(v -> handleDecreaseQuantity(item, quantity, total));
        btnIncrease.setOnClickListener(v -> handleIncreaseQuantity(item, quantity, total));
        btnDelete.setOnClickListener(v -> handleDeleteItem(item));

        cartItemsContainer.addView(itemView);
    }

    private void handleDecreaseQuantity(CartItem item, TextView quantityView, TextView totalView) {
        int newQty = item.getQuantity() - 1;

        if (newQty <= 0) {
            CartManager.getInstance().removeFromCart(item.getProduct().getId());
            loadCart();
        } else {
            CartManager.getInstance().updateQuantity(item.getProduct().getId(), newQty);
            quantityView.setText(String.valueOf(newQty));
            totalView.setText(String.format(Locale.FRENCH, "%.2f DH",
                    item.getProduct().getPrice() * newQty));
            updateTotals();
        }
    }

    private void handleIncreaseQuantity(CartItem item, TextView quantityView, TextView totalView) {
        int newQty = item.getQuantity() + 1;

        CartManager.getInstance().updateQuantity(item.getProduct().getId(), newQty);
        quantityView.setText(String.valueOf(newQty));
        totalView.setText(String.format(Locale.FRENCH, "%.2f DH",
                item.getProduct().getPrice() * newQty));
        updateTotals();
    }

    private void handleDeleteItem(CartItem item) {
        CartManager.getInstance().removeFromCart(item.getProduct().getId());
        Toast.makeText(this, R.string.item_removed, Toast.LENGTH_SHORT).show();
        loadCart();
    }

    private void updateTotals() {
        double total = CartManager.getInstance().getTotalPrice();
        String formattedTotal = String.format(Locale.FRENCH, "%.2f DH", total);
        tvSubtotal.setText(formattedTotal);
        tvTotal.setText(formattedTotal);
    }

    // ✅ MÉTHODE CHECKOUT CORRIGÉE - Utilise Parcelable
    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, R.string.cart_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!prefs.isLogged()) {
            Toast.makeText(this, "Veuillez vous connecter pour continuer", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CartActivity.this, LoginActivity.class));
            return;
        }

        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);

        // Passer le montant total
        double totalAmount = CartManager.getInstance().getTotalPrice();
        intent.putExtra("TOTAL_PANIER", totalAmount);

        // Passer le nombre d'articles
        int itemCount = cartItems.size();
        intent.putExtra("item_count", itemCount);

        // ✅ UTILISER putParcelableArrayList au lieu de putExtra
        ArrayList<CartItem> itemsList = new ArrayList<>(cartItems);
        intent.putParcelableArrayListExtra("cart_items", itemsList);

        // Construire la description des produits
        StringBuilder produitsDescription = new StringBuilder();
        for (CartItem item : cartItems) {
            produitsDescription.append(item.getProduct().getName())
                    .append(" (x")
                    .append(item.getQuantity())
                    .append(") - ")
                    .append(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()))
                    .append("\n");
        }
        intent.putExtra("PRODUITS_COMMANDE", produitsDescription.toString().trim());

        startActivity(intent);
        Toast.makeText(this, "Redirection vers le paiement...", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
        setActiveNav(2);
    }
}