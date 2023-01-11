package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.registration.individual_registration.IndividualRegistrationSchedule;
import celtab.swge.repository.IndividualRegistrationScheduleRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndividualRegistrationScheduleService extends GenericService<IndividualRegistrationSchedule, Long> {

    private final IndividualRegistrationScheduleRepository individualRegistrationScheduleRepository;

    public IndividualRegistrationScheduleService(IndividualRegistrationScheduleRepository individualRegistrationScheduleRepository) {
        super(individualRegistrationScheduleRepository, "individual registration schedule(s)", new GenericSpecification<>(IndividualRegistrationSchedule.class));
        this.individualRegistrationScheduleRepository = individualRegistrationScheduleRepository;
    }

    @Transactional
    public void deleteAllByActivityAndNotInScheduleList(Long activityId, List<Long> scheduleIds) {
        try {
            individualRegistrationScheduleRepository.deleteAllByScheduleActivityIdEqualsAndScheduleIdIsNotIn(activityId, scheduleIds);
        } catch (RuntimeException e) {
            throw new ServiceException("It was not possible to delete the " + customModelNameMessage + "!");
        }
    }

}
