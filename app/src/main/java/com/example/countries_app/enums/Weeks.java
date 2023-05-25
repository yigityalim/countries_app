package com.example.countries_app.enums;

public enum Weeks {
    MONDAY("Pazartesi"),
    SATURDAY("Cumartesi"),
    SUNDAY("Pazar");
    private final String day;

    Weeks(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }
}