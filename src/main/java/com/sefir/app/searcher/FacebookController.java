package com.sefir.app.searcher;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import org.springframework.http.MediaType;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/fb")
public class FacebookController {
    private static final int ERROR = -1000;
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public FacebookController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    /**
     * This method, given country and city Strings, returns from GoogleGeocoding API latitude and longitude of given city.
     * @param country name of the country
     * @param city name of the city
     * @return LatLng object, containing two Doubles: latitude and longitude
     * @throws InterruptedException GoogleAPI exception
     * @throws ApiException GoogleAPI exception
     * @throws IOException GoogleAPI exception
     */
    static LatLng getLatitudeAndLongitudeFromCountryAndCity(String country, String city) throws InterruptedException, ApiException, IOException {
        String API_KEY = "AIzaSyD399eBkXR1jml6Vt3D7Sy5Sytn1sCv90U";
        GeoApiContext context = new GeoApiContext().setApiKey(API_KEY);
        GeocodingApiRequest req = GeocodingApi.newRequest(context).address(country + " " + city + ", ");
        GeocodingResult[] results =  req.await();
        if(results.length != 0) {
            return results[0].geometry.location;
        } else {
            return new LatLng(ERROR,ERROR);
        }
    }

    /**
     * @param model - used for errors (state tells us
     * @param search - Valid Search object from the form visible on index.html
     * @param redirectAttributes these are given to RESTController so he can produce JSON with them
     * @return index when some kind of error occurs, redirects to /search if we can perform the search
     * @throws InterruptedException GoogleAPI exception
     * @throws ApiException GoogleAPI exception
     * @throws IOException GoogleAPI exception
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String helloFacebook(Model model, @Valid Search search, RedirectAttributes redirectAttributes) throws InterruptedException, ApiException, IOException {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            model.addAttribute("state", 0);
            model.addAttribute("message", "Please, log in to Facebook before searching for places.");
            return "index";
        }
        LatLng latLng = getLatitudeAndLongitudeFromCountryAndCity(search.getCountry(), search.getCity());
        if(latLng.lat == ERROR) {
            model.addAttribute("state", 0);
            model.addAttribute("message", "There were errors in Your country or city.");
            return "index";
        }
        redirectAttributes.addFlashAttribute("latLng", latLng);
        redirectAttributes.addFlashAttribute("search", search);
        redirectAttributes.addFlashAttribute("facebook", facebook);
        return "redirect:/search";
    }

    /**
     * If user is not logged to Facebook, this method redirects him to view, which is responsible for logging in.
     * @param model used for sending a message to view
     * @return index if user is logged in, redirects otherwise.
     */
    @GetMapping
    public String getFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }
        model.addAttribute("state", 1);
        model.addAttribute("message", "You are already logged in to Facebook.");
        return "index";
    }

}