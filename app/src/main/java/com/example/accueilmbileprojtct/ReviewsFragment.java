package com.example.accueilmbileprojtct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private ImageView btnBack;
    private ImageView[] stars;
    private EditText etReviewTitle, etReviewComment;
    private MaterialButton btnSubmitReview;
    private TextView tvTotalReviews, tvTotalPoints;
    private RecyclerView recyclerViewReviews;
    private View emptyState;

    private PrefsManager prefs;
    private ReviewsAdapter adapter;
    private List<Review> reviewsList;
    private int currentRating = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        prefs = new PrefsManager(requireContext());
        reviewsList = new ArrayList<>();

        // Initialiser les vues
        initViews(view);

        // Configurer les étoiles
        setupStars();

        // Charger les statistiques
        loadStats();

        // Charger les avis sauvegardés
        loadReviews();

        // Configurer le RecyclerView
        setupRecyclerView();

        // Configurer le bouton retour
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Configurer le bouton d'envoi
        btnSubmitReview.setOnClickListener(v -> submitReview());

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        etReviewTitle = view.findViewById(R.id.etReviewTitle);
        etReviewComment = view.findViewById(R.id.etReviewComment);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        emptyState = view.findViewById(R.id.emptyState);

        // Initialiser les étoiles
        stars = new ImageView[5];
        stars[0] = view.findViewById(R.id.star1);
        stars[1] = view.findViewById(R.id.star2);
        stars[2] = view.findViewById(R.id.star3);
        stars[3] = view.findViewById(R.id.star4);
        stars[4] = view.findViewById(R.id.star5);
    }

    private void setupStars() {
        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> {
                currentRating = rating;
                updateStars();
            });
        }
    }

    private void updateStars() {
        for (int i = 0; i < stars.length; i++) {
            if (i < currentRating) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_outline);
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new ReviewsAdapter(reviewsList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewReviews.setAdapter(adapter);

        updateEmptyState();
    }

    private void loadStats() {
        int reviewsCount = prefs.getReviewsCount();
        int pointsCount = prefs.getPointsCount();

        tvTotalReviews.setText(String.valueOf(reviewsCount));
        tvTotalPoints.setText(String.valueOf(pointsCount));
    }

    private void loadReviews() {
        // Charger les avis depuis PrefsManager
        String reviewsJson = prefs.getReviewsJson();
        if (!reviewsJson.isEmpty()) {
            reviewsList.addAll(Review.parseJsonList(reviewsJson));
        }
    }

    private void submitReview() {
        String title = etReviewTitle.getText().toString().trim();
        String comment = etReviewComment.getText().toString().trim();

        // Validation
        if (currentRating == 0) {
            Toast.makeText(requireContext(), "Veuillez donner une note", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez écrire un commentaire", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer le nouvel avis
        Review newReview = new Review(
                currentRating,
                title,
                comment,
                System.currentTimeMillis()
        );

        // Ajouter à la liste
        reviewsList.add(0, newReview); // Ajouter au début
        adapter.notifyItemInserted(0);
        recyclerViewReviews.smoothScrollToPosition(0);

        // Sauvegarder dans PrefsManager
        String reviewsJson = Review.toJsonList(reviewsList);
        prefs.saveReviewsJson(reviewsJson);

        // Incrémenter les statistiques
        prefs.incrementReview();
        prefs.incrementPoints(10); // +10 points

        // Mettre à jour l'affichage des stats
        loadStats();

        // Afficher message de succès
        Toast.makeText(requireContext(), "Avis envoyé ! +10 points 🎉", Toast.LENGTH_LONG).show();

        // Réinitialiser le formulaire
        resetForm();

        // Mettre à jour l'état vide
        updateEmptyState();
    }

    private void resetForm() {
        etReviewTitle.setText("");
        etReviewComment.setText("");
        currentRating = 0;
        updateStars();
    }

    private void updateEmptyState() {
        if (reviewsList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerViewReviews.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerViewReviews.setVisibility(View.VISIBLE);
        }
    }
}