package com.example.accueilmbileprojtct;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;

public class ChatbotActivity extends AppCompatActivity {

    EditText inputMessage;
    Button sendButton, btnHelp, btnProducts, btnDelivery;
    LinearLayout chatContainer;
    ScrollView scrollView;
    // Déclaration de la liste de produits ici (au niveau de la classe)
    private List<String> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        ImageView btnBack = findViewById(R.id.btnBaack);
        btnBack.setOnClickListener(v -> finish());

// Initialiser la liste des produits
        productList = new ArrayList<>();
        productList.add("Foundation SHEGLAM");
        productList.add("Skincare");
        productList.add("Cerave Gel Moussant");
        productList.add("fond de teint");
        productList.add("THE ORDINARY Niacinamide");
        productList.add("La Roche-Posay Moisturizer");
        productList.add("Makeup");
        productList.add("Makeup By MARIO Palette");
        productList.add("YVES SAINT LAURENT");
        productList.add("Maybelline SKY HIGH Mascara");
        productList.add("Foundation SHEGLAM");
        productList.add("Parfum");
        // Liaison des vues
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollViewChat);

        btnHelp = findViewById(R.id.btnHelp);
        btnProducts = findViewById(R.id.btnProducts);
        btnDelivery = findViewById(R.id.btnDelivery);

        // Message de bienvenue
        showBotMessage("Bonjour 👋 Je suis ton assistant. Pose-moi une question 😊");

        // Boutons suggérés
        btnHelp.setOnClickListener(v -> showBotMessage("Merci ! Je peux vous aider à trouver un produit ou vérifier votre commande. Que souhaitez-vous faire?"));
        btnProducts.setOnClickListener(v -> showBotMessage("Pouvez-vous me donner le nom ou le type de produit que vous cherchez?"));
        btnDelivery.setOnClickListener(v -> showBotMessage("La livraison est rapide et sécurisée 🚚 et tu peux suivre tes commandes depuis l’onglet Order 📦"));

        // Envoyer message utilisateur
        sendButton.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                showUserMessage(msg);           // Affiche le message utilisateur
                showBotMessage(botReply(msg));  // Affiche la réponse du bot
                inputMessage.setText("");
            }
        });
    }

    // Affiche le message utilisateur (droite)
    private void showUserMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.parseColor("#F06292"));
        textView.setPadding(24, 16, 24, 16);
        textView.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        params.setMargins(100, 0, 0, 24);

        textView.setLayoutParams(params);
        chatContainer.addView(textView);
        scrollToBottom();
    }

    // Affiche le message bot (gauche)
    private void showBotMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(24, 16, 24, 16);
        textView.setTextSize(16);
        textView.setElevation(4);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.START;
        params.setMargins(0, 0, 100, 24);

        textView.setLayoutParams(params);
        chatContainer.addView(textView);
        scrollToBottom();
    }

    // Scroll automatique vers le bas
    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // Logique du bot
    private String botReply(String msg) {
        msg = msg.toLowerCase();

        // Salutations
        if (msg.contains("bonjour") || msg.contains("salut") || msg.contains("hello"))
            return "Bonjour 👋 Comment puis-je t’aider ?";

        // Aide générale
        if (msg.contains("aide"))
            return "Merci ! Je peux vous aider à trouver un produit ou vérifier votre commande. Que souhaitez-vous faire? 😊";

        // Livraison
        if (msg.contains("livraison"))
            return "La livraison prend généralement 24 à 48h 🚚";

        // Commande
        if (msg.contains("commande") || msg.contains("order"))
            return "Tu peux suivre tes commandes depuis l’onglet Order 📦";

        // Vérifier si le message parle d’un produit
        for (String product : productList) {
            if (msg.contains(product.toLowerCase())) {
                return "Oui 👍, le produit \"" + product + "\" est disponible !";
            }
        }

        // Vérifier si l'utilisateur demande un produit général
        if (msg.contains("produit") || msg.contains("disponible") || msg.contains("avez-vous")) {
            return "Pouvez-vous me donner le nom ou le type de produit que vous cherchez?";
        }

        // Si rien ne correspond
        return "Je n’ai pas compris 🤔 Essaie : produit, livraison, aide...";
    }

}
