package net.maaroufi.core.recommendation;

import java.util.List;

/**
 * Contract for a recommendation engine that returns scored catalog items for a customer.
 */
public interface IRecommendationEngine {

    /**
     * @param customerId      ID of the ICustomer requesting recommendations
     * @param domainNamespace Namespace of the domain ("ecommerce", "football", ...)
     * @param topN            Maximum number of recommendations to return
     * @return Ordered list of recommendations (highest score first)
     */
    List<? extends IProductRecommendation> recommend(Long customerId, String domainNamespace, int topN);
}
