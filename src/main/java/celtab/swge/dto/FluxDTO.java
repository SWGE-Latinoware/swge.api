package celtab.swge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FluxDTO {

    private Long usersUpdateDate;

    private Long tutoredUsersUpdateDate;

    private Long institutionsUpdateDate;

    private Long caravansUpdateDate;

    private Long editionsUpdateDate;

    private Long tracksUpdateDate;

    private Long activitiesUpdateDate;

    private Long certificatesUpdateDate;

    private Long caravanEnrollmentsUpdateDate;

    private Long caravanTutoredEnrollmentsUpdateDate;

    private Long registrationsUpdateDate;

    private Long tutoredRegistrationsUpdateDate;

    private Long themesUpdateDate;

    private Long feedbacksUpdateDate;

    private Long vouchersUpdateDate;

    private Long exclusionsUpdateDate;

    private Long editionHomesUpdateDate;
}
