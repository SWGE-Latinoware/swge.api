package celtab.swge.event.handler;

import lombok.Getter;

import java.time.Instant;

@Getter
public class EntityUpdateHistory {

    private static EntityUpdateHistory instance;

    public static EntityUpdateHistory getInstance() {
        if (instance == null) {
            instance = new EntityUpdateHistory();
        }
        return instance;
    }

    private Instant usersUpdatedDate;

    private Instant tutoredUsersUpdatedDate;

    private Instant institutionsUpdateDate;

    private Instant caravansUpdateDate;

    private Instant editionsUpdateDate;

    private Instant tracksUpdateDate;

    private Instant activitiesUpdateDate;

    private Instant certificatesUpdateDate;

    private Instant caravanEnrollmentsUpdateDate;

    private Instant caravanTutoredEnrollmentsUpdateDate;

    private Instant registrationsUpdateDate;

    private Instant tutoredRegistrationsUpdateDate;

    private Instant themesUpdateDate;

    private Instant feedbacksUpdateDate;

    private Instant vouchersUpdateDate;

    private Instant exclusionsUpdateDate;

    private Instant editionHomesUpdateDate;

    private EntityUpdateHistory() {
        usersUpdatedDate = Instant.now();
        tutoredUsersUpdatedDate = Instant.now();
        institutionsUpdateDate = Instant.now();
        caravansUpdateDate = Instant.now();
        editionsUpdateDate = Instant.now();
        tracksUpdateDate = Instant.now();
        activitiesUpdateDate = Instant.now();
        certificatesUpdateDate = Instant.now();
        caravanEnrollmentsUpdateDate = Instant.now();
        caravanTutoredEnrollmentsUpdateDate = Instant.now();
        registrationsUpdateDate = Instant.now();
        tutoredRegistrationsUpdateDate = Instant.now();
        themesUpdateDate = Instant.now();
        feedbacksUpdateDate = Instant.now();
        vouchersUpdateDate = Instant.now();
        exclusionsUpdateDate = Instant.now();
        editionHomesUpdateDate = Instant.now();
    }

    public synchronized void setUsersUpdatedDate(Instant usersUpdatedDate) {
        if (usersUpdatedDate.isAfter(this.usersUpdatedDate)) {
            this.usersUpdatedDate = usersUpdatedDate;
        }
    }

    public synchronized void setTutoredUsersUpdatedDate(Instant tutoredUsersUpdatedDate) {
        if (tutoredUsersUpdatedDate.isAfter(this.tutoredUsersUpdatedDate)) {
            this.tutoredUsersUpdatedDate = tutoredUsersUpdatedDate;
        }
    }

    public synchronized void setInstitutionsUpdateDate(Instant institutionsUpdateDate) {
        if (institutionsUpdateDate.isAfter(this.institutionsUpdateDate)) {
            this.institutionsUpdateDate = institutionsUpdateDate;
        }
    }

    public synchronized void setCaravansUpdateDate(Instant caravansUpdateDate) {
        if (caravansUpdateDate.isAfter(this.caravansUpdateDate)) {
            this.caravansUpdateDate = caravansUpdateDate;
        }
    }

    public synchronized void setEditionsUpdateDate(Instant editionsUpdateDate) {
        if (editionsUpdateDate.isAfter(this.editionsUpdateDate)) {
            this.editionsUpdateDate = editionsUpdateDate;
        }
    }

    public synchronized void setTracksUpdateDate(Instant tracksUpdateDate) {
        if (tracksUpdateDate.isAfter(this.tracksUpdateDate)) {
            this.tracksUpdateDate = tracksUpdateDate;
        }
    }

    public synchronized void setActivitiesUpdateDate(Instant activitiesUpdateDate) {
        if (activitiesUpdateDate.isAfter(this.activitiesUpdateDate)) {
            this.activitiesUpdateDate = activitiesUpdateDate;
        }
    }

    public synchronized void setCertificatesUpdateDate(Instant certificatesUpdateDate) {
        if (certificatesUpdateDate.isAfter(this.certificatesUpdateDate)) {
            this.certificatesUpdateDate = certificatesUpdateDate;
        }
    }

    public synchronized void setCaravanEnrollmentsUpdateDate(Instant caravanEnrollmentsUpdateDate) {
        if (caravanEnrollmentsUpdateDate.isAfter(this.caravanEnrollmentsUpdateDate)) {
            this.caravanEnrollmentsUpdateDate = caravanEnrollmentsUpdateDate;
        }
    }

    public synchronized void setCaravanTutoredEnrollmentsUpdateDate(Instant caravanTutoredEnrollmentsUpdateDate) {
        if (caravanTutoredEnrollmentsUpdateDate.isAfter(this.caravanTutoredEnrollmentsUpdateDate)) {
            this.caravanTutoredEnrollmentsUpdateDate = caravanTutoredEnrollmentsUpdateDate;
        }
    }

    public synchronized void setRegistrationsUpdateDate(Instant registrationsUpdateDate) {
        if (registrationsUpdateDate.isAfter(this.registrationsUpdateDate)) {
            this.registrationsUpdateDate = registrationsUpdateDate;
        }
    }

    public synchronized void setTutoredRegistrationsUpdateDate(Instant tutoredRegistrationsUpdateDate) {
        if (tutoredRegistrationsUpdateDate.isAfter(this.tutoredRegistrationsUpdateDate)) {
            this.tutoredRegistrationsUpdateDate = tutoredRegistrationsUpdateDate;
        }
    }

    public synchronized void setThemesUpdateDate(Instant themesUpdateDate) {
        if (themesUpdateDate.isAfter(this.themesUpdateDate)) {
            this.themesUpdateDate = themesUpdateDate;
        }
    }

    public synchronized void setFeedbacksUpdateDate(Instant feedbacksUpdateDate) {
        if (feedbacksUpdateDate.isAfter(this.feedbacksUpdateDate)) {
            this.feedbacksUpdateDate = feedbacksUpdateDate;
        }
    }

    public synchronized void setVouchersUpdateDate(Instant vouchersUpdateDate) {
        if (vouchersUpdateDate.isAfter(this.vouchersUpdateDate)) {
            this.vouchersUpdateDate = vouchersUpdateDate;
        }
    }

    public synchronized void setExclusionsUpdateDate(Instant exclusionsUpdateDate) {
        if (exclusionsUpdateDate.isAfter(this.exclusionsUpdateDate)) {
            this.exclusionsUpdateDate = exclusionsUpdateDate;
        }
    }

    public synchronized void setEditionHomesUpdateDate(Instant editionHomesUpdateDate) {
        if (editionHomesUpdateDate.isAfter(this.editionHomesUpdateDate)) {
            this.editionHomesUpdateDate = editionHomesUpdateDate;
        }
    }
}
