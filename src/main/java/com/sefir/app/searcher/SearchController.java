package com.sefir.app.searcher;


import com.google.maps.model.LatLng;
import org.json.simple.JSONObject;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class SearchController {

    /**
     * This method returns places, which are close to given city's centre in JSON format
     * @param model - method gets Facebook binding object, Search object and latitude and longitude object to perform search
     * @return JSON with max. 25 places
     */
    @GetMapping(value="/search")
    public JSONObject returnJSONWithSearchResults(Model model){
        Search search = (Search)model.asMap().get("search");
        Facebook facebook = (Facebook)model.asMap().get("facebook");
        LatLng latLng = (LatLng)model.asMap().get("latLng");
        JSONObject jsonToBeReturned = new JSONObject();
        List<Page> pages = facebook.pageOperations().searchPlaces(search.getPlace(), latLng.lat, latLng.lng, 10000);
        int counter = 1;
        for (Page page: pages) {
            Page pg = facebook.fetchObject(page.getId(), Page.class, "location");
            JSONObject onePlace = new JSONObject();
            onePlace.put("name", page.getName());
            onePlace.put("latitude", pg.getLocation().getLatitude());
            onePlace.put("longitude", pg.getLocation().getLongitude());
            jsonToBeReturned.put("place" + counter++, onePlace);
        }
        return jsonToBeReturned;
    }
}
