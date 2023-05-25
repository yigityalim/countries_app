package com.example.countries_app;

public class Country {
    private String name;
    private String flagUrl;

    public Country(String name, String flagUrl) {
        this.name = name;
        this.flagUrl = flagUrl;
    }

    public String getName() {
        return name;
    }

    public String getFlagUrl() {
        return flagUrl;
    }
}
