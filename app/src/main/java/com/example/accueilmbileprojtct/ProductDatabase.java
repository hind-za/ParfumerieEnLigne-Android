package com.example.accueilmbileprojtct;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabase {

    private static ProductDatabase instance;
    private List<Product> allProducts;

    private ProductDatabase() {
        allProducts = new ArrayList<>();
        loadAllProducts();
    }

    public static ProductDatabase getInstance() {
        if (instance == null) {
            instance = new ProductDatabase();
        }
        return instance;
    }

    private void loadAllProducts() {
        // 🔥 PARFUMS
        allProducts.add(new Product(101, "Chanel N°5", "Parfum emblématique pour femme", 850.0,
                R.drawable.ysllibre, "Parfum", 5, false, false, null, null));
        allProducts.add(new Product(102, "Dior Sauvage", "Eau de toilette homme", 900.0,
                R.drawable.parfum2, "Parfum", 5, false, false, null, null));
        allProducts.add(new Product(103, "Yves Saint Laurent Libre", "Eau de parfum intense", 1708.66,
                R.drawable.ysllibre, "Parfum", 5, true, false, null, null));
        allProducts.add(new Product(104, "Gucci Bloom", "Eau de parfum floral", 1200.0,
                R.drawable.parfum2, "Parfum", 5, false, true, "200.0", "100K+ sold"));
        allProducts.add(new Product(105, "Miss Dior Absolutely Blooming", "Parfum emblématique", 2000.0,
                R.drawable.missdior, "Parfum", 5, false, false, "1500.0", null));
        allProducts.add(new Product(106, "Prada Infusion de Figue", "Parfum frais", 1730.0,
                R.drawable.ysllibre, "Parfum", 5, false, false, null, null));

        // 🔥 MAKEUP
        allProducts.add(new Product(201, "Foundation SHEGLAM HD Pro 30ml", "Uniform color to the complexion", 450.0,
                R.drawable.foundation, "Makeup", 5, false, false, null, null));
        allProducts.add(new Product(202, "YVES SAINT LAURENT Rouge pur", "Intense Color Payoff", 320.0,
                R.drawable.rougealevre, "Makeup", 5, false, false, null, null));
        allProducts.add(new Product(203, "Maybelline SKY HIGH Mascara", "Mascara volumisant", 400.0,
                R.drawable.mascara, "Makeup", 5, false, false, null, null));
        allProducts.add(new Product(204, "Makeup By MARIO Palette", "Palette Ombres Shimmer", 390.0,
                R.drawable.eyeshadow, "Makeup", 5, true, false, null, null));

        // 🔥 SKINCARE
        allProducts.add(new Product(301, "La Roche-Posay Moisturizer", "Soin hydratant", 380.0,
                R.drawable.moisturizer, "Skincare", 5, false, false, null, null));
        allProducts.add(new Product(302, "THE ORDINARY Niacinamide 10%", "Sérum 30ML", 520.0,
                R.drawable.serum, "Skincare", 5, false, false, null, null));
        allProducts.add(new Product(303, "Cerave Gel Moussant", "Nettoyant 473ml", 220.0,
                R.drawable.gel, "Skincare", 5, false, false, null, null));
        allProducts.add(new Product(304, "BEAUTY OF JOSEON Relief Sun", "SPF 50ML", 310.0,
                R.drawable.sunscreen, "Skincare", 5, false, false, null, null));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }

    /**
     * Recherche des produits par nom (insensible à la casse)
     */
    public List<Product> searchProducts(String query) {
        List<Product> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String searchQuery = query.toLowerCase().trim();

        for (Product product : allProducts) {
            String productName = product.getName().toLowerCase();
            String productCategory = product.getCategory() != null ? product.getCategory().toLowerCase() : "";

            // Recherche dans le nom ou la catégorie
            if (productName.contains(searchQuery) || productCategory.contains(searchQuery)) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * Obtenir les produits d'une catégorie spécifique
     */
    public List<Product> getProductsByCategory(String category) {
        List<Product> results = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * Obtenir un produit par son ID
     */
    public Product getProductById(int id) {
        for (Product product : allProducts) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }


    //pour les notifications des promotions
    public List<Product> getDiscountedProducts() {
        List<Product> results = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
                results.add(product);
            }
        }
        return results;
    }


}