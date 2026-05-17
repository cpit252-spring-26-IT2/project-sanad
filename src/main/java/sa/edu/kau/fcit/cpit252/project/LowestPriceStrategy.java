package sa.edu.kau.fcit.cpit252.project;

import java.util.*;

public class LowestPriceStrategy implements PriceComparisonStrategy {
    @Override
    public List<ProductOffer> sortOffers(List<ProductOffer> offers) {
        if (offers == null || offers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductOffer> sortedOffers = new ArrayList<>(offers);
        Comparator<ProductOffer> lowestPriceComparator = new Comparator<ProductOffer>() {
            @Override
            public int compare(ProductOffer offer1, ProductOffer offer2) {
                return Double.compare(offer1.getPrice(), offer2.getPrice());
            }
        };
        sortedOffers.sort(lowestPriceComparator);

        return sortedOffers;
    }
}