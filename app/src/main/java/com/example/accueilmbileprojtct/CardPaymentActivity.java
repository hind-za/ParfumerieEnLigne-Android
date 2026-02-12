package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accueilmbileprojtct.R;

public class CardPaymentActivity extends AppCompatActivity {

    private TextView tvMontantAPayer;
    private RadioGroup rgTypecarte;
    private RadioButton rbVisa, rbMastercard, rbCMI;
    private ImageView ivCarteLogo;
    private EditText etNumeroCarte, etNomTitulaire, etDateExpiration, etCVV;
    private Button btnPayer;
    private ProgressBar progressBar;

    private double montantTotal;
    private String typeCarte = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        initializeViews();
        chargerMontantTotal();
        configurerTypesCarte();
        configurerFormatageChamps();

        btnPayer.setOnClickListener(v -> traiterPaiement());
    }

    private void initializeViews() {
        tvMontantAPayer = findViewById(R.id.tv_montant_a_payer);
        rgTypecarte = findViewById(R.id.rg_type_carte);
        rbVisa = findViewById(R.id.rb_visa);
        rbMastercard = findViewById(R.id.rb_mastercard);
        rbCMI = findViewById(R.id.rb_cmi);
        ivCarteLogo = findViewById(R.id.iv_carte_logo);
        etNumeroCarte = findViewById(R.id.et_numero_carte);
        etNomTitulaire = findViewById(R.id.et_nom_titulaire);
        etDateExpiration = findViewById(R.id.et_date_expiration);
        etCVV = findViewById(R.id.et_cvv);
        btnPayer = findViewById(R.id.btn_payer);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void chargerMontantTotal() {
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        montantTotal = prefs.getFloat("montant_total", 0.0f);
        tvMontantAPayer.setText(String.format("Montant à payer: %.2f MAD", montantTotal));
    }

    private void configurerTypesCarte() {
        rgTypecarte.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_visa) {
                typeCarte = "Visa";
                ivCarteLogo.setImageResource(R.drawable.ic_visa);
            } else if (checkedId == R.id.rb_mastercard) {
                typeCarte = "Mastercard";
                ivCarteLogo.setImageResource(R.drawable.ic_mastercard);
            } else if (checkedId == R.id.rb_cmi) {
                typeCarte = "CMI";
                ivCarteLogo.setImageResource(R.drawable.ic_cmi);
            }
            ivCarteLogo.setVisibility(View.VISIBLE);
        });
    }

    private void configurerFormatageChamps() {
        // Formatage automatique du numéro de carte (XXXX XXXX XXXX XXXX)
        etNumeroCarte.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                String text = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < text.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(text.charAt(i));
                }

                isUpdating = true;
                etNumeroCarte.setText(formatted.toString());
                etNumeroCarte.setSelection(formatted.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Formatage de la date d'expiration (MM/YY)
        etDateExpiration.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                String text = s.toString().replaceAll("/", "");
                if (text.length() >= 2) {
                    text = text.substring(0, 2) + "/" + text.substring(2);
                }

                isUpdating = true;
                etDateExpiration.setText(text);
                etDateExpiration.setSelection(text.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void traiterPaiement() {
        // Validation
        if (typeCarte.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner un type de carte", Toast.LENGTH_SHORT).show();
            return;
        }

        String numeroCarte = etNumeroCarte.getText().toString().replaceAll("\\s", "");
        String nomTitulaire = etNomTitulaire.getText().toString().trim();
        String dateExpiration = etDateExpiration.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        if (numeroCarte.length() != 16) {
            etNumeroCarte.setError("Numéro de carte invalide");
            etNumeroCarte.requestFocus();
            return;
        }

        if (nomTitulaire.isEmpty()) {
            etNomTitulaire.setError("Nom du titulaire requis");
            etNomTitulaire.requestFocus();
            return;
        }

        if (dateExpiration.length() != 5) {
            etDateExpiration.setError("Date invalide (MM/YY)");
            etDateExpiration.requestFocus();
            return;
        }

        if (cvv.length() != 3) {
            etCVV.setError("CVV invalide (3 chiffres)");
            etCVV.requestFocus();
            return;
        }

        // Sauvegarder les infos de paiement (masquées pour sécurité)
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("type_carte", typeCarte);
        editor.putString("derniers_chiffres", numeroCarte.substring(12)); // 4 derniers chiffres
        editor.apply();

        // Simuler le traitement du paiement
        simulerPaiement();
    }

    private void simulerPaiement() {
        btnPayer.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Simuler un délai de traitement (2 secondes)
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);

            // Paiement réussi - Aller à la confirmation
            Intent intent = new Intent(CardPaymentActivity.this, com.example.accueilmbileprojtct.ConfirmationActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}