package com.example.accueilmbileprojtct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Review {
    private int rating;
    private String title;
    private String comment;
    private long timestamp;

    public Review(int rating, String title, String comment, long timestamp) {
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters
    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
        return sdf.format(new Date(timestamp));
    }

    // Conversion JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("rating", rating);
            json.put("title", title);
            json.put("comment", comment);
            json.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Review fromJson(JSONObject json) {
        try {
            return new Review(
                    json.getInt("rating"),
                    json.getString("title"),
                    json.getString("comment"),
                    json.getLong("timestamp")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Conversion liste → JSON
    public static String toJsonList(List<Review> reviews) {
        JSONArray jsonArray = new JSONArray();
        for (Review review : reviews) {
            jsonArray.put(review.toJson());
        }
        return jsonArray.toString();
    }

    // Conversion JSON → liste
    public static List<Review> parseJsonList(String jsonString) {
        List<Review> reviews = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                Review review = fromJson(jsonArray.getJSONObject(i));
                if (review != null) {
                    reviews.add(review);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}