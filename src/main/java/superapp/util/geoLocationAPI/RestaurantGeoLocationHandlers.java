package superapp.util.geoLocationAPI;

import java.util.ArrayList;
import java.util.Map;

public interface RestaurantGeoLocationHandlers {
    public ArrayList<Map<String, Object>> getRestaurantByCuisine(String cuisine, int limit, int radius);
}
