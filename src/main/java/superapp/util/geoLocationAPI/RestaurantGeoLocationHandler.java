package superapp.util.geoLocationAPI;

import org.springframework.web.client.RestTemplate;
import superapp.util.exceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestaurantGeoLocationHandler extends MapBox implements RestaurantGeoLocationHandlers {
    private String CuisineURL;
    private MapBoxConverter mapBoxConverter;

    public RestaurantGeoLocationHandler(MapBoxConverter mapBoxConverter) {
        super();
        this.CuisineURL = super.getBaseUrl() + "geocoding/v5/mapbox.places/";
        this.mapBoxConverter = mapBoxConverter;
    }

    @Override
    public ArrayList<Map<String, Object>> getRestaurantByCuisine(String cuisine, int limit, int radius){
        RestTemplate restTemplate = new RestTemplate();
        String url = this.CuisineURL +cuisine+".json?country=IL&proximity=" + super.getCollageLocation() +"&limit="+limit+"&radius="+radius+"&access_token="
                +super.getKey();

        String response = restTemplate.getForObject(url, String.class);
        Map<String, Object> responseToMap =mapBoxConverter.detailsToMap(response);
        ArrayList<LinkedHashMap> restaurants = (ArrayList<LinkedHashMap>) responseToMap.get("features");

        if(restaurants.size() ==0)
            throw new InvalidInputException("cuisine Not Found");

        return extractRestaurantFromMap(restaurants);
    }

    private ArrayList<Map<String, Object>> extractRestaurantFromMap(ArrayList<LinkedHashMap> restaurants){
        ArrayList<Map<String, Object>> rv = new ArrayList<Map<String, Object>>();
        restaurants.forEach(restaurant -> rv.add(mapBoxConverter.mapToRestaurantDetails(restaurant)));
        return rv;
    }
}
