package com.example;

import java.io.Serializable;

public class ReviewBean implements Serializable {
    private String id;
    private String country;
    private String description;
    private String designation;
    private Integer points;
    private Double price;
    private String province;
    private String region_1;
    private String region_2;
    private String taster_name;

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(final String designation) {
        this.designation = designation;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(final Integer points) {
        this.points = points;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(final String province) {
        this.province = province;
    }

    public String getRegion_1() {
        return this.region_1;
    }

    public void setRegion_1(final String region_1) {
        this.region_1 = region_1;
    }

    public String getRegion_2() {
        return this.region_2;
    }

    public void setRegion_2(final String region_2) {
        this.region_2 = region_2;
    }

    public String getTaster_name() {
        return this.taster_name;
    }

    public void setTaster_name(final String taster_name) {
        this.taster_name = taster_name;
    }

    public String getTaster_twitter_handle() {
        return this.taster_twitter_handle;
    }

    public void setTaster_twitter_handle(final String taster_twitter_handle) {
        this.taster_twitter_handle = taster_twitter_handle;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getVariety() {
        return this.variety;
    }

    public void setVariety(final String variety) {
        this.variety = variety;
    }

    public String getWinery() {
        return this.winery;
    }

    public void setWinery(final String winery) {
        this.winery = winery;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    private String taster_twitter_handle;
    private String title;
    private String variety;
    private String winery;


}
