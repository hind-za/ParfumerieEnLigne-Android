package com.example.accueilmbileprojtct;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrefsManager {
    private static final String PREFS_NAME = "beauty_prefs";
    private static final String KEY_USERNAME = "user_username";
    private static final String KEY_FIRST = "user_firstname";
    private static final String KEY_LAST = "user_lastname";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_LOGGED = "is_logged";

    private static final String KEY_ORDER_COUNT = "order_count_";
    private static final String KEY_POINTS_COUNT = "points_count_";
    private static final String KEY_REVIEWS_COUNT = "reviews_count_";
    private static final String KEY_REVIEWS_JSON = "reviews_json_";

    // 🔥 NOUVELLE CLÉ pour l'historique des commandes
    private static final String KEY_ORDERS_JSON = "orders_json_";

    private final SharedPreferences prefs;

    public PrefsManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // =============================
    //   HELPER : Générer clé unique par utilisateur
    // =============================

    private String getUserKey(String baseKey) {
        String email = getEmail();
        if (email.isEmpty()) {
            return baseKey + "default";
        }
        String cleanEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        return baseKey + cleanEmail;
    }

    // =============================
    //   INSCRIPTION + CONNEXION
    // =============================

    public void saveUser(String username, String last, String email, String password) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_FIRST, username)
                .putString(KEY_LAST, last)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_LOGGED, true)
                .apply();
    }

    public void saveUser(String first, String last, String email) {
        saveUser(first, last, email, "");
    }

    // =============================
    //   GETTERS
    // =============================

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public String getFirstName() {
        return prefs.getString(KEY_FIRST, "");
    }

    public String getLastName() {
        return prefs.getString(KEY_LAST, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, "");
    }

    public boolean isLogged() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    // =============================
    //   VÉRIFICATION CONNEXION
    // =============================

    public boolean verifyCredentials(String email, String password) {
        String savedEmail = getEmail();
        String savedPassword = getPassword();

        if (savedEmail.isEmpty() || savedPassword.isEmpty()) {
            return false;
        }

        boolean isValid = savedEmail.equals(email) && savedPassword.equals(password);

        if (isValid) {
            prefs.edit().putBoolean(KEY_LOGGED, true).apply();
        }

        return isValid;
    }

    // =============================
    //   COMMANDES (PAR UTILISATEUR)
    // =============================

    public void setOrderCount(int count) {
        String key = getUserKey(KEY_ORDER_COUNT);
        prefs.edit().putInt(key, count).apply();
    }

    public int getOrderCount() {
        String key = getUserKey(KEY_ORDER_COUNT);
        return prefs.getInt(key, 0);
    }

    public void incrementOrder() {
        setOrderCount(getOrderCount() + 1);
    }

    // =============================
    //   POINTS (PAR UTILISATEUR)
    // =============================

    public void setPointsCount(int count) {
        String key = getUserKey(KEY_POINTS_COUNT);
        prefs.edit().putInt(key, count).apply();
    }

    public int getPointsCount() {
        String key = getUserKey(KEY_POINTS_COUNT);
        return prefs.getInt(key, 0);
    }

    public void incrementPoints(int pointsToAdd) {
        setPointsCount(getPointsCount() + pointsToAdd);
    }

    // =============================
    //   AVIS (PAR UTILISATEUR)
    // =============================

    public void setReviewsCount(int count) {
        String key = getUserKey(KEY_REVIEWS_COUNT);
        prefs.edit().putInt(key, count).apply();
    }

    public int getReviewsCount() {
        String key = getUserKey(KEY_REVIEWS_COUNT);
        return prefs.getInt(key, 0);
    }

    public void incrementReview() {
        setReviewsCount(getReviewsCount() + 1);
    }

    // =============================
    //   SAUVEGARDE DES AVIS JSON (PAR UTILISATEUR)
    // =============================

    public void saveReviewsJson(String jsonString) {
        String key = getUserKey(KEY_REVIEWS_JSON);
        prefs.edit().putString(key, jsonString).apply();
    }

    public String getReviewsJson() {
        String key = getUserKey(KEY_REVIEWS_JSON);
        return prefs.getString(key, "");
    }

    // =============================
    //   🔥 HISTORIQUE DES COMMANDES (NOUVEAU)
    // =============================

    /**
     * Sauvegarder une nouvelle commande dans l'historique
     */
    public void saveOrder(Order order) {
        List<Order> orders = getOrders();
        orders.add(0, order); // Ajouter au début de la liste
        saveOrdersList(orders);
    }

    /**
     * Récupérer toutes les commandes de l'utilisateur
     */
    public List<Order> getOrders() {
        String key = getUserKey(KEY_ORDERS_JSON);
        String json = prefs.getString(key, "");

        List<Order> orders = new ArrayList<>();

        if (json.isEmpty()) {
            return orders;
        }

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject orderJson = jsonArray.getJSONObject(i);
                Order order = jsonToOrder(orderJson);
                orders.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Sauvegarder la liste complète des commandes
     */
    private void saveOrdersList(List<Order> orders) {
        String key = getUserKey(KEY_ORDERS_JSON);

        try {
            JSONArray jsonArray = new JSONArray();

            for (Order order : orders) {
                JSONObject orderJson = orderToJson(order);
                jsonArray.put(orderJson);
            }

            prefs.edit().putString(key, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convertir un Order en JSONObject
     */
    private JSONObject orderToJson(Order order) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("numeroCommande", order.getNumeroCommande());
        json.put("dateCommande", order.getDateCommande());
        json.put("montantTotal", order.getMontantTotal());
        json.put("adresseLivraison", order.getAdresseLivraison());
        json.put("modePaiement", order.getModePaiement());
        json.put("statut", order.getStatut());
        json.put("timestamp", order.getTimestamp());

        // Sauvegarder les produits
        JSONArray itemsArray = new JSONArray();
        for (CartItem item : order.getItems()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("productId", item.getProduct().getId());
            itemJson.put("productName", item.getProduct().getName());
            itemJson.put("productPrice", item.getProduct().getPrice());
            itemJson.put("productImage", item.getProduct().getImageResource());
            itemJson.put("quantity", item.getQuantity());
            itemsArray.put(itemJson);
        }
        json.put("items", itemsArray);

        return json;
    }

    /**
     * Convertir un JSONObject en Order
     */
    private Order jsonToOrder(JSONObject json) throws JSONException {
        Order order = new Order();
        order.setNumeroCommande(json.getString("numeroCommande"));
        order.setDateCommande(json.getString("dateCommande"));
        order.setMontantTotal(json.getDouble("montantTotal"));
        order.setAdresseLivraison(json.getString("adresseLivraison"));
        order.setModePaiement(json.getString("modePaiement"));
        order.setStatut(json.getString("statut"));
        order.setTimestamp(json.getLong("timestamp"));

        // Récupérer les produits
        JSONArray itemsArray = json.getJSONArray("items");
        List<CartItem> items = new ArrayList<>();

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);

            // Recréer le produit
            Product product = new Product(
                    itemJson.getInt("productId"),
                    itemJson.getString("productName"),
                    "", // description
                    itemJson.getDouble("productPrice"),
                    itemJson.getInt("productImage"),
                    "" // category
            );

            CartItem cartItem = new CartItem(product, itemJson.getInt("quantity"));
            items.add(cartItem);
        }

        order.setItems(items);
        return order;
    }

    // =============================
    //   DÉCONNEXION
    // =============================

    public void clearSession() {
        prefs.edit()
                .putBoolean(KEY_LOGGED, false)
                .apply();
    }

    public void deleteAccount() {
        String email = getEmail();
        if (!email.isEmpty()) {
            String cleanEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
            prefs.edit()
                    .remove(KEY_ORDER_COUNT + cleanEmail)
                    .remove(KEY_POINTS_COUNT + cleanEmail)
                    .remove(KEY_REVIEWS_COUNT + cleanEmail)
                    .remove(KEY_REVIEWS_JSON + cleanEmail)
                    .remove(KEY_ORDERS_JSON + cleanEmail)
                    .apply();
        }

        prefs.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_FIRST)
                .remove(KEY_LAST)
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .remove(KEY_LOGGED)
                .apply();
    }
}