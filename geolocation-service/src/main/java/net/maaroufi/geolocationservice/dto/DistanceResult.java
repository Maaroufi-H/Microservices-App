package net.maaroufi.geolocationservice.dto;

public class DistanceResult {

    private Double lat1;
    private Double lng1;
    private Double lat2;
    private Double lng2;
    private Double distanceKm;
    private Double distanceMiles;

    public DistanceResult() {}

    public DistanceResult(double lat1, double lng1, double lat2, double lng2,
                          double distanceKm, double distanceMiles) {
        this.lat1 = lat1;
        this.lng1 = lng1;
        this.lat2 = lat2;
        this.lng2 = lng2;
        this.distanceKm = distanceKm;
        this.distanceMiles = distanceMiles;
    }

    public Double getLat1() { return lat1; }
    public void setLat1(Double lat1) { this.lat1 = lat1; }

    public Double getLng1() { return lng1; }
    public void setLng1(Double lng1) { this.lng1 = lng1; }

    public Double getLat2() { return lat2; }
    public void setLat2(Double lat2) { this.lat2 = lat2; }

    public Double getLng2() { return lng2; }
    public void setLng2(Double lng2) { this.lng2 = lng2; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDistanceMiles() { return distanceMiles; }
    public void setDistanceMiles(Double distanceMiles) { this.distanceMiles = distanceMiles; }
}
