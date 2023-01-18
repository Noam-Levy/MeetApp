package superapp.util.geoLocationAPI;

import java.util.ArrayList;
import java.util.Map;

public interface DirectionsAPIHandlers {
    public Map<String,Object> getDirectionsByAddress(String language, ArrayList<String> addresses);
}
