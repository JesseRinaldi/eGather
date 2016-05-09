package com.rinaldi.jesse.egather;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jesse on 4/30/2016.
 */
public class Event {
    private String name, locationAddress, locationId, locationName, body, website, websiteTitle, category, photoURL, mod;
    private int month=-1, day=-1, year=-1, startHour=-1, startMinute=-1, endHour=-1, endMinute=-1;
    private Boolean inviteOnly, closedInvites;
    private double latitude, longitude;
    //private String[] tags;

    public Event() {}
    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Event setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocationAddress() { return locationAddress; }
    public String getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public Event setLocation(Place location) {
        this.locationAddress = location.getAddress().toString();
        this.locationId = location.getId().toString();
        this.locationName = location.getName().toString();
        this.latitude = location.getLatLng().latitude;
        this.longitude = location.getLatLng().longitude;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Event setBody(String body) {
        this.body = body;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public Event setWebsite(String website) {
        this.website = website;
        return this;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public Event setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
        return this;
    }

    public String getMod() {
        return mod;
    }

    public Event setMod(String mod) {
        this.mod = mod;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Event setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getWebsiteTitle() {
        return websiteTitle;
    }

    public Event setWebsiteTitle(String websiteTitle) {
        this.websiteTitle = websiteTitle;
        return this;
    }
/*
    public String[] getTags() { return tags; }

    public Event setTags(String tags) {
        this.tags = tags.split(",");
        for (int i = 0; i < this.tags.length; i++){
            this.tags[i].trim();
        }
        return this;
    } */

    public Boolean getInviteOnly() {
        return inviteOnly;
    }

    public Event setInviteOnly(Boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
        return this;
    }

    public Boolean getClosedInvites() {
        return closedInvites;
    }

    public Event setClosedInvites(Boolean closedInvites) {
        this.closedInvites = closedInvites;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public Event setDate(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public Event setTime(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        return this;
    }
}


