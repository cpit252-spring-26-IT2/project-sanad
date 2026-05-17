package sa.edu.kau.fcit.cpit252.project;

import java.util.List;

public interface PriceComparisonStrategy {
    List<ProductOffer> sortOffers(List<ProductOffer> offers);
}
