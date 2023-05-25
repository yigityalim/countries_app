package com.example.countries_app.enums;

public enum Regions {
    AFRICA("Afrika"),
    ASIA("Asya"),
    EUROPE("Avrupa"),
    AMERICAS("Amerika"),
    OCEANIA("Okyanusya"),
    ANTARCTIC("Antarktika");

    private final String regionName;

    Regions(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionName() {
        return regionName;
    }
}
