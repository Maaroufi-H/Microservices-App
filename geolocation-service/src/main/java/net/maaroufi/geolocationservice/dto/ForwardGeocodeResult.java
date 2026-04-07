package net.maaroufi.geolocationservice.dto;

public class ForwardGeocodeResult {

    private String displayName;
    private Double lat;
    private Double lon;
    private String type;
    private Double importance;

    public ForwardGeocodeResult() {}

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getImportance() { return importance; }
    public void setImportance(Double importance) { this.importance = importance; }
}
