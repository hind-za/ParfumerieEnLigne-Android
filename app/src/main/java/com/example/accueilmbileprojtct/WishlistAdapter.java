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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Product> wishlist;
    private OnWishlistItemClickListener listener;
    private OnWishlistEmptyListener emptyListener;

    // Interface pour suppression
    public interface OnWishlistItemClickListener {
        void onRemoveClicked(Product product);
    }

    // Interface pour signaler que la wishlist est vide
    public interface OnWishlistEmptyListener {
        void onWishlistEmpty();
    }

    public WishlistAdapter(List<Product> wishlist,
                           OnWishlistItemClickListener listener,
                           OnWishlistEmptyListener emptyListener) {
        this.wishlist = wishlist;
        this.listener = listener;
        this.emptyListener = emptyListener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Product product = wishlist.get(position);
        Context context = holder.itemView.getContext();

        // IMAGE
        holder.productImage.setImageResource(product.getImageResource());

        // NOM
        holder.productName.setText(product.getName());

        // PRIX ET PROMOTION
        if (product.getOldPrice() != null && !product.getOldPrice().isEmpty()) {
            holder.productPrice.setText(String.format("%.0f MAD", product.getPrice()));
            if (holder.productOldPrice != null) {
                holder.productOldPrice.setText(String.format("%.0f MAD", Double.parseDouble(product.getOldPrice())));
                holder.productOldPrice.setPaintFlags(holder.productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.productOldPrice.setVisibility(View.VISIBLE);
            }
        } else {
            holder.productPrice.setText(String.format("%.0f MAD", product.getPrice()));
            if (holder.productOldPrice != null) {
                holder.productOldPrice.setVisibility(View.GONE);
            }
        }

        // RATING
        if (holder.productRating != null) {
            holder.productRating.setText(String.valueOf(product.getRating()));
        }

        // BADGE MOST POPULAR
        if (holder.badgePopularText != null) {
            holder.badgePopularText.setVisibility(product.isMostPopular() ? View.VISIBLE : View.GONE);
        }

        // BADGE YEAR-END DEALS
        if (holder.badgeDeal != null) {
            holder.badgeDeal.setVisibility(product.hasYearEndDeal() ? View.VISIBLE : View.GONE);
        }

        // DISCOUNT
        if (holder.productDiscount != null) {
            if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
                holder.productDiscount.setText(product.getDiscount());
                holder.productDiscount.setVisibility(View.VISIBLE);
            } else {
                holder.productDiscount.setVisibility(View.GONE);
            }
        }

        // SOLD COUNT
        if (holder.badgeSold != null && holder.badgeSoldText != null) {
            if (product.getSoldCount() != null) {
                holder.badgeSold.setVisibility(View.VISIBLE);
                holder.badgeSoldText.setText(product.getSoldCount());
            } else {
                holder.badgeSold.setVisibility(View.GONE);
            }
        }

        // WISHLIST BUTTON - Coeur toujours rempli dans la wishlist
        holder.btnWishlist.setImageResource(R.drawable.ic_favorite);

        holder.btnWishlist.setOnClickListener(v -> {
            // Supprimer du WishlistManager
            WishlistManager.remove(product);

            // Mettre à jour l'état favorite du produit
            product.setFavorite(false);

            // Retirer de la liste de l'adapter et notifier le changement
            wishlist.remove(product);
            notifyDataSetChanged();

            // Message de confirmation
            Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show();

            // Notifier l'Activity si la wishlist est vide
            if (wishlist.isEmpty() && emptyListener != null) {
                emptyListener.onWishlistEmpty();
            }

            // Callback optionnel
            if (listener != null) listener.onRemoveClicked(product);
        });

        // CLICK SUR LA CARTE PRODUIT - Ouvrir les détails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        // ⚡ ADD TO CART BUTTON - FIX ICI ⚡
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                // Ajouter au panier
                CartManager.getInstance().addToCart(product);

                // Message de confirmation
                Toast.makeText(context,
                        product.getName() + " ajouté au panier ✓",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productOldPrice;
        TextView productRating;
        TextView badgePopularText;
        View badgeDeal;
        TextView productDiscount;
        View badgeSold;
        TextView badgeSoldText;
        ImageButton btnWishlist;
        View btnAddToCart;  // ⚡ AJOUTÉ

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productOldPrice = itemView.findViewById(R.id.product_old_price);
            productRating = itemView.findViewById(R.id.product_rating);
            badgePopularText = itemView.findViewById(R.id.badge_popular_text);
            badgeDeal = itemView.findViewById(R.id.badge_deal);
            productDiscount = itemView.findViewById(R.id.product_discount);
            badgeSold = itemView.findViewById(R.id.badge_sold);
            badgeSoldText = itemView.findViewById(R.id.badge_sold_text);
            btnWishlist = itemView.findViewById(R.id.btn_wishlist);

            // ⚡ RÉCUPÉRER LE BOUTON ADD TO CART ⚡
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            if (btnAddToCart == null) {
                btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            }
        }
    }
}