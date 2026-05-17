package sa.edu.kau.fcit.cpit252.project;

import java.util.*;

public class HighestPriceStrategy implements PriceComparisonStrategy {
    @Override
    public List<ProductOffer> sortOffers(List<ProductOffer> offers) {
        if (offers == null || offers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductOffer> sortedOffers = new ArrayList<>(offers);
        Comparator<ProductOffer> highestPriceComparator = new Comparator<ProductOffer>() {
            @Override
            public int compare(ProductOffer offer1, ProductOffer offer2) {
                return Double.compare(offer2.getPrice(), offer1.getPrice());
            }
        };
        sortedOffers.sort(highestPriceComparator);

        return sortedOffers;
    }
}