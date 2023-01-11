package celtab.swge.service;

import celtab.swge.model.Feedback;
import celtab.swge.repository.FeedbackRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService extends GenericService<Feedback, Long> {

    public FeedbackService(FeedbackRepository feedbackRepository) {
        super(feedbackRepository, "feedback(s)", new GenericSpecification<>(Feedback.class));
    }

}
