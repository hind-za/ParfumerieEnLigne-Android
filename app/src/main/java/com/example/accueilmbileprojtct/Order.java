package com.example.accueilmbileprojtct;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String numeroCommande;
    private String dateCommande;
    private List<CartItem> items;
    private double montantTotal;
    private String adresseLivraison;
    private String modePaiement;
    private String statut;
    private long timestamp;

    public Order(String numeroCommande, String dateCommande, List<CartItem> items,
                 double montantTotal, String adresseLivraison, String modePaiement, String statut) {
        this.numeroCommande = numeroCommande;
        this.dateCommande = dateCommande;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.montantTotal = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.timestamp = System.currentTimeMillis();
    }

    public Order() {
        this.items = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public String getNumeroCommande() { return numeroCommande; }
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }

    public String getDateCommande() { return dateCommande; }
    public void setDateCommande(String dateCommande) { this.dateCommande = dateCommande; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
}