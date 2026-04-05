package net.maaroufi.core.recommendation;

/**
 * A single scored recommendation returned by the ML model.
 */
public interface IProductRecommendation {

    Long getCatalogItemId();

    String getDisplayName();

    /** Confidence score from the ML model, between 0.0 and 1.0. */
    double getScore();

    String getDomainNamespace();
}
