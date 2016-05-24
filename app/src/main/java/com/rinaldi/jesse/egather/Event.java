package com.rinaldi.jesse.egather;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.HashMap;
import java.util.Map;

/**
 * NAME
 *      Event
 * DESCRIPTION
 *      This class represents an eGather event. All of its private data member are simple data
 *      types because that makes it easier for it to be retrieved from the Firebase server
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      3/30/2016
 */
public class Event {
    private String name, locationAddress, locationId, locationName, body, tags, website, websiteTitle, category, photoURL, mod="";
    private int month=-1, day=-1, year=-1, startHour=-1, startMinute=-1, endHour=-1, endMinute=-1;
    private Boolean inviteOnly, closedInvites;
    private double latitude, longitude, dateTimeSort = -1;

    /**
     * NAME
     *      Event (Constructor)
     * DESCRIPTION
     *      Base constructor for Event class
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event() {}

    /**
     * NAME
     *      Event (Constructor)
     * SYNOPSIS
     *      @param name - Name of event
     * DESCRIPTION
     *      Second constructor to set name on creation
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event(String name) {
        this.name = name;
    }

    /**
     * NAME
     *      Event.getName
     * DESCRIPTION
     *      Getter for name
     * AUTHOR
     *      @author Jesse Rinaldi
     * RETURN
     *      @return name - name of event
     * DATE
     *      3/30/2016
     */
    public String getName() {
        return name;
    }

    /**
     * NAME
     *      Event.setName
     * SYNOPSIS
     *      @param name - Name of event
     * DESCRIPTION
     *      Setter for name
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * NAME
     *      Event.getLocationAddress
     * DESCRIPTION
     *      Getter for locationAddress
     * RETURN
     *      @return locationAddress - Address of event location
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getLocationAddress() { return locationAddress; }

    /**
     * NAME
     *      Event.getLocationId
     * DESCRIPTION
     *      Getter for locationId
     * RETURNS
     *      @return locationId - Google ID for location
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getLocationId() { return locationId; }

    /**
     * NAME
     *      Event.getLocationName
     * DESCRIPTION
     *      Getter for locationName
     * RETURNS
     *      @return locationName - Name of location, sometimes useless for house addresses
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getLocationName() { return locationName; }

    /**
     * NAME
     *      Event.getLatitude
     * DESCRIPTION
     *      Getter for latitude
     * RETURNS
     *      @return latitude - Latitude of location
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public double getLatitude() { return latitude; }

    /**
     * NAME
     *      Event.getLongitude
     * DESCRIPTION
     *      Getter for longitude
     * RETURNS
     *      @return longitude - longitude of location
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public double getLongitude() { return longitude; }

    /**
     * NAME
     *      Event.setLocationAddress
     * SYNOPSIS
     *      @param locationAddress - Address string
     * DESCRIPTION
     *      Setter for locationAddress
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
        return this;
    }

    /**
     * NAME
     *      Event.setLocationId
     * SYNOPSIS
     *      @param locationId - Google Id for location
     * DESCRIPTION
     *      Setter for locationId
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    /**
     * NAME
     *      Event.setLocationName
     * SYNOPSIS
     *      @param locationName - Name of location
     * DESCRIPTION
     *      Setter for locationName
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    /**
     * NAME
     *      Event.setLatitude
     * SYNOPSIS
     *      @param latitude - Latitude of location
     * DESCRIPTION
     *      Setter for latitude
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * NAME
     *      Event.setLongitude
     * SYNOPSIS
     *      @param longitude - Longitude of location
     * DESCRIPTION
     *      Setter for longitude
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * NAME
     *      Event.setName
     * SYNOPSIS
     *      @param location - Google Place object for event's location
     * DESCRIPTION
     *      To easily set locationAddress, locationId, locationName, longitude and latitude,
     *      use this method with uses a Google Place object
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setLocation(Place location) {
        this.locationAddress = location.getAddress().toString();
        this.locationId = location.getId().toString();
        this.locationName = location.getName().toString();
        this.latitude = location.getLatLng().latitude;
        this.longitude = location.getLatLng().longitude;
        return this;
    }

    /**
     * NAME
     *      Event.getBody
     * DESCRIPTION
     *      Getter for body
     * RETURNS
     *      @return body - Description of event
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getBody() {
        return body;
    }

    /**
     * NAME
     *      Event.setBody
     * SYNOPSIS
     *      @param body - Description of event
     * DESCRIPTION
     *      Setter for body
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * NAME
     *      Event.getWebsite
     * DESCRIPTION
     *      Getter for website
     * RETURNS
     *      @return website - URL string
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getWebsite() {
        return website;
    }

    public Event setWebsite(String website) {
        this.website = website;
        return this;
    }

    /**
     * NAME
     *      Event.getPhotoURL
     * DESCRIPTION
     *      Getter for photoURL
     * RETURNS
     *      @return photoURL - URL string for image
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      5/10/2016
     */
    public String getPhotoURL() {
        return photoURL;
    }

