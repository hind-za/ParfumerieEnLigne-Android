package com.example.accueilmbileprojtct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private OrdersAdapter ordersAdapter;
    private LinearLayout emptyStateLayout;
    private ImageView btnBack;
    private PrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        prefsManager = new PrefsManager(requireContext());

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnBack = view.findViewById(R.id.btnBackOrders);

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadOrders();

        return view;
    }

    private void loadOrders() {
        List<Order> orders = prefsManager.getOrders();

        if (orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            ordersAdapter = new OrdersAdapter(requireContext(), orders);
            recyclerViewOrders.setAdapter(ordersAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}