package com.example.accueilmbileprojtct;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> list;

    public NotificationAdapter(List<NotificationItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());
        holder.date.setText(item.getDate());

        // 🚀 Rendre les notifications cliquables
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent;

            String target = item.getTarget();
            if (target == null) target = "home";

            switch (target) {

                case "promo": // 🔥 NOTIFICATION PROMOTION
                    intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("mode", "promo"); // 👈 ICI
                    break;

                case "category_perfume":
                    intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Parfum");
                    break;

                case "category_makeup":
                    intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Makeup");
                    break;

                case "home":
                default:
                    intent = new Intent(context, MainActivity.class);
                    break;
            }
            // ⚡ Ajoute ce flag ici
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, date;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            message = itemView.findViewById(R.id.txtMessage);
            date = itemView.findViewById(R.id.txtDate);
        }
    }
}
