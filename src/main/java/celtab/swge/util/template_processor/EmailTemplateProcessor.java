package celtab.swge.util.template_processor;

import celtab.swge.exception.ControllerException;
import celtab.swge.model.Edition;
import celtab.swge.model.URL;
import celtab.swge.model.enums.TemplateProcessorItemType;
import celtab.swge.model.enums.URLType;
import celtab.swge.model.user.User;
import celtab.swge.service.URLService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class EmailTemplateProcessor extends TemplateProcessor {

    private final URLService urlService;

    private User to;

    private String tempPassword;

    private Edition edition;

    public EmailTemplateProcessor(URLService urlService) {
        super(List.of(
            TemplateProcessorItemType.RANDOM_URL,
            TemplateProcessorItemType.FRONTEND_ADDRESS,
            TemplateProcessorItemType.TEMP_PASSWORD,
            TemplateProcessorItemType.IMAGE_TYPE,
            TemplateProcessorItemType.EDITION_NAME,
            TemplateProcessorItemType.EDITION_YEAR,
            TemplateProcessorItemType.EDITION_LOGO,
            TemplateProcessorItemType.EDITION_LOGO_WHITE,
            TemplateProcessorItemType.LOGO_WIDTH,
            TemplateProcessorItemType.LOGO_WHITE_WIDTH
        ));
        this.urlService = urlService;
    }

    private String checkImageType() {
        return to.getEmail().endsWith("@gmail.com") ? "png" : "svg";
    }

    @Override
    protected String getEdition(TemplateProcessorItemType itemType) {
        try {
            switch (itemType) {
                case EDITION_NAME:
                    return edition.getName();
                case EDITION_YEAR:
                    return edition.getYear().toString();
                case EDITION_LOCATION:
                    return edition.getInstitution().getName();
                case EDITION_LOGO:
                    if (edition == null || edition.getLogo() == null)
                        return String.format("api/files/images/%s/logo", checkImageType());
                    return "api/files/images/" + edition.getLogo().getId();
                case EDITION_LOGO_WHITE:
                    if (edition == null || edition.getLogo() == null)
                        return String.format("api/files/images/%s/logoWhite", checkImageType());
                    return "api/files/images/" + edition.getLogo().getId();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getUser(TemplateProcessorItemType itemType) {
        if (to == null) throw new ControllerException(HttpStatus.BAD_REQUEST, "No User selected for the email");
        return checkImageType();
    }

    @Override
    protected String getTrack(TemplateProcessorItemType itemType) {
        return null;
    }

    @Override
    protected String getActivity(TemplateProcessorItemType itemType) {
        return null;
    }

    @Override
    protected String generateRandomUrl() {
        try {
            var url = new URL();
            url.setType(URLType.EMAIL_CONFIRMATION);
            url.setUser(to);
            url.setUrlFragment(getUUIDFragment());
            url.setEmail(to.getEmail());
            urlService.save(url);
            return getAppAddress() + "email-confirmation/" + url.getUrlFragment();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getTempPassword() {
        return tempPassword;
    }

    protected String getWidth(TemplateProcessorItemType itemType) {
        try {
            switch (itemType) {
                case LOGO_WIDTH:
                    if (edition == null || edition.getLogo() == null) return "40%";
                    return "90%";
                case LOGO_WHITE_WIDTH:
                    if (edition == null || edition.getLogo() == null) return "10%";
                    return "20%";
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
