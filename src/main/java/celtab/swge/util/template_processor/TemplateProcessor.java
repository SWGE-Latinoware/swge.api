package celtab.swge.util.template_processor;

import celtab.swge.model.enums.TemplateProcessorItemType;
import celtab.swge.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public abstract class TemplateProcessor implements UUIDUtils {

    public static final String RANDOM_URL = "{{random_url}}";

    public static final String TEMP_PASSWORD = "{{temp_password}}";

    public static final String FRONTEND_ADDRESS = "{{frontend_address}}";

    public static final String IMAGE_TYPE = "{{type}}";

    public static final String TRACK_NAME = "{{track.name}}";

    public static final String TRACK_INITIAL_DATE = "{{track.initialDate}}";

    public static final String TRACK_FINAL_DATE = "{{track.finalDate}}";

    public static final String USER_NAME = "{{user.name}}";

    public static final String EDITION_NAME = "{{edition.name}}";

    public static final String EDITION_YEAR = "{{edition.year}}";

    public static final String EDITION_LOCATION = "{{edition.institution}}";

    public static final String EDITION_LOGO = "{{edition.logo}}";

    public static final String EDITION_LOGO_WHITE = "{{edition.logoWhite}}";

    public static final String LOGO_WIDTH = "{{logo.width}}";

    public static final String LOGO_WHITE_WIDTH = "{{logo_white.width}}";

    public static final String ACTIVITY_NAME = "{{activity.name}}";

    public static final String ACTIVITY_WORKLOAD = "{{activity.workload}}";
    public static final Map<TemplateProcessorItemType, String> correspondingRegex = Map.ofEntries(
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.RANDOM_URL, RANDOM_URL),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.TEMP_PASSWORD, TEMP_PASSWORD),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.FRONTEND_ADDRESS, FRONTEND_ADDRESS),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.IMAGE_TYPE, IMAGE_TYPE),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.USER_NAME, USER_NAME),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.EDITION_NAME, EDITION_NAME),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.EDITION_YEAR, EDITION_YEAR),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.EDITION_LOCATION, EDITION_LOCATION),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.EDITION_LOGO, EDITION_LOGO),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.EDITION_LOGO_WHITE, EDITION_LOGO_WHITE),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.TRACK_NAME, TRACK_NAME),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.TRACK_INITIAL_DATE, TRACK_INITIAL_DATE),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.TRACK_FINAL_DATE, TRACK_FINAL_DATE),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.ACTIVITY_NAME, ACTIVITY_NAME),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.ACTIVITY_WORKLOAD, ACTIVITY_WORKLOAD),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.LOGO_WIDTH, LOGO_WIDTH),
        new AbstractMap.SimpleEntry<>(TemplateProcessorItemType.LOGO_WHITE_WIDTH, LOGO_WHITE_WIDTH)
    );
    private final List<TemplateProcessorItemType> allowedProcessingTypes;
    @Value("${url.frontend.address}")
    private String appAddress;

    protected TemplateProcessor(List<TemplateProcessorItemType> allowedProcessingTypes) {
        this.allowedProcessingTypes = allowedProcessingTypes;
    }

    private boolean isMatch(String template, String regex) {
        return template.contains(regex);
    }

    public String processTemplate(String template) {
        var auxTemplate = template;
        for (var itemType : allowedProcessingTypes) {
            var regex = correspondingRegex.getOrDefault(itemType, null);
            if (regex == null) continue;
            if (isMatch(auxTemplate, regex)) {
                String content;
                switch (itemType) {
                    case RANDOM_URL:
                        content = generateRandomUrl();
                        break;
                    case FRONTEND_ADDRESS:
                        content = getAppAddress();
                        break;
                    case TEMP_PASSWORD:
                        content = getTempPassword();
                        break;
                    case TRACK_NAME:
                    case TRACK_INITIAL_DATE:
                    case TRACK_FINAL_DATE:
                        content = getTrack(itemType);
                        break;
                    case IMAGE_TYPE:
                    case USER_NAME:
                        content = getUser(itemType);
                        break;
                    case EDITION_NAME:
                    case EDITION_YEAR:
                    case EDITION_LOCATION:
                    case EDITION_LOGO:
                    case EDITION_LOGO_WHITE:
                        content = getEdition(itemType);
                        break;
                    case ACTIVITY_NAME:
                    case ACTIVITY_WORKLOAD:
                        content = getActivity(itemType);
                        break;
                    case LOGO_WIDTH:
                    case LOGO_WHITE_WIDTH:
                        content = getWidth(itemType);
                        break;
                    default:
                        content = null;
                }
                if (content != null) {
                    auxTemplate = auxTemplate.replace(regex, content);
                }
            }
        }
        return auxTemplate;
    }

    protected abstract String getEdition(TemplateProcessorItemType itemType);

    protected abstract String getUser(TemplateProcessorItemType itemType);

    protected abstract String getTrack(TemplateProcessorItemType itemType);

    protected abstract String getActivity(TemplateProcessorItemType itemType);

    protected abstract String getWidth(TemplateProcessorItemType itemType);

    protected abstract String generateRandomUrl();

    protected String getAppAddress() {
        return appAddress.endsWith("/") ? appAddress : appAddress + "/";
    }

    protected String getUUIDFragment() {
        return getRandomUUIDString();
    }

    protected String getTempPassword() {
        return getUUIDFragment();
    }

}
