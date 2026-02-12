package com.example.accueilmbileprojtct;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    // Common fields
    private int id;
    private String name;
    private String description;
    private double price;
    private int imageResource;
    private String category;

    // Additional fields
    private int rating;
    private boolean isMostPopular;
    private boolean hasYearEndDeal;
    private String discount;
    private String soldCount;
    private boolean favorite = false;
    private String oldPrice;  // 🔥 AJOUT pour les promotions

    // Full constructor avec oldPrice
    public Product(int id, String name, String description, double price, int imageResource,
                   String category, int rating, boolean isMostPopular, boolean hasYearEndDeal,
                   String oldPrice, String soldCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.rating = rating;
        this.isMostPopular = isMostPopular;
        this.hasYearEndDeal = hasYearEndDeal;
        this.oldPrice = oldPrice;  // 🔥 CHANGÉ : discount -> oldPrice
        this.soldCount = soldCount;

        // Calculer automatiquement le discount si oldPrice existe
        if (oldPrice != null && !oldPrice.isEmpty()) {
            try {
                // Nettoyer oldPrice (enlever "dh", espaces, etc.)
                String cleanOldPrice = oldPrice.replaceAll("[^0-9.]", "");
                double oldPriceValue = Double.parseDouble(cleanOldPrice);
                double discountPercent = ((oldPriceValue - price) / oldPriceValue) * 100;
                this.discount = String.format("-%.0f%%", discountPercent);
            } catch (NumberFormatException e) {
                this.discount = null;
            }
        } else {
            this.discount = null;
        }
    }

    // Constructor for older usages (without extra fields)
    public Product(int imageResource, String name, double price, int rating) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.isMostPopular = false;
        this.hasYearEndDeal = false;
        this.discount = null;
        this.oldPrice = null;
        this.soldCount = null;
    }

    // Constructor for older usages (without rating and extra fields)
    public Product(int id, String name, String description, double price, int imageResource, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.rating = 0;
        this.isMostPopular = false;
        this.hasYearEndDeal = false;
        this.discount = null;
        this.oldPrice = null;
        this.soldCount = null;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageResource = in.readInt();
        category = in.readString();
        rating = in.readInt();
        isMostPopular = in.readByte() != 0;
        hasYearEndDeal = in.readByte() != 0;
        discount = in.readString();
        oldPrice = in.readString();  // 🔥 AJOUT
        soldCount = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(imageResource);
        dest.writeString(category);
        dest.writeInt(rating);
        dest.writeByte((byte) (isMostPopular ? 1 : 0));
        dest.writeByte((byte) (hasYearEndDeal ? 1 : 0));
        dest.writeString(discount);
        dest.writeString(oldPrice);  // 🔥 AJOUT
        dest.writeString(soldCount);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public boolean isMostPopular() { return isMostPopular; }
    public void setMostPopular(boolean mostPopular) { isMostPopular = mostPopular; }

    public boolean hasYearEndDeal() { return hasYearEndDeal; }
    public void setYearEndDeal(boolean yearEndDeal) { hasYearEndDeal = yearEndDeal; }

    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }

    // 🔥 AJOUT : Getter et Setter pour oldPrice
    public String getOldPrice() { return oldPrice; }
    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
        // Recalculer le discount
        if (oldPrice != null && !oldPrice.isEmpty()) {
            try {
                // Nettoyer oldPrice (enlever "dh", espaces, etc.)
                String cleanOldPrice = oldPrice.replaceAll("[^0-9.]", "");
                double oldPriceValue = Double.parseDouble(cleanOldPrice);
                double discountPercent = ((oldPriceValue - price) / oldPriceValue) * 100;
                this.discount = String.format("-%.0f%%", discountPercent);
            } catch (NumberFormatException e) {
                this.discount = null;
            }
        } else {
            this.discount = null;
        }
    }

    public String getSoldCount() { return soldCount; }
    public void setSoldCount(String soldCount) { this.soldCount = soldCount; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}