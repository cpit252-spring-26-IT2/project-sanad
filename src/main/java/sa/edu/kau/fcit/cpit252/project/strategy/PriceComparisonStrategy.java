package sa.edu.kau.fcit.cpit252.project.strategy;

import java.util.List;

public interface PriceComparisonStrategy {
    List<ProductOffer> sortOffers(List<ProductOffer> offers);
}
