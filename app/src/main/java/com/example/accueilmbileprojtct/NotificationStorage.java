package com.example.accueilmbileprojtct;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationStorage {

    private static final String PREF_NAME = "notif_pref";
    private static final String KEY_NOTIFS = "notifications";

    // Sauvegarde une notification avec sa destination
    public static void save(Context context, String title, String message, String target) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String oldData = prefs.getString(KEY_NOTIFS, "[]");

            JSONArray array = new JSONArray(oldData);

            JSONObject obj = new JSONObject();
            obj.put("title", title);
            obj.put("message", message);
            obj.put("date", getCurrentDate());
            obj.put("target", target);

            array.put(obj);
            prefs.edit().putString(KEY_NOTIFS, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Récupère toutes les notifications
    public static List<NotificationItem> getAll(Context context) {
        List<NotificationItem> list = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String data = prefs.getString(KEY_NOTIFS, "[]");

            JSONArray array = new JSONArray(data);
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new NotificationItem(
                        obj.getString("title"),
                        obj.getString("message"),
                        obj.getString("date"),
                        obj.getString("target")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());
    }
}
