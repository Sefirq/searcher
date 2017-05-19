package com.sefir.app.searcher;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class GetRequestController {
    int ERROR = -1000;
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public GetRequestController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Given country name, city name and desired place, method searches for latitude and longitude of this city to be used by REST Controller
     * @param country name of country
     * @param city name of city
     * @param place name of place
     * @param model it is needed to make an error message
     * @param redirectAttributes these are given to RESTController so he can produce JSON with them
     * @return view - index if there were errors, or redirect to /search when data is valid
     * @throws InterruptedException GoogleAPI exception
     * @throws ApiException GoogleAPI exception
     * @throws IOException GoogleAPI exception
     */
    @GetMapping(value="/{country}/{city}/{place}")
    public String test(@PathVariable(value = "country") String country, @PathVariable(value = "city") String city
            , @PathVariable(value = "place") String place, Model model, RedirectAttributes redirectAttributes) throws InterruptedException, ApiException, IOException {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        } else {
            LatLng latLng = FacebookController.getLatitudeAndLongitudeFromCountryAndCity(country, city);
            if(latLng.lat == ERROR) {
                model.addAttribute("state", 0);
                model.addAttribute("message", "There were errors in Your country or city.");
                return "index";
            }
            redirectAttributes.addFlashAttribute("latLng", latLng);
            redirectAttributes.addFlashAttribute("search", new Search(country, city, place));
            redirectAttributes.addFlashAttribute("facebook", facebook);
            return "redirect:/search";
        }
    }
}
