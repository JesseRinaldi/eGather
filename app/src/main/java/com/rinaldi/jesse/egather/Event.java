package com.rinaldi.jesse.egather;

/**
 * Created by Jesse on 4/30/2016.
 */
public class Event {
    private String name, address, city, state, zip, body, website, mod;
    private Genre genre; private WebsiteType websiteType;
    private Double price;
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

    public String getAddress() {
        return address;
    }

    public Event setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Event setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public Event setState(String state) {
        this.state = state;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public Event setZip(String zip) {
        this.zip = zip;
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

    public String getMod() {
        return mod;
    }

    public Event setMod(String mod) {
        this.mod = mod;
        return this;
    }

    public Genre getGenre() {
        return genre;
    }

    public Event setGenre(Genre genre) {
        this.genre = genre;
        return this;
    }

    public WebsiteType getWebsiteType() {
        return websiteType;
    }

    public Event setWebsiteType(WebsiteType websiteType) {
        this.websiteType = websiteType;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public Event setPrice(Double price) {
        this.price = price;
        return this;
    }

    public enum Genre {
        MUSIC, COMEDY, SPORTS, PARTY, ENTERTAINMENT, THEATER, CONVENTION, FESTIVAL, OTHER
    }
    public enum WebsiteType {
        FACEBOOK, TWITTER, INSTAGRAM, TICKETMASTER, REVERBNATION, LIVENATION, BANDCAMP, OTHER
    }
}


