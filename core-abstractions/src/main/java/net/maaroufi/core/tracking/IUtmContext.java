package net.maaroufi.core.tracking;

/**
 * UTM parameters attached to an event (from paid ads, email campaigns, etc.).
 */
public interface IUtmContext {
    String getUtmSource();
    String getUtmMedium();
    String getUtmCampaign();
    String getUtmContent();
    String getUtmTerm();
}
