package net.maaroufi.trackingservice.dto;

public class UtmParameters {

    private String utmSource;    // ex: "facebook"
    private String utmMedium;    // ex: "cpc"
    private String utmCampaign;  // ex: "summer-sale-2025"
    private String utmContent;   // ex: "image-ad-v2"
    private String utmTerm;      // ex: "dropshipping shoes"

    public UtmParameters() {}

    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }

    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }

    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }

    public String getUtmContent() { return utmContent; }
    public void setUtmContent(String utmContent) { this.utmContent = utmContent; }

    public String getUtmTerm() { return utmTerm; }
    public void setUtmTerm(String utmTerm) { this.utmTerm = utmTerm; }
}
