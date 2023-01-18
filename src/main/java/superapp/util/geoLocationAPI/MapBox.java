package superapp.util.geoLocationAPI;

public abstract class MapBox {
    protected final String AFEKA_COLLEGE_LOCATION = "34.818080852283444,32.114965594875784";
    protected final String API_KEY = System.getenv("MAP_BOX_API_KEY");
    protected final String BASE_URL = "https://api.mapbox.com/";

    public String getCollageLocation() {
        return AFEKA_COLLEGE_LOCATION;
    }

    public String getKey() {
        return API_KEY;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }
}
