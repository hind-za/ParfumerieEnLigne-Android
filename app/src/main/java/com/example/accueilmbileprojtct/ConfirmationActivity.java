package com.example.accueilmbileprojtct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvNumeroCommande, tvDateCommande, tvMontantTotal;
    private TextView tvNomClient, tvAdresseLivraison, tvTelephone;
    private TextView tvModePaiement, tvDateLivraisonEstimee;
    private TextView tvEmailInfo;
    private Button btnRetourAccueil;
    private LinearLayout productsContainer;

    private String numeroCommande;
    private String emailClient;
    private ArrayList<CartItem> cartItems;

    // 🔥 AJOUT : PrefsManager pour sauvegarder les commandes
    private PrefsManager prefsManager;

    private static final String EMAILJS_SERVICE_ID = "service_eil86kg";
    private static final String EMAILJS_TEMPLATE_ID = "template_flhh2wd";
    private static final String EMAILJS_PUBLIC_KEY = "bEvreZSTJTq3rfQt4";
    private static final String EMAILJS_PRIVATE_KEY = "3t6coBCwoTyRdHdQPOj_a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        initializeViews();

        // 🔥 INITIALISER PrefsManager
        prefsManager = new PrefsManager(this);

        genererNumeroCommande();
        recupererProduitsPanier();
        afficherDetailsCommande();

        // 🔥 INCRÉMENTER LES STATISTIQUES (AVANT l'envoi email)
        incrementerStatistiques();

        envoyerEmailConfirmation();

        if (btnRetourAccueil != null) {
            btnRetourAccueil.setOnClickListener(v -> retourAccueil());
        }
    }

    private void initializeViews() {
        tvNumeroCommande = findViewById(R.id.tv_numero_commande);
        tvDateCommande = findViewById(R.id.tv_date_commande);
        tvMontantTotal = findViewById(R.id.tv_montant_total);
        tvNomClient = findViewById(R.id.tv_nom_client);
        tvAdresseLivraison = findViewById(R.id.tv_adresse_livraison);
        tvTelephone = findViewById(R.id.tv_telephone);
        tvModePaiement = findViewById(R.id.tv_mode_paiement);
        tvDateLivraisonEstimee = findViewById(R.id.tv_date_livraison_estimee);
        btnRetourAccueil = findViewById(R.id.btn_retour_accueil);
        tvEmailInfo = findViewById(R.id.tv_email_info);

        if (tvNumeroCommande == null || tvDateCommande == null ||
                tvMontantTotal == null || tvNomClient == null || tvAdresseLivraison == null ||
                tvTelephone == null || tvModePaiement == null || tvDateLivraisonEstimee == null) {

            Toast.makeText(this, "Erreur de chargement de la vue", Toast.LENGTH_LONG).show();
            Log.e("ConfirmationActivity", "Une ou plusieurs vues sont null");
        }
    }

    private void genererNumeroCommande() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = sdf.format(Calendar.getInstance().getTime());

        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);

        numeroCommande = "CMD-" + date + "-" + randomNum;
    }

    private void recupererProduitsPanier() {
        cartItems = new ArrayList<>(CartManager.getInstance().getCartItems());
    }

    // 🔥 NOUVELLE MÉTHODE : Incrémenter les statistiques automatiquement
    private void incrementerStatistiques() {
        // Incrémenter le nombre de commandes
        prefsManager.incrementOrder();

        // Calculer les points gagnés (10% du montant total)
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        float montant = prefs.getFloat("montant_total", 0.0f);
        int pointsGagnes = (int) (montant * 0.10);

        // Incrémenter les points
        prefsManager.incrementPoints(pointsGagnes);

        Log.d("ConfirmationActivity", "Stats incrémentées - Commandes: " +
                prefsManager.getOrderCount() + ", Points: +" + pointsGagnes);
    }

    private void afficherDetailsCommande() {
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);

        if (tvNumeroCommande != null) {
            tvNumeroCommande.setText(numeroCommande);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        String dateCommande = sdf.format(Calendar.getInstance().getTime());
        if (tvDateCommande != null) {
            tvDateCommande.setText(dateCommande);
        }

        TextView tvProduits = findViewById(R.id.tv_produits);
        if (tvProduits != null && tvProduits.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) tvProduits.getParent();
            int index = parent.indexOfChild(tvProduits);

            parent.removeView(tvProduits);

            productsContainer = new LinearLayout(this);
            productsContainer.setOrientation(LinearLayout.VERTICAL);
            productsContainer.setLayoutParams(tvProduits.getLayoutParams());

            parent.addView(productsContainer, index);

            if (cartItems != null && !cartItems.isEmpty()) {
                for (CartItem item : cartItems) {
                    addProductView(item);
                }
            }
        }

        float montant = prefs.getFloat("montant_total", 0.0f);
        if (tvMontantTotal != null) {
            tvMontantTotal.setText(String.format(Locale.FRENCH, "%.2f DH", montant));
        }

        String nom = prefs.getString("nom", "");
        String adresse = prefs.getString("adresse", "");
        String ville = prefs.getString("ville", "");
        String telephone = prefs.getString("telephone", "");
        emailClient = prefs.getString("email", "");

        if (tvNomClient != null) {
            tvNomClient.setText(nom);
        }

        if (tvAdresseLivraison != null) {
            tvAdresseLivraison.setText(adresse + ", " + ville);
        }

        if (tvTelephone != null) {
            tvTelephone.setText(telephone);
        }

        if (tvEmailInfo != null) {
            if (!emailClient.isEmpty()) {
                tvEmailInfo.setText("📧 Préparation de l'envoi à : " + emailClient);
            } else {
                tvEmailInfo.setText("📧 Email non renseigné");
            }
        }

        String modePaiement = prefs.getString("mode_paiement", "LIVRAISON");
        if (tvModePaiement != null) {
            if (modePaiement.equals("CARTE")) {
                String typeCarte = prefs.getString("type_carte", "Carte bancaire");
                String derniers = prefs.getString("derniers_chiffres", "****");
                tvModePaiement.setText(typeCarte + " **** " + derniers);
            } else {
                tvModePaiement.setText("Paiement à la livraison");
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 4);
        SimpleDateFormat sdfLivraison = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String dateLivraison = sdfLivraison.format(cal.getTime());
        if (tvDateLivraisonEstimee != null) {
            tvDateLivraisonEstimee.setText("Livraison estimée le " + dateLivraison);
        }

        sauvegarderCommande();
    }

    private void addProductView(CartItem item) {
        View productView = LayoutInflater.from(this)
                .inflate(R.layout.item_cart_simple, productsContainer, false);

        ImageView productImage = productView.findViewById(R.id.cartItemImage);
        TextView productName = productView.findViewById(R.id.cartItemName);
        TextView productPrice = productView.findViewById(R.id.cartItemPrice);
        TextView productQuantity = productView.findViewById(R.id.tvQuantity);
        TextView productTotal = productView.findViewById(R.id.cartItemTotal);

        ImageButton btnDecrease = productView.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = productView.findViewById(R.id.btnIncrease);
        ImageButton btnDelete = productView.findViewById(R.id.btnDelete);

        productImage.setImageResource(item.getProduct().getImageResource());
        productName.setText(item.getProduct().getName());
        productPrice.setText(String.format(Locale.FRENCH, "%.2f DH", item.getProduct().getPrice()));
        productQuantity.setText("x" + item.getQuantity());
        productTotal.setText(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()));

        btnDecrease.setVisibility(View.GONE);
        btnIncrease.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);

        productQuantity.setTextColor(getResources().getColor(R.color.primary));
        productQuantity.setTextSize(16);
        productQuantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        productsContainer.addView(productView);
    }

    private void envoyerEmailConfirmation() {
        if (emailClient == null || emailClient.isEmpty()) {
            runOnUiThread(() -> {
                if (tvEmailInfo != null) {
                    tvEmailInfo.setText("⚠️ Aucun email renseigné");
                }
                Toast.makeText(this, "Email non renseigné", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);

                String nom = prefs.getString("nom", "Client");
                String produits = prefs.getString("produits", "");
                float montant = prefs.getFloat("montant_total", 0.0f);
                String adresse = prefs.getString("adresse", "");
                String ville = prefs.getString("ville", "");
                String telephone = prefs.getString("telephone", "");
                String modePaiement = prefs.getString("mode_paiement", "LIVRAISON");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
                String dateCommande = sdf.format(Calendar.getInstance().getTime());

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 4);
                SimpleDateFormat sdfLivraison = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                String dateLivraison = sdfLivraison.format(cal.getTime());

                JSONObject json = new JSONObject();
                json.put("service_id", EMAILJS_SERVICE_ID);
                json.put("template_id", EMAILJS_TEMPLATE_ID);
                json.put("user_id", EMAILJS_PUBLIC_KEY);
                json.put("accessToken", EMAILJS_PRIVATE_KEY);

                JSONObject templateParams = new JSONObject();
                templateParams.put("email_client", emailClient);
                templateParams.put("nom", nom);
                templateParams.put("numero_commande", numeroCommande);
                templateParams.put("date_commande", dateCommande);
                templateParams.put("produits", produits);
                templateParams.put("montant", String.format(Locale.FRENCH, "%.2f", montant));
                templateParams.put("adresse", adresse);
                templateParams.put("ville", ville);
                templateParams.put("telephone", telephone);
                templateParams.put("mode_paiement", modePaiement);
                templateParams.put("date_livraison", dateLivraison);

                json.put("template_params", templateParams);

                Log.d("EmailJS", "JSON envoyé : " + json.toString());

                URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("EmailJS", "Code de réponse : " + responseCode);

                String responseMessage = "";
                try {
                    BufferedReader br;
                    if (responseCode == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    responseMessage = response.toString();
                    Log.d("EmailJS", "Réponse : " + responseMessage);
                } catch (Exception e) {
                    Log.e("EmailJS", "Erreur lecture réponse", e);
                }

                String finalResponse = responseMessage;
                int finalCode = responseCode;

                runOnUiThread(() -> {
                    if (finalCode == 200) {
                        if (tvEmailInfo != null) {
                            tvEmailInfo.setText("✅ Email envoyé avec succès à : " + emailClient);
                        }
                        Toast.makeText(this, "✅ Email de confirmation envoyé !", Toast.LENGTH_LONG).show();
                    } else {
                        if (tvEmailInfo != null) {
                            tvEmailInfo.setText("⚠️ Erreur lors de l'envoi (Code: " + finalCode + ")");
                        }
                        Toast.makeText(this,
                                "⚠️ Erreur " + finalCode + ": " + finalResponse,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("EmailJS", "Exception : " + e.getMessage(), e);

                runOnUiThread(() -> {
                    if (tvEmailInfo != null) {
                        tvEmailInfo.setText("⚠️ Erreur réseau - Email non envoyé");
                    }
                    Toast.makeText(this, "Erreur réseau : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // 🔥 MÉTHODE MODIFIÉE : Sauvegarder la commande complète avec PrefsManager
    private void sauvegarderCommande() {
        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);

        // Créer l'objet Order
        Order order = new Order();
        order.setNumeroCommande(numeroCommande);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);
        order.setDateCommande(sdf.format(Calendar.getInstance().getTime()));

        order.setItems(cartItems);
        order.setMontantTotal(prefs.getFloat("montant_total", 0.0f));

        String adresse = prefs.getString("adresse", "");
        String ville = prefs.getString("ville", "");
        order.setAdresseLivraison(adresse + ", " + ville);

        String modePaiement = prefs.getString("mode_paiement", "LIVRAISON");
        if (modePaiement.equals("CARTE")) {
            String typeCarte = prefs.getString("type_carte", "Carte bancaire");
            String derniers = prefs.getString("derniers_chiffres", "****");
            order.setModePaiement(typeCarte + " **** " + derniers);
        } else {
            order.setModePaiement("Paiement à la livraison");
        }

        order.setStatut("En cours");
        order.setTimestamp(System.currentTimeMillis());

        // Sauvegarder avec PrefsManager
        prefsManager.saveOrder(order);

        Log.d("ConfirmationActivity", "Commande sauvegardée : " + numeroCommande);
    }

    private void retourAccueil() {
        CartManager.getInstance().clearCart();

        SharedPreferences prefs = getSharedPreferences("CheckoutData", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        retourAccueil();
    }
}