package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etNomComplet, etAdresse, etVille, etEmail, etTelephone;
    private LinearLayout productsContainer;
    private TextView tvSousTotal, tvFraisLivraison, tvTotal;
    private Button btnContinuer;

    private double montantPanier;
    private ArrayList<CartItem> cartItems;
    private static final double FRAIS_LIVRAISON = 30.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initializeViews();
        chargerDonneesUtilisateur();
        recupererDonneesPanier();
        afficherRecapitulatif();

        btnContinuer.setOnClickListener(v -> validerEtContinuer());
    }

    private void initializeViews() {
        etNomComplet = findViewById(R.id.et_nom_complet);
        etAdresse = findViewById(R.id.et_adresse);
        etVille = findViewById(R.id.et_ville);
        etEmail = findViewById(R.id.et_email);
        etTelephone = findViewById(R.id.et_telephone);

        // ✅ On va injecter les produits AVANT le TextView tv_produits
        productsContainer = null; // On va l'utiliser différemment

        tvSousTotal = findViewById(R.id.tv_sous_total);
        tvFraisLivraison = findViewById(R.id.tv_frais_livraison);
        tvTotal = findViewById(R.id.tv_total);

        btnContinuer = findViewById(R.id.btn_continuer);
    }

    private void chargerDonneesUtilisateur() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String nom = prefs.getString("nom", "");
        String telephone = prefs.getString("telephone", "");
        String email = prefs.getString("email", "");

        if (!nom.isEmpty()) etNomComplet.setText(nom);
        if (!telephone.isEmpty()) etTelephone.setText(telephone);
        if (!email.isEmpty()) etEmail.setText(email);
    }

    private void recupererDonneesPanier() {
        cartItems = new ArrayList<>(CartManager.getInstance().getCartItems());
        montantPanier = CartManager.getInstance().getTotalPrice();

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void afficherRecapitulatif() {
        // ✅ Trouver le TextView tv_produits et le remplacer par la liste des produits
        TextView tvProduits = findViewById(R.id.tv_produits);

        if (tvProduits != null && tvProduits.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) tvProduits.getParent();
            int index = parent.indexOfChild(tvProduits);

            // Supprimer le TextView
            parent.removeView(tvProduits);

            // Créer un container pour les produits
            productsContainer = new LinearLayout(this);
            productsContainer.setOrientation(LinearLayout.VERTICAL);
            productsContainer.setLayoutParams(tvProduits.getLayoutParams());

            // Ajouter le container à la même position
            parent.addView(productsContainer, index);

            // Ajouter tous les produits
            for (CartItem item : cartItems) {
                addProductView(item);
            }
        }

        // Afficher les totaux
        tvSousTotal.setText(String.format(Locale.FRENCH, "%.2f DH", montantPanier));
        tvFraisLivraison.setText(String.format(Locale.FRENCH, "%.2f DH", FRAIS_LIVRAISON));

        double total = montantPanier + FRAIS_LIVRAISON;
        tvTotal.setText(String.format(Locale.FRENCH, "%.2f DH", total));
    }

    // ✅ Réutilise item_cart_simple.xml mais masque les boutons
    private void addProductView(CartItem item) {
        View productView = LayoutInflater.from(this)
                .inflate(R.layout.item_cart_simple, productsContainer, false);

        ImageView productImage = productView.findViewById(R.id.cartItemImage);
        TextView productName = productView.findViewById(R.id.cartItemName);
        TextView productPrice = productView.findViewById(R.id.cartItemPrice);
        TextView productQuantity = productView.findViewById(R.id.tvQuantity);
        TextView productTotal = productView.findViewById(R.id.cartItemTotal);

        // Boutons à masquer
        ImageButton btnDecrease = productView.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = productView.findViewById(R.id.btnIncrease);
        ImageButton btnDelete = productView.findViewById(R.id.btnDelete);

        // Remplir les données
        productImage.setImageResource(item.getProduct().getImageResource());
        productName.setText(item.getProduct().getName());
        productPrice.setText(String.format(Locale.FRENCH, "%.2f DH", item.getProduct().getPrice()));
        productQuantity.setText(String.valueOf(item.getQuantity()));
        productTotal.setText(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()));

        // ✅ MASQUER les boutons (lecture seule)
        btnDecrease.setVisibility(View.GONE);
        btnIncrease.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);

        // Rendre la quantité plus visible (style "badge")
        productQuantity.setTextColor(getResources().getColor(R.color.primary));
        productQuantity.setTextSize(16);
        productQuantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        productsContainer.addView(productView);
    }

    private void validerEtContinuer() {
        String nom = etNomComplet.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();
        String ville = etVille.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();

        // Validation
        if (nom.isEmpty()) {
            etNomComplet.setError("Nom complet requis");
            etNomComplet.requestFocus();
            return;
        }

        if (adresse.isEmpty()) {
            etAdresse.setError("Adresse requise");
            etAdresse.requestFocus();
            return;
        }

        if (ville.isEmpty()) {
            etVille.setError("Ville requise");
            etVille.requestFocus();
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        if (telephone.isEmpty() || telephone.length() != 10) {
            etTelephone.setError("Téléphone invalide (10 chiffres)");
            etTelephone.requestFocus();
            return;
        }

        // Construire la description des produits pour l'email
        StringBuilder produitsDescription = new StringBuilder();
        for (CartItem item : cartItems) {
            produitsDescription.append(item.getProduct().getName())
                    .append(" (x")
                    .append(item.getQuantity())
                    .append(") - ")
                    .append(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()))
                    .append("\n");
        }

        // Sauvegarde dans SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nom", nom);
        editor.putString("adresse", adresse);
        editor.putString("ville", ville);
        editor.putString("email", email);
        editor.putString("telephone", telephone);
        editor.putFloat("montant_total", (float)(montantPanier + FRAIS_LIVRAISON));
        editor.putString("produits", produitsDescription.toString().trim());

        editor.apply();

        // Navigation vers l'activité méthode de paiement
        Intent intent = new Intent(CheckoutActivity.this, PaymentMethodActivity.class);
        startActivity(intent);
    }
}