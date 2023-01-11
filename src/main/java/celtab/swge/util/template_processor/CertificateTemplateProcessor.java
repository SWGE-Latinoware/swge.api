package celtab.swge.util.template_processor;

import celtab.swge.model.Activity;
import celtab.swge.model.Edition;
import celtab.swge.model.Track;
import celtab.swge.model.enums.TemplateProcessorItemType;
import celtab.swge.model.user.BasicUser;

import java.time.Instant;
import java.util.List;

public class CertificateTemplateProcessor extends TemplateProcessor {

    private final BasicUser user;

    private final Track track;

    private final Edition edition;

    private final Activity activity;

    public CertificateTemplateProcessor(BasicUser user, Track track, Edition edition, Activity activity) {
        super(List.of(
            TemplateProcessorItemType.USER_NAME,
            TemplateProcessorItemType.EDITION_NAME,
            TemplateProcessorItemType.TRACK_NAME,
            TemplateProcessorItemType.TRACK_FINAL_DATE,
            TemplateProcessorItemType.EDITION_YEAR,
            TemplateProcessorItemType.EDITION_LOCATION,
            TemplateProcessorItemType.TRACK_INITIAL_DATE,
            TemplateProcessorItemType.ACTIVITY_NAME,
            TemplateProcessorItemType.ACTIVITY_WORKLOAD
        ));
        this.user = user;
        this.track = track;
        this.edition = edition;
        this.activity = activity;
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
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getUser(TemplateProcessorItemType itemType) {
        try {
            if (itemType == TemplateProcessorItemType.USER_NAME) {
                return user.getName();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getTrack(TemplateProcessorItemType itemType) {
        try {
            switch (itemType) {
                case TRACK_NAME:
                    return track.getName();
                case TRACK_INITIAL_DATE:
                    return track.getInitialDate().toString();
                case TRACK_FINAL_DATE:
                    return Instant.now().toString();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getActivity(TemplateProcessorItemType itemType) {
        try {
            switch (itemType) {
                case ACTIVITY_NAME:
                    return activity.getName();
                case ACTIVITY_WORKLOAD:
                    return activity.getWorkload();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getWidth(TemplateProcessorItemType itemType) {
        return null;
    }

    @Override
    protected String generateRandomUrl() {
        return null;
    }
}
