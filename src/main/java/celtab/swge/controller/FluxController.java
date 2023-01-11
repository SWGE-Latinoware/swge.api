package celtab.swge.controller;

import celtab.swge.dto.FluxDTO;
import celtab.swge.event.handler.EntityUpdateHistory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@CrossOrigin("*")
@RestController
@RequestMapping("/flux")
public class FluxController {

    @GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<FluxDTO> fluxAll() {
        var manager = EntityUpdateHistory.getInstance();
        return Flux
            .interval(Duration.ofSeconds(1))
            .map(i -> new FluxDTO(
                manager.getUsersUpdatedDate().getEpochSecond(),
                manager.getTutoredUsersUpdatedDate().getEpochSecond(),
                manager.getInstitutionsUpdateDate().getEpochSecond(),
                manager.getCaravansUpdateDate().getEpochSecond(),
                manager.getEditionsUpdateDate().getEpochSecond(),
                manager.getTracksUpdateDate().getEpochSecond(),
                manager.getActivitiesUpdateDate().getEpochSecond(),
                manager.getCertificatesUpdateDate().getEpochSecond(),
                manager.getCaravanEnrollmentsUpdateDate().getEpochSecond(),
                manager.getCaravanTutoredEnrollmentsUpdateDate().getEpochSecond(),
                manager.getRegistrationsUpdateDate().getEpochSecond(),
                manager.getTutoredRegistrationsUpdateDate().getEpochSecond(),
                manager.getThemesUpdateDate().getEpochSecond(),
                manager.getFeedbacksUpdateDate().getEpochSecond(),
                manager.getVouchersUpdateDate().getEpochSecond(),
                manager.getExclusionsUpdateDate().getEpochSecond(),
                manager.getEditionHomesUpdateDate().getEpochSecond()
            ));
    }
}
