package celtab.swge.event.handler;

import celtab.swge.dto.*;
import celtab.swge.model.*;
import celtab.swge.model.enrollment.CaravanEnrollment;
import celtab.swge.model.enrollment.CaravanTutoredEnrollment;
import celtab.swge.model.enums.AuditAction;
import celtab.swge.model.registration.Registration;
import celtab.swge.model.registration.TutoredRegistration;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.registration.individual_registration.IndividualRegistrationSchedule;
import celtab.swge.model.registration.individual_registration.TutoredIndividualRegistration;
import celtab.swge.model.user.DeleteRequest;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.TutoredUser;
import celtab.swge.model.user.User;
import celtab.swge.repository.AuditRepository;
import celtab.swge.util.EnvironmentInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Component
public class AuditListener {

    private static AuditRepository auditRepository;
    private static EnvironmentInfo environmentInfo;
    private final ModelMapper modelMapper;

    public AuditListener() {
        var modelMap = new ModelMapper();
        modelMap.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper = modelMap;
    }

    @Autowired
    public void setAuditRepository(AuditRepository auditRepository) {
        AuditListener.auditRepository = auditRepository;
    }

    @Autowired
    public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
        AuditListener.environmentInfo = environmentInfo;
    }

    public Class<? extends GenericDTO<Long>> getResponseDTO(Object object) {
        var klass = object.getClass();
        if (klass.isAssignableFrom(Activity.class)) {
            return ActivityResponseDTO.class;
        }
        if (klass.isAssignableFrom(Caravan.class)) {
            return CaravanResponseDTO.class;
        }
        if (klass.isAssignableFrom(CaravanEnrollment.class)) {
            return CaravanEnrollmentResponseDTO.class;
        }
        if (klass.isAssignableFrom(CaravanTutoredEnrollment.class)) {
            return CaravanTutoredEnrollmentResponseDTO.class;
        }
        if (klass.isAssignableFrom(Certificate.class)) {
            return CertificateResponseDTO.class;
        }
        if (klass.isAssignableFrom(DeleteRequest.class)) {
            return DeleteRequestResponseDTO.class;
        }
        if (klass.isAssignableFrom(DynamicContent.class)) {
            return DynamicContentResponseDTO.class;
        }
        if (klass.isAssignableFrom(Edition.class)) {
            return EditionResponseDTO.class;
        }
        if (klass.isAssignableFrom(EditionHome.class)) {
            return EditionHomeResponseDTO.class;
        }
        if (klass.isAssignableFrom(Exclusion.class)) {
            return ExclusionResponseDTO.class;
        }
        if (klass.isAssignableFrom(Feedback.class)) {
            return FeedbackResponseDTO.class;
        }
        if (klass.isAssignableFrom(File.class)) {
            return FileResponseDTO.class;
        }
        if (klass.isAssignableFrom(IndividualRegistration.class)) {
            return IndividualRegistrationResponseDTO.class;
        }
        if (klass.isAssignableFrom(IndividualRegistrationSchedule.class)) {
            return IndividualRegistrationScheduleResponseDTO.class;
        }
        if (klass.isAssignableFrom(Institution.class)) {
            return InstitutionResponseDTO.class;
        }
        if (klass.isAssignableFrom(Notice.class)) {
            return NoticeResponseDTO.class;
        }
        if (klass.isAssignableFrom(Promotion.class)) {
            return PromotionResponseDTO.class;
        }
        if (klass.isAssignableFrom(Registration.class)) {
            return RegistrationResponseDTO.class;
        }
        if (klass.isAssignableFrom(RegistrationType.class)) {
            return RegistrationTypeResponseDTO.class;
        }
        if (klass.isAssignableFrom(Schedule.class)) {
            return ScheduleResponseDTO.class;
        }
        if (klass.isAssignableFrom(Space.class)) {
            return SpaceResponseDTO.class;
        }
        if (klass.isAssignableFrom(SpeakerActivity.class)) {
            return SpeakerActivitySimpleResponseDTO.class;
        }
        if (klass.isAssignableFrom(Theme.class)) {
            return ThemeResponseDTO.class;
        }
        if (klass.isAssignableFrom(Track.class)) {
            return TrackResponseDTO.class;
        }
        if (klass.isAssignableFrom(TutoredIndividualRegistration.class)) {
            return TutoredIndividualRegistrationResponseDTO.class;
        }
        if (klass.isAssignableFrom(TutoredRegistration.class)) {
            return TutoredRegistrationResponseDTO.class;
        }
        if (klass.isAssignableFrom(TutoredUser.class)) {
            return TutoredUserResponseDTO.class;
        }
        if (klass.isAssignableFrom(URL.class)) {
            return URLResponseDTO.class;
        }
        if (klass.isAssignableFrom(UserPermission.class)) {
            return UserPermissionResponseDTO.class;
        }
        if (klass.isAssignableFrom(User.class)) {
            return UserSimpleResponseDTO.class;
        }
        if (klass.isAssignableFrom(Voucher.class)) {
            return VoucherResponseDTO.class;
        }
        return null;
    }

    public String getTableName(Object object) {

        var klass = object.getClass();
        if (klass.isAssignableFrom(User.class)) {
            return "users";
        }
        return klass.getSimpleName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    @PostPersist
    public void auditCreate(Object object) {
        var klass = getResponseDTO(object);
        if (klass != null) {
            addAudit(object, klass, AuditAction.CREATE);
        }
    }

    @PostUpdate
    public void auditUpdate(Object object) {
        var klass = getResponseDTO(object);
        if (klass != null) {
            addAudit(object, klass, AuditAction.UPDATE);
        }
    }

    @PostRemove
    public void auditDelete(Object object) {
        var klass = getResponseDTO(object);
        if (klass != null) {
            addAudit(object, klass, AuditAction.DELETE);
        }
    }

    public void addAudit(Object object, Class<? extends GenericDTO<Long>> klass, AuditAction action) {
        if (environmentInfo.isTest()) return;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = authentication == null ? "0" : authentication.getName();

        var map = modelMapper.map(object, klass);

        var audit = new Audit();
        audit.setId(null);
        audit.setAction(action);
        audit.setNewData(map.toString().replaceAll("(\\w+ResponseDTO)", ""));
        audit.setDataId(map.getId());
        audit.setTableName(getTableName(object));
        audit.setResponsibleUser(user);

        auditRepository.save(audit);
    }
}
