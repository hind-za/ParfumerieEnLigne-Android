package com.example.accueilmbileprojtct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvOrderCount, tvPointsCount, tvReviewsCount;
    private MaterialButton btnLogout;
    private LinearLayout btnPersonalInfo, btnOrders, btnReviews, btnNotifications;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefs = new PrefsManager(requireContext());

        // Initialiser les vues
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvOrderCount = view.findViewById(R.id.tvOrderCount);
        tvPointsCount = view.findViewById(R.id.tvPointsCount);
        tvReviewsCount = view.findViewById(R.id.tvReviewsCount);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnPersonalInfo = view.findViewById(R.id.btnPersonalInfo);
        btnOrders = view.findViewById(R.id.btnOrders);
        btnReviews = view.findViewById(R.id.btnReviews);
        btnNotifications = view.findViewById(R.id.btnNotifications);

        // Charger les données utilisateur
        loadUserData();

        // Configurer le bouton de déconnexion
        btnLogout.setOnClickListener(v -> logout());

        // 🔥 NAVIGATION VERS LA PAGE DES AVIS
        btnReviews.setOnClickListener(v -> openReviewsPage());

        // 🔥 NAVIGATION VERS LA PAGE DES COMMANDES
        btnOrders.setOnClickListener(v -> openOrdersPage());

        // Configurer les autres boutons
        btnPersonalInfo.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Informations personnelles - À venir", Toast.LENGTH_SHORT).show()
        );

        btnNotifications.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(requireActivity(), WishlistActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Wishlist indisponible",
                        Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void loadUserData() {
        // Récupérer les données depuis PrefsManager
        String username = prefs.getUsername();
        String firstName = prefs.getFirstName();
        String lastName = prefs.getLastName();
        String email = prefs.getEmail();
        int orderCount = prefs.getOrderCount();
        int pointsCount = prefs.getPointsCount();
        int reviewsCount = prefs.getReviewsCount();

        // Afficher le nom
        String displayName;
        if (!username.isEmpty()) {
            displayName = username;
        } else if (!firstName.isEmpty() || !lastName.isEmpty()) {
            displayName = firstName + " " + lastName;
        } else {
            displayName = email.split("@")[0];
        }

        tvUserName.setText(displayName.trim());
        tvUserEmail.setText(email);
        tvOrderCount.setText(String.valueOf(orderCount));
        tvPointsCount.setText(String.valueOf(pointsCount));
        tvReviewsCount.setText(String.valueOf(reviewsCount));
    }

    // Ouvrir la page des avis
    private void openReviewsPage() {
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, reviewsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // 🔥 NOUVELLE MÉTHODE : Ouvrir la page des commandes
    private void openOrdersPage() {
        OrdersFragment ordersFragment = new OrdersFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ordersFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // ===== MÉTHODES POUR INCRÉMENTER LES STATISTIQUES =====

    public void incrementOrders() {
        int currentOrders = prefs.getOrderCount();
        currentOrders++;
        prefs.setOrderCount(currentOrders);
        tvOrderCount.setText(String.valueOf(currentOrders));
        Toast.makeText(requireContext(), "Commande ajoutée !", Toast.LENGTH_SHORT).show();
    }

    public void incrementPoints(int pointsToAdd) {
        int currentPoints = prefs.getPointsCount();
        currentPoints += pointsToAdd;
        prefs.setPointsCount(currentPoints);
        tvPointsCount.setText(String.valueOf(currentPoints));
        Toast.makeText(requireContext(), "+" + pointsToAdd + " points gagnés !", Toast.LENGTH_SHORT).show();
    }

    public void incrementReviews() {
        int currentReviews = prefs.getReviewsCount();
        currentReviews++;
        prefs.setReviewsCount(currentReviews);
        tvReviewsCount.setText(String.valueOf(currentReviews));
        Toast.makeText(requireContext(), "Merci pour votre avis !", Toast.LENGTH_SHORT).show();
    }

    public void refreshStats() {
        loadUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Rafraîchir les stats quand on revient sur ce fragment
    }

    private void logout() {
        prefs.clearSession();
        Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}