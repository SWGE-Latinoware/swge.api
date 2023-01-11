package celtab.swge.service;


import celtab.swge.model.Promotion;
import celtab.swge.repository.PromotionRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class PromotionService extends GenericService<Promotion, Long> {

    public PromotionService(PromotionRepository promotionRepository) {
        super(promotionRepository, "promotion(s)", new GenericSpecification<>(Promotion.class));
    }
}