    /**
     * NAME
     *      Event.setPhotoURL
     * SYNOPSIS
     *      @param photoURL - URL string for image
     * DESCRIPTION
     *      Setter for photoURL
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      5/10/2016
     */
    public Event setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
        return this;
    }

    /**
     * NAME
     *      Event.getMod
     * DESCRIPTION
     *      Getter for mod
     * RETURNS
     *      @return mod - User ID for event's creator
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getMod() {
        return mod;
    }

    /**
     * NAME
     *      Event.setMod
     * SYNOPSIS
     *      @param mod - User ID for event's creator
     * DESCRIPTION
     *      Setter for mod
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setMod(String mod) {
        this.mod = mod;
        return this;
    }

    /**
     * NAME
     *      Event.getCategory
     * DESCRIPTION
     *      Getter for category
     * RETURNS
     *      @return category - Category of event
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getCategory() {
        return category;
    }

    /**
     * NAME
     *      Event.setCategory
     * SYNOPSIS
     *      @param category - Category of event
     * DESCRIPTION
     *      Setter for category
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * NAME
     *      Event.getWebsiteTitle
     * DESCRIPTION
     *      Getter for websiteTitle
     * RETURNS
     *      @return websiteTitle - Title for website's hyperlink
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getWebsiteTitle() {
        return websiteTitle;
    }

    /**
     * NAME
     *      Event.setWebsiteTitle
     * SYNOPSIS
     *      @param websiteTitle - Title for website's hyperlink
     * DESCRIPTION
     *      Setter for websiteTitle
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setWebsiteTitle(String websiteTitle) {
        this.websiteTitle = websiteTitle;
        return this;
    }

    /**
     * NAME
     *      Event.getTags
     * DESCRIPTION
     *      Getter for tags
     * RETURNS
     *      @return tags - String of words to search for
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public String getTags() { return tags; }

    /**
     * NAME
     *      Event.setTags
     * SYNOPSIS
     *      @param tags - String of words to search for
     * DESCRIPTION
     *      Setter for tags
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setTags(String tags) {
        this.tags = tags;
        return this;
    }

    /**
     * NAME
     *      Event.getInviteOnly
     * DESCRIPTION
     *      Getter for inviteOnly
     * RETURNS
     *      @return inviteOnly - true if event is invite-only
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Boolean getInviteOnly() {
        return inviteOnly;
    }

    /**
     * NAME
     *      Event.setName
     * SYNOPSIS
     *      @param inviteOnly - true if event is invite-only
     * DESCRIPTION
     *      Setter for inviteOnly
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setInviteOnly(Boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
        return this;
    }

    /**
     * NAME
     *      Event.getClosedInvites
     * DESCRIPTION
     *      Getter for closedInvites
     * RETURNS
     *      @return closedInvites - true to prevent non-mods from inviting users to event
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Boolean getClosedInvites() {
        return closedInvites;
    }

    /**
     * NAME
     *      Event.setClosedInvites
     * SYNOPSIS
     *      @param closedInvites - true to prevent non-mods from inviting users to event
     * DESCRIPTION
     *      Setter for closedInvites
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setClosedInvites(Boolean closedInvites) {
        this.closedInvites = closedInvites;
        return this;
    }

    /**
     * NAME
     *      Event.getMonth
     * DESCRIPTION
     *      Getter for month
     * RETURNS
     *      @return month - 1-12 for month
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getMonth() {
        return month;
    }

    /**
     * NAME
     *      Event.getDay
     * DESCRIPTION
     *      Getter for day
     * RETURNS
     *      @return day - 1-31 for day
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getDay() {
        return day;
    }

    /**
     * NAME
     *      Event.getYear
     * DESCRIPTION
     *      Getter for year
     * RETURNS
     *      @return year - year as int
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getYear() {
        return year;
    }

    /**
     * NAME
     *      Event.setDate
     * SYNOPSIS
     *      @param month - 1-12 for month
     *      @param day - 1-31 for day
     *      @param year - year as int
     * DESCRIPTION
     *      Setter for month, day, and year together.
     *      Also sets dateTimeSort with that data.
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setDate(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
        setDateTimeSort();
        return this;
    }

    /**
     * NAME
     *      Event.getStartHour
     * DESCRIPTION
     *      Getter for startHour
     * RETURNS
     *      @return startHour - Hour 0-23
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * NAME
     *      Event.getStartMinute
     * DESCRIPTION
     *      Getter for startMinute
     * RETURNS
     *      @return startMinute - Minute 0-59
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * NAME
     *      Event.getEndHour
     * DESCRIPTION
     *      Getter for endHour
     * RETURNS
     *      @return endHour - Hour 0-23
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * NAME
     *      Event.getEndMinute
     * DESCRIPTION
     *      Getter for endMinute
     * RETURNS
     *      @return endMinute - Minute 0-59
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * NAME
     *      Event.setTime
     * SYNOPSIS
     *      @param startHour - Hour 0-23
     *      @param startMinute - Minute 0-59
     *      @param endHour - Hour 0-23
     *      @param endMinute - Minute 0-59
     * DESCRIPTION
     *      Setter for all time values together.
     *      Recalculates dateTimeSort
     * RETURNS
     *      @return this - for method chaining
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    public Event setTime(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        setDateTimeSort();
        return this;
    }

    /**
     * NAME
     *      Event.getDateTimeSort
     * DESCRIPTION
     *      Getter for dateTimeSort
     * RETURNS
     *      @return dateTimeSort - Double for sorting by Date & Time: Format YYYYMMDDHHMM
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      5/21/2016
     */
    public double getDateTimeSort() { return dateTimeSort;}

    /**
     * NAME
     *      Event.setDateTimeSort
     * DESCRIPTION
     *      Calculates the dateTimeSort based on date & time members
     *      Format YYYYMMDDHHMM
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      3/30/2016
     */
    private void setDateTimeSort() {
        double sh = (startHour == -1 ? 0 : startHour);
        double sm = (startMinute == -1 ? 0 : startMinute);
        double m = (month == -1 ? 0 : month);
        double d = (day == -1 ? 0 : day);
        double y = (year == -1 ? 0 : year);
        dateTimeSort = y*100000000 + m*1000000 + d*10000 + sh*100 + sm;
        //If startTime wasn't set, decrement by .1 so events that start at
        //midnight have a larger value
        if (startHour == -1) dateTimeSort -= .1;
    }
}


