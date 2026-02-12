package com.example.accueilmbileprojtct;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private LinearLayout emptyContainer;
    private TextView btnGoShopping;
    private TextView tvWishlistCount;
    private ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initViews();
        setupListeners();
        loadWishlist();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_wishlist);
        emptyContainer = findViewById(R.id.empty_wishlist_container);
        btnGoShopping = findViewById(R.id.btn_go_shopping);
        tvWishlistCount = findViewById(R.id.tv_wishlist_count);
        btnBack = findViewById(R.id.btn_back);

        // Utiliser GridLayoutManager pour afficher 2 produits par ligne (comme MainActivity)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupListeners() {
        // Bouton retour
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Bouton "Découvrir nos produits"
        if (btnGoShopping != null) {
            btnGoShopping.setOnClickListener(v -> finish());
        }
    }

    private void loadWishlist() {
        List<Product> wishlist = WishlistManager.getWishlist();

        // Mettre à jour le compteur
        updateWishlistCount(wishlist.size());

        if (wishlist.isEmpty()) {
            toggleEmptyState(true);
        } else {
            toggleEmptyState(false);

            adapter = new WishlistAdapter(
                    wishlist,
                    product -> {
                        // Callback pour suppression d'un produit
                        WishlistManager.remove(product);
                        loadWishlist(); // Recharger la liste
                    },
                    () -> {
                        // ⚡ La wishlist est vide, on affiche l'état vide
                        toggleEmptyState(true);
                        updateWishlistCount(0);
                    }
            );

            recyclerView.setAdapter(adapter);
        }
    }

    private void updateWishlistCount(int count) {
        if (tvWishlistCount != null) {
            tvWishlistCount.setText(String.valueOf(count));
            // Optionnel : cacher le badge si count = 0
            tvWishlistCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger la wishlist quand on revient sur l'activité
        loadWishlist();
    }
}