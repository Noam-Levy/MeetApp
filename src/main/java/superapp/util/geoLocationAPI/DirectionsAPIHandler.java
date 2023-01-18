package superapp.util.geoLocationAPI;

import org.springframework.web.client.RestTemplate;
import superapp.util.exceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DirectionsAPIHandler extends MapBox implements DirectionsAPIHandlers {
    private String directionURL;
    private String searchPlacesURL;
    private MapBoxConverter mapBoxConverter;

    public DirectionsAPIHandler(MapBoxConverter mapBoxConverter) {
        super();
        this.directionURL = super.getBaseUrl() + "directions/v5/mapbox/driving/";
        this.searchPlacesURL = super.getBaseUrl() +"geocoding/v5/mapbox.places/";
        this.mapBoxConverter = mapBoxConverter;
    }

    private Map<String,Object> getDirectionByCoordinates(String language, ArrayList<Coordinate> coordinates){
        if(coordinates.size() <2 || coordinates.size() >25)
            throw new InvalidInputException("Please Provide 2 to 25 coordinates ");
        RestTemplate restTemplate = new RestTemplate();
        String coordinatesConverted = mapBoxConverter.coordinatesToString(coordinates);
        String url = this.directionURL +coordinatesConverted+"?language="+language+"&steps=true&access_token="
                +super.getKey();

        String response = restTemplate.getForObject(url, String.class);
        Map<String, Object> responseToMap =mapBoxConverter.detailsToMap(response);
        if(responseToMap.get("code") != "Ok")
            throw new InvalidInputException("MapBoxAPI failed to provide directions! Check again your input.");
       return mapBoxConverter.filterMapBoxRequestToDirections(responseToMap);
    }

    @Override
    public Map<String,Object> getDirectionsByAddress(String language,ArrayList<String> addresses) {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        addresses.forEach(address -> coordinates.add(addressToCoordinates(address)));
        return getDirectionByCoordinates(language,coordinates);
    }

    private Coordinate addressToCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = this.searchPlacesURL+address+".json?&limit=1&access_token="+super.getKey();
        String response = restTemplate.getForObject(url, String.class);
        Map<String, Object> responseToMap =mapBoxConverter.detailsToMap(response);
        //todo check if res is OK (200)
        ArrayList< LinkedHashMap> features = (ArrayList< LinkedHashMap>)responseToMap.get("features");
        if(features.size() ==0)
            throw new InvalidInputException("MapBoxAPI failed to provide directions please check again your address ");
        ArrayList<Double> center = (ArrayList<Double>) features.get(0).get("center");
        return new Coordinate(center.get(0), center.get(1));
    }

}
