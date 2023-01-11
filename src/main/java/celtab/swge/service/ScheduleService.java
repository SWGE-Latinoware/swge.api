package celtab.swge.service;

import celtab.swge.model.Schedule;
import celtab.swge.repository.ScheduleRepository;
import celtab.swge.specification.GenericSpecification;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService extends GenericService<Schedule, Long> {

    public ScheduleService(ScheduleRepository scheduleRepository) {
        super(scheduleRepository, "registration(s)", new GenericSpecification<>(Schedule.class));
    }
}
