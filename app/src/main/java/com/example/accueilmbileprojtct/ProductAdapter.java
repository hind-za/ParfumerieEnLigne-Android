package com.example.accueilmbileprojtct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;
    private Context context;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onWishlistClick(Product product);
        void onAddToCartClick(Product product);
    }

    // Constructeur avec listener (pour MainActivity première version)
    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    // Constructeur sans listener (pour MainActivity deuxième version)
    public ProductAdapter(List<Product> products) {
        this.products = products;
        this.listener = null;
        this.context = null;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx)
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Context ctx = context != null ? context : holder.itemView.getContext();

        // 1️⃣ IMAGE
        holder.productImage.setImageResource(product.getImageResource());

        // 2️⃣ NOM
        holder.productName.setText(product.getName());

        // 3️⃣ PRIX ET PROMOTION
        if (product.getOldPrice() != null && !product.getOldPrice().isEmpty()) {
            // Il y a une promotion
            holder.productPrice.setText(String.format("%.0f MAD", product.getPrice()));

            // Afficher l'ancien prix barré
            if (holder.productOldPrice != null) {
                holder.productOldPrice.setText(String.format("%.0f MAD", Double.parseDouble(product.getOldPrice())));
                holder.productOldPrice.setPaintFlags(holder.productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.productOldPrice.setVisibility(View.VISIBLE);
            }
        } else {
            // Pas de promotion
            holder.productPrice.setText(String.format("%.0f MAD", product.getPrice()));
            if (holder.productOldPrice != null) {
                holder.productOldPrice.setVisibility(View.GONE);
            }
        }

        // 4️⃣ RATING
        if (holder.productRating != null) {
            holder.productRating.setText(String.valueOf(product.getRating()));
        }

        // 5️⃣ BADGE MOST POPULAR
        if (holder.badgePopularText != null) {
            if (product.isMostPopular()) {
                holder.badgePopularText.setVisibility(View.VISIBLE);
            } else {
                holder.badgePopularText.setVisibility(View.GONE);
            }
        }

        // 6️⃣ BADGE YEAR-END DEALS
        if (holder.badgeDeal != null) {
            if (product.hasYearEndDeal()) {
                holder.badgeDeal.setVisibility(View.VISIBLE);
            } else {
                holder.badgeDeal.setVisibility(View.GONE);
            }
        }

        // 7️⃣ DISCOUNT
        if (holder.productDiscount != null) {
            if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
                holder.productDiscount.setText(product.getDiscount());
                holder.productDiscount.setVisibility(View.VISIBLE);
            } else {
                holder.productDiscount.setVisibility(View.GONE);
            }
        }

        // 8️⃣ SOLD COUNT
        if (holder.badgeSold != null && holder.badgeSoldText != null) {
            if (product.getSoldCount() != null) {
                holder.badgeSold.setVisibility(View.VISIBLE);
                holder.badgeSoldText.setText(product.getSoldCount());
            } else {
                holder.badgeSold.setVisibility(View.GONE);
            }
        }

        // 9️⃣ WISHLIST BUTTON
        if (holder.btnWishlist != null) {
            if (product.isFavorite()) {
                holder.btnWishlist.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.btnWishlist.setImageResource(R.drawable.ic_favorite_border);
            }

            holder.btnWishlist.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWishlistClick(product);
                    notifyItemChanged(position);
                } else {
                    // Fallback si pas de listener
                    if (product.isFavorite()) {
                        WishlistManager.remove(product);
                        product.setFavorite(false);
                    } else {
                        WishlistManager.add(product);
                        product.setFavorite(true);
                    }
                    notifyItemChanged(position);
                }
            });
        }

        // 🔟 CLICK SUR LA CARTE PRODUIT
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            } else {
                // Fallback : ouvrir ProductDetailActivity
                Intent intent = new Intent(ctx, ProductDetailActivity.class);
                intent.putExtra("product", product);
                ctx.startActivity(intent);
            }
        });

        // 1️⃣1️⃣ ADD TO CART BUTTON
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                } else {
                    // Fallback
                    CartManager.getInstance().addToCart(product);
                    Toast.makeText(ctx, product.getName() + " ajouté au panier", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productOldPrice;  // Pour l'ancien prix barré
        TextView productRating;
        TextView badgePopularText;
        View badgeDeal;
        TextView productDiscount;
        View badgeSold;
        TextView badgeSoldText;
        ImageButton btnWishlist;
        View btnAddToCart;  // Peut être TextView ou CardView

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productOldPrice = itemView.findViewById(R.id.product_old_price);  // À ajouter dans le layout
            productRating = itemView.findViewById(R.id.product_rating);
            badgePopularText = itemView.findViewById(R.id.badge_popular_text);
            badgeDeal = itemView.findViewById(R.id.badge_deal);
            productDiscount = itemView.findViewById(R.id.product_discount);
            badgeSold = itemView.findViewById(R.id.badge_sold);
            badgeSoldText = itemView.findViewById(R.id.badge_sold_text);
            btnWishlist = itemView.findViewById(R.id.btn_wishlist);

            // Essayer les deux IDs possibles pour le bouton cart
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            if (btnAddToCart == null) {
                btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            }
        }
    }
}