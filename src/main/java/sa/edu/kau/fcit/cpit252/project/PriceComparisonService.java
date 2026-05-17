package sa.edu.kau.fcit.cpit252.project;

import java.util.*;

public class PriceComparisonService {
    private PriceComparisonStrategy strategy;

    public PriceComparisonService(PriceComparisonStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null.");
        }
        this.strategy = strategy;
    }

    public void setStrategy(PriceComparisonStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null.");
        }
        this.strategy = strategy;
    }

    public List<ProductOffer> compareOffers(List<ProductOffer> offers) {
        if (offers == null) {
            return new ArrayList<>();
        }
        return strategy.sortOffers(offers);
    }

    public Map<String, List<ProductOffer>> compareOffersByProduct(List<ProductOffer> offers) {
        Map<String, List<ProductOffer>> groupedOffers = new LinkedHashMap<>();

        if (offers == null || offers.isEmpty()) {
            return groupedOffers;
        }

        for (ProductOffer offer : offers) {
            if (offer == null) {
                continue;
            }

            String productKey = buildProductKey(offer);

            if (!groupedOffers.containsKey(productKey)) {
                groupedOffers.put(productKey, new ArrayList<>());
            }

            groupedOffers.get(productKey).add(offer);
        }

        for (String productKey : groupedOffers.keySet()) {
            List<ProductOffer> sortedOffers = strategy.sortOffers(groupedOffers.get(productKey));
            groupedOffers.put(productKey, sortedOffers);
        }

        return groupedOffers;
    }

    private String buildProductKey(ProductOffer offer) {
        return offer.getProductName() + " (" + offer.getVariant() + ")";
    }
}
