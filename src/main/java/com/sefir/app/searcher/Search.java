package com.sefir.app.searcher;


/**
 * Class with all the data needed to perform search.
 */
public class Search {
    private String country;
    private String city;
    private String place;

    public Search(){
    }

    public Search(String country, String city, String place) {
        this.country = country;
        this.city = city;
        this.place = place;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String toString() {
        return getCountry() + " " + getCity() + " " + getPlace();
    }
}
