package com.example.accueilmbileprojtct;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer plusieurs utilisateurs dans l'application
 * Permet de sauvegarder et charger les données de chaque utilisateur
 */
public class UserManager {
    private static final String PREFS_NAME = "beauty_users";
    private static final String KEY_USERS_LIST = "users_list";
    private static final String KEY_CURRENT_USER = "current_user_email";

    private final SharedPreferences prefs;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Enregistrer un nouvel utilisateur
     */
    public void registerUser(String username, String firstName, String lastName, String email, String password) {
        List<UserData> users = getAllUsers();

        // Vérifier si l'email existe déjà
        for (UserData user : users) {
            if (user.email.equals(email)) {
                // Utilisateur existe déjà, mettre à jour ses infos
                user.username = username;
                user.firstName = firstName;
                user.lastName = lastName;
                user.password = password;
                saveAllUsers(users);
                return;
            }
        }

        // Nouvel utilisateur
        UserData newUser = new UserData();
        newUser.username = username;
        newUser.firstName = firstName;
        newUser.lastName = lastName;
        newUser.email = email;
        newUser.password = password;

        users.add(newUser);
        saveAllUsers(users);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<UserData> getAllUsers() {
        List<UserData> users = new ArrayList<>();
        String usersJson = prefs.getString(KEY_USERS_LIST, "");

        if (usersJson.isEmpty()) {
            return users;
        }

        try {
            JSONArray jsonArray = new JSONArray(usersJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJson = jsonArray.getJSONObject(i);
                UserData user = UserData.fromJson(userJson);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Sauvegarder tous les utilisateurs
     */
    private void saveAllUsers(List<UserData> users) {
        JSONArray jsonArray = new JSONArray();
        for (UserData user : users) {
            jsonArray.put(user.toJson());
        }
        prefs.edit().putString(KEY_USERS_LIST, jsonArray.toString()).apply();
    }

    /**
     * Vérifier les identifiants et définir l'utilisateur actuel
     */
    public boolean login(String email, String password) {
        List<UserData> users = getAllUsers();

        for (UserData user : users) {
            if (user.email.equals(email) && user.password.equals(password)) {
                setCurrentUser(email);
                return true;
            }
        }

        return false;
    }

    /**
     * Définir l'utilisateur actuellement connecté
     */
    public void setCurrentUser(String email) {
        prefs.edit().putString(KEY_CURRENT_USER, email).apply();
    }

    /**
     * Récupérer l'email de l'utilisateur actuellement connecté
     */
    public String getCurrentUserEmail() {
        return prefs.getString(KEY_CURRENT_USER, "");
    }

    /**
     * Récupérer les données de l'utilisateur actuel
     */
    public UserData getCurrentUser() {
        String currentEmail = getCurrentUserEmail();
        if (currentEmail.isEmpty()) {
            return null;
        }

        List<UserData> users = getAllUsers();
        for (UserData user : users) {
            if (user.email.equals(currentEmail)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Déconnexion
     */
    public void logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply();
    }

    /**
     * Classe pour stocker les données d'un utilisateur
     */
    public static class UserData {
        public String username;
        public String firstName;
        public String lastName;
        public String email;
        public String password;

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("username", username);
                json.put("firstName", firstName);
                json.put("lastName", lastName);
                json.put("email", email);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        public static UserData fromJson(JSONObject json) {
            try {
                UserData user = new UserData();
                user.username = json.getString("username");
                user.firstName = json.getString("firstName");
                user.lastName = json.getString("lastName");
                user.email = json.getString("email");
                user.password = json.getString("password");
                return user;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}