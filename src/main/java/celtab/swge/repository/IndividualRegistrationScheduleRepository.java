package celtab.swge.repository;

import celtab.swge.model.registration.individual_registration.IndividualRegistrationSchedule;

import java.util.List;

public interface IndividualRegistrationScheduleRepository extends GenericRepository<IndividualRegistrationSchedule, Long> {

    void deleteAllByScheduleActivityIdEqualsAndScheduleIdIsNotIn(Long activityId, List<Long> scheduleIds);

}
