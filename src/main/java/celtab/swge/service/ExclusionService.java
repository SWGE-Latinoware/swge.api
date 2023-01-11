package celtab.swge.service;

import celtab.swge.dto.UserRequestDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.enums.RequestType;
import celtab.swge.model.user.Exclusion;
import celtab.swge.model.user.User;
import celtab.swge.repository.ExclusionRepository;
import celtab.swge.specification.GenericSpecification;
import celtab.swge.util.ClassPathUtils;
import celtab.swge.util.email.EmailOptions;
import celtab.swge.util.email.EmailSender;
import celtab.swge.util.template_processor.EmailTemplateProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class ExclusionService extends GenericService<Exclusion, Long> implements ClassPathUtils {

    private final ExclusionRepository exclusionRepository;

    private final EmailSender emailSender;

    private final EmailTemplateProcessor emailTemplateProcessor;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${spring.lgpd.mail}")
    private String lgpdMail;

    public ExclusionService(ExclusionRepository exclusionRepository, EmailSender emailSender, EmailTemplateProcessor emailTemplateProcessor) {
        super(exclusionRepository, "exclusion(s)", new GenericSpecification<>(Exclusion.class));
        this.exclusionRepository = exclusionRepository;
        this.emailSender = emailSender;
        this.emailTemplateProcessor = emailTemplateProcessor;
    }

    public void sendUserExclusionCreatedMail(UserRequestDTO user, Boolean isDPO, RequestType requestType) throws ControllerException {
        try {
            var options = new EmailOptions();
            options.setFrom(emailFrom);
            options.setTo(Boolean.TRUE.equals(isDPO) ? lgpdMail : user.getEmail());
            options.setSubject("Solicitação de Exclusão");
            options.setIsTextHTML(true);
            var html = getContentFromClassPath("mail/exclusionRequestCreatedInactivity.html");
            switch (requestType) {
                case INACTIVITY:
                    html = getContentFromClassPath("mail/exclusionRequestCreatedInactivity.html");
                    break;
                case USER:
                    html = getContentFromClassPath("mail/exclusionRequestCreated.html");
                    break;
                case ANONYMOUS:
                    html = getContentFromClassPath("mail/exclusionRequestCreatedDPO.html");
                    break;
                case TUTORED:
                    return;
            }

            var userTo = new User();
            userTo.setEmail(Boolean.TRUE.equals(isDPO) ? lgpdMail : user.getEmail());
            emailTemplateProcessor.setTo(userTo);
            emailTemplateProcessor.setEdition(null);

            options.setText(emailTemplateProcessor.processTemplate(html));
            emailSender.sendEmail(options);
        } catch (Exception e) {
            throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<Exclusion> findAllByUserId(Long id) {
        return exclusionRepository.findAllByUserId(id);
    }
}
