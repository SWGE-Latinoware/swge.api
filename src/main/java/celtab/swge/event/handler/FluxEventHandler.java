package celtab.swge.event.handler;

import celtab.swge.model.*;
import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.registration.Registration;
import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.registration.individual_registration.TutoredIndividualRegistration;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.model.user.User;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.Instant;

public class FluxEventHandler {

    @PostPersist
    @PostUpdate
    @PostRemove
    public void updateEntity(BasicModel<Long> entity) {
        var klass = entity.getClass();
        if (klass.isAssignableFrom(User.class) || klass.isAssignableFrom(UserPermission.class)) {
            EntityUpdateHistory.getInstance().setUsersUpdatedDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(TutoredUser.class)) {
            EntityUpdateHistory.getInstance().setTutoredUsersUpdatedDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Institution.class) || klass.isAssignableFrom(Space.class)) {
            EntityUpdateHistory.getInstance().setInstitutionsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Caravan.class) || klass.isAssignableFrom(Notice.class)) {
            EntityUpdateHistory.getInstance().setCaravansUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Edition.class) ||
            klass.isAssignableFrom(RegistrationType.class) ||
            klass.isAssignableFrom(Promotion.class)
        ) {
            EntityUpdateHistory.getInstance().setEditionsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Track.class)) {
            EntityUpdateHistory.getInstance().setTracksUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Activity.class) ||
            klass.isAssignableFrom(SpeakerActivity.class) ||
            klass.isAssignableFrom(Schedule.class)
        ) {
            EntityUpdateHistory.getInstance().setActivitiesUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Certificate.class) || klass.isAssignableFrom(DynamicContent.class)) {
            EntityUpdateHistory.getInstance().setCertificatesUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(CaravanEnrollment.class)) {
            EntityUpdateHistory.getInstance().setCaravanEnrollmentsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(CaravanTutoredEnrollment.class)) {
            EntityUpdateHistory.getInstance().setCaravanTutoredEnrollmentsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Registration.class) || klass.isAssignableFrom(IndividualRegistration.class)) {
            EntityUpdateHistory.getInstance().setRegistrationsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(TutoredRegistration.class) || klass.isAssignableFrom(TutoredIndividualRegistration.class)) {
            EntityUpdateHistory.getInstance().setTutoredRegistrationsUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Theme.class)) {
            EntityUpdateHistory.getInstance().setThemesUpdateDate(Instant.now());
            return;
        }
        if (klass.isAssignableFrom(Feedback.class)) {
            EntityUpdateHistory.getInstance().setFeedbacksUpdateDate(Instant.now());
        }
        if (klass.isAssignableFrom(Voucher.class)) {
            EntityUpdateHistory.getInstance().setVouchersUpdateDate(Instant.now());
        }
        if (klass.isAssignableFrom(Exclusion.class)) {
            EntityUpdateHistory.getInstance().setExclusionsUpdateDate(Instant.now());
        }
        if (klass.isAssignableFrom(EditionHome.class)) {
            EntityUpdateHistory.getInstance().setEditionHomesUpdateDate(Instant.now());
        }
    }

}
