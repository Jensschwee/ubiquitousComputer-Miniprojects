package uci.mmmi.sdu.dk.contextawarenessproject.pojos;

/**
 * Created by jens on 05-05-2017.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Geometry {

    private List<Double> coordinates = null;
    private String type;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
