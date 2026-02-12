package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class PaymentMethodActivity extends AppCompatActivity {

    private CardView cardPaiementCarte, cardPaiementLivraison;
    private RadioGroup rgMethodePaiement;
    private RadioButton rbCarte, rbLivraison;
    private Button btnConfirmerPaiement;
    private TextView tvMontantTotal; // ✅ Ajouté
    private String methodePaiement = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        initializeViews();
        afficherMontantTotal(); // ✅ Ajouté
        configurerSelectionMethode();

        btnConfirmerPaiement.setOnClickListener(v -> procederAuPaiement());
    }

    private void initializeViews() {
        cardPaiementCarte = findViewById(R.id.card_paiement_carte);
        cardPaiementLivraison = findViewById(R.id.card_paiement_livraison);
        rgMethodePaiement = findViewById(R.id.rg_methode_paiement);
        rbCarte = findViewById(R.id.rb_carte);
        rbLivraison = findViewById(R.id.rb_livraison);
        btnConfirmerPaiement = findViewById(R.id.btn_confirmer_paiement);
        tvMontantTotal = findViewById(R.id.tv_montant_total); // ✅ Ajouté (à ajouter dans le XML)
    }

    // ✅ NOUVELLE MÉTHODE : Afficher le montant total
    private void afficherMontantTotal() {
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        float montantTotal = prefs.getFloat("montant_total", 0.0f);

        if (tvMontantTotal != null) {
            tvMontantTotal.setText(String.format(Locale.FRENCH, "Total : %.2f DH", montantTotal));
        }
    }

    private void configurerSelectionMethode() {
        // Sélection par carte cliquable
        cardPaiementCarte.setOnClickListener(v -> {
            rbCarte.setChecked(true);
            methodePaiement = "CARTE";
            highlightCard(cardPaiementCarte, cardPaiementLivraison);
        });

        // Sélection par livraison cliquable
        cardPaiementLivraison.setOnClickListener(v -> {
            rbLivraison.setChecked(true);
            methodePaiement = "LIVRAISON";
            highlightCard(cardPaiementLivraison, cardPaiementCarte);
        });

        // Radio buttons
        rgMethodePaiement.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_carte) {
                methodePaiement = "CARTE";
                highlightCard(cardPaiementCarte, cardPaiementLivraison);
            } else if (checkedId == R.id.rb_livraison) {
                methodePaiement = "LIVRAISON";
                highlightCard(cardPaiementLivraison, cardPaiementCarte);
            }
        });
    }

    private void highlightCard(CardView selected, CardView unselected) {
        selected.setCardElevation(12f);
        selected.setAlpha(1.0f);

        unselected.setCardElevation(4f);
        unselected.setAlpha(0.6f);
    }

    private void procederAuPaiement() {
        if (methodePaiement.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner un mode de paiement", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mode_paiement", methodePaiement);
        editor.apply();

        if (methodePaiement.equals("CARTE")) {
            startActivity(new Intent(this, CardPaymentActivity.class));
        } else {
            startActivity(new Intent(this, ConfirmationActivity.class));
        }
    }
}