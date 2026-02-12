package com.example.accueilmbileprojtct;

public class NotificationItem {

    private String title;
    private String message;
    private String date;
    private String target; // home / category / product

    public NotificationItem(String title, String message, String date, String target) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.target = target;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getTarget() { return target; }
}
