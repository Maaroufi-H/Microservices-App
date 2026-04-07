package net.maaroufi.geolocationservice.dto;

public class CountryStatsDTO {

    private String country;
    private String countryCode;
    private Long userCount;

    public CountryStatsDTO() {}

    public CountryStatsDTO(String country, String countryCode, Long userCount) {
        this.country = country;
        this.countryCode = countryCode;
        this.userCount = userCount;
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Long getUserCount() { return userCount; }
    public void setUserCount(Long userCount) { this.userCount = userCount; }
}
