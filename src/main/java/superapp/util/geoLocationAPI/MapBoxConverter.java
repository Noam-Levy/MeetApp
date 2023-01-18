package superapp.util.geoLocationAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import superapp.util.exceptions.InvalidInputException;

import java.util.*;

public class MapBoxConverter {
        private ObjectMapper jackson;

        public MapBoxConverter() {
            this.jackson = new ObjectMapper();
        }
        public Map<String, Object> detailsToMap(String details) {
            try {
                return (Map<String, Object>)this.jackson.readValue(details, Map.class);
            }
            catch (Exception e) { throw new InvalidInputException(e.getMessage()); }
        }

        public HashMap<String, Object> mapToRestaurantDetails(LinkedHashMap resturant) {
            HashMap<String,Object> rv = new HashMap<String,Object>();
            String name = (String) resturant.get("text");
            rv.put("name",name);

            LinkedHashMap properties = (LinkedHashMap)resturant.get("properties");
            String address = (String) ((LinkedHashMap)properties).get("address");
            rv.put("address",address);
            String category = (String) ((LinkedHashMap)properties).get("category");
            rv.put("category",category);

            LinkedHashMap geo = (LinkedHashMap)resturant.get("geometry");
            List<String> coordinates = (List<String>) geo.get("coordinates");

            rv.put("coordinates",coordinates);
            ArrayList<LinkedHashMap> context = (ArrayList<LinkedHashMap>)resturant.get("context");
            String district= (String) context.get(1).get("text");
            rv.put("district",district);
            return rv;
        }

        public String coordinatesToString(List<Coordinate> coordinates) {
            StringBuffer rv = new StringBuffer();
            coordinates.forEach(coordinate ->
                    rv.append(coordinate.getLatitude()+","+coordinate.getLongitude()+";"));

            return rv.substring(0,rv.length()-2);
    }

    public HashMap<String, Object> filterMapBoxRequestToDirections(Map<String, Object> responseToMap) {
            HashMap<String,Object> rv = new HashMap<String,Object>();
            Map<String, Object> routes = (Map<String, Object>) ((ArrayList)responseToMap.get("routes")).get(0);
            rv.put("duration",routes.get("duration"));
            rv.put("distance",routes.get("distance"));

            Map<String, Object> legs = (Map<String, Object>) ((ArrayList)routes.get("legs")).get(0);
            ArrayList<LinkedHashMap> steps = (ArrayList<LinkedHashMap>) legs.get("steps");
            List<Map<String, Object>> fileredSteps = filterSteps(steps);

            rv.put("steps",fileredSteps);
             return rv;
    }

    private List<Map<String, Object>> filterSteps(ArrayList<LinkedHashMap> steps) {
        List<Map<String, Object>> rv = new ArrayList<>();
        steps
            .stream()
            .forEach(step -> {
                HashMap<String, Object> m = new HashMap<>();
                for (String key : new String[]{"name", "duration", "distance"})
                    m.put(key, step.get(key));

                HashMap<String, Object> maneuver = new HashMap<>();
                for (String key : new String[]{"type", "instruction"})
                    maneuver.put(key, ((LinkedHashMap)step.get("maneuver")).get(key));
                m.put("maneuver", maneuver);
                rv.add(m);
                });
        return rv;
    }
}
