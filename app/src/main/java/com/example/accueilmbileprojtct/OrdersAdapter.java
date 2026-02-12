package com.example.accueilmbileprojtct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrdersAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderNumber.setText(order.getNumeroCommande());
        holder.tvOrderDate.setText(order.getDateCommande());
        holder.tvOrderTotal.setText(String.format(Locale.FRENCH, "%.2f DH", order.getMontantTotal()));
        holder.tvOrderStatus.setText(order.getStatut());
        holder.tvOrderAddress.setText(order.getAdresseLivraison());
        holder.tvOrderPayment.setText(order.getModePaiement());

        int totalItems = order.getTotalItems();
        holder.tvOrderItems.setText(totalItems + (totalItems > 1 ? " articles" : " article"));

        int statusColor;
        switch (order.getStatut()) {
            case "Livrée":
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "Annulée":
                statusColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
        }
        holder.tvOrderStatus.setTextColor(statusColor);

        holder.layoutProducts.removeAllViews();

        for (CartItem item : order.getItems()) {
            View productView = LayoutInflater.from(context)
                    .inflate(R.layout.item_order_product, holder.layoutProducts, false);

            ImageView productImage = productView.findViewById(R.id.orderProductImage);
            TextView productName = productView.findViewById(R.id.orderProductName);
            TextView productQuantity = productView.findViewById(R.id.orderProductQuantity);
            TextView productPrice = productView.findViewById(R.id.orderProductPrice);

            productImage.setImageResource(item.getProduct().getImageResource());
            productName.setText(item.getProduct().getName());
            productQuantity.setText("x" + item.getQuantity());
            productPrice.setText(String.format(Locale.FRENCH, "%.2f DH", item.getTotalPrice()));

            holder.layoutProducts.addView(productView);
        }

        holder.itemView.setOnClickListener(v -> {
            boolean isExpanded = holder.layoutDetails.getVisibility() == View.VISIBLE;
            holder.layoutDetails.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            holder.ivExpandIcon.setRotation(isExpanded ? 0 : 180);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvOrderDate, tvOrderTotal, tvOrderStatus;
        TextView tvOrderItems, tvOrderAddress, tvOrderPayment;
        LinearLayout layoutProducts, layoutDetails;
        ImageView ivExpandIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvOrderPayment = itemView.findViewById(R.id.tvOrderPayment);
            layoutProducts = itemView.findViewById(R.id.layoutOrderProducts);
            layoutDetails = itemView.findViewById(R.id.layoutOrderDetails);
            ivExpandIcon = itemView.findViewById(R.id.ivExpandIconOrder);
        }
    }
}