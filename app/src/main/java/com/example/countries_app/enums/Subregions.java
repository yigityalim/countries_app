package com.example.countries_app.enums;

public enum Subregions {
    CARIBBEAN("Karayipler"),
    POLYNESIA("Polinezya"),
    AUSTRALIA_AND_NEW_ZEALAND("Avustralya ve Yeni Zelanda"),
    MIDDLE_AFRICA("Orta Afrika"),
    EASTERN_AFRICA("Doğu Afrika"),
    WESTERN_ASIA("Batı Asya"),
    SOUTHERN_EUROPE("Güney Avrupa"),
    SOUTHERN_ASIA("Güney Asya"),
    WESTERN_AFRICA("Batı Afrika"),
    WESTERN_EUROPE("Batı Avrupa"),
    MICRONESIA("Mikronezya"),
    EASTERN_ASIA("Doğu Asya"),
    SOUTHEAST_EUROPE("Güneydoğu Avrupa"),
    NORTHERN_AFRICA("Kuzey Afrika"),
    EASTERN_EUROPE("Doğu Avrupa"),
    SOUTH_EASTERN_ASIA("Güneydoğu Asya"),
    CENTRAL_AMERICA("Orta Amerika"),
    NORTH_AMERICA("Kuzey Amerika"),
    CENTRAL_ASIA("Orta Asya"),
    SOUTH_AMERICA("Güney Amerika"),
    MELANESIA("Melanezya"),
    SOUTHERN_AFRICA("Güney Afrika"),
    NORTHERN_EUROPE("Kuzey Avrupa"),
    CENTRAL_EUROPE("Orta Avrupa");

    private final String subregionName;

    Subregions(String regionName) {
        this.subregionName = regionName;
    }

    public String getSubregionName() {
        return subregionName;
    }
}
