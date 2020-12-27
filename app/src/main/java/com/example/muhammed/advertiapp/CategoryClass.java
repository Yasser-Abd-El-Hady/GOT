package com.example.muhammed.advertiapp;

/**
 * Created by mido elgebaly1 on 11/6/2017.
 */

public class CategoryClass {

    private String id;
    private String name;
    private String parent_id;
    private String arabicName;
    private String visibility;
    private String numberAds;

    public CategoryClass(String id, String name, String parent_id, String arabicName, String visibility) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.arabicName = arabicName;
        this.visibility = visibility;
    }

    public CategoryClass(String id, String name, String parent_id, String arabicName, String visibility, String numberAds) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.arabicName = arabicName;
        this.visibility = visibility;
        this.numberAds = numberAds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }


    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getNumberAds() {
        return numberAds;
    }

    public void setNumberAds(String numberAds) {
        this.numberAds = numberAds;
    }
}
