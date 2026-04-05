package net.maaroufi.core.customer;

/**
 * Contract for any entity that can participate in a transaction across any domain.
 *
 * E-commerce : Customer implements ICustomer
 * Football   : FootPlayer implements ICustomer
 */
public interface ICustomer {

    Long getId();

    String getEmail();

    /** Full display name (e.g. "John Doe", "FC Barcelona"). */
    String getDisplayName();

    /**
     * ML feature — preferred segment/category derived from behavior history.
     * E-commerce : preferredCategory ("electronics", "clothing"...)
     * Football   : preferredPosition ("GK", "ST"...)
     */
    String getPreferredSegment();

    /**
     * ML feature — ISO 3166-1 alpha-2 country code (e.g. "FR", "MA", "US").
     */
    String getCountryCode();
}
