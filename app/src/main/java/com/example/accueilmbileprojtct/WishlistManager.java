package com.example.accueilmbileprojtct;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {

    private static final List<Product> wishlist = new ArrayList<>();

    // ➕ Ajouter un produit
    public static void add(Product product) {
        if (!isInWishlist(product)) {
            wishlist.add(product);
        }
    }

    // ❌ Supprimer un produit
    public static void remove(Product product) {
        for (int i = 0; i < wishlist.size(); i++) {
            if (wishlist.get(i).getId() == product.getId()) {
                wishlist.remove(i);
                break;
            }
        }
    }


    // ❤️ Vérifier si le produit est déjà en wishlist
    public static boolean isInWishlist(Product product) {
        for (Product p : wishlist) {
            if (p.getId() == product.getId()) {
                return true;
            }
        }
        return false;
    }

    // 📦 Récupérer toute la wishlist
    public static List<Product> getWishlist() {
        return new ArrayList<>(wishlist);
    }


    // 🧹 Vider la wishlist (optionnel)
    public static void clear() {
        wishlist.clear();
    }
}
