package com.mysite.travelo.hyo.customcourse;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public class CustomCourseRequest {

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private Map<String, Integer> placeMap;

    @NotEmpty
    private String privateYn;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Integer> getPlaceMap() {
        return placeMap;
    }

    public void setPlaceMap(Map<String, Integer> placeMap) {
        this.placeMap = placeMap;
    }

    public String getPrivateYn() {
        return privateYn;
    }

    public void setPrivateYn(String privateYn) {
        this.privateYn = privateYn;
    }
}