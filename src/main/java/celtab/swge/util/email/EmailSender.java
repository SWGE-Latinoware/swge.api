package celtab.swge.util.email;

import celtab.swge.exception.ServiceException;
import celtab.swge.util.EnvironmentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailSender {

    private final JavaMailSender javaMailSender;

    private final EnvironmentInfo environmentInfo;

    public void sendEmail(EmailOptions options) throws ServiceException {
        try {
            if (environmentInfo.isTest()) return;
            var message = javaMailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, Boolean.TRUE, "UTF-8");
            helper.setFrom(options.getFrom());
            helper.setTo(options.getTo().toArray(String[]::new));
            helper.setText(options.getText(), options.getIsTextHTML());
            helper.setSubject(options.getSubject());
            if (options.getAttachments() != null && !options.getAttachments().isEmpty()) {
                for (var attachment : options.getAttachments()) {
                    helper.addAttachment(attachment.getName(), attachment.getResource());
                }
            }
            if (Boolean.TRUE.equals(options.getIsTextHTML()) && options.getInlineResources() != null && !options.getInlineResources().isEmpty()) {
                for (var inlineResource : options.getInlineResources()) {
                    helper.addInline(inlineResource.getContentId(), inlineResource.getResource());
                }
            }
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

}
