package celtab.swge;

import celtab.swge.controller.*;
import celtab.swge.repository.*;
import celtab.swge.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionController institutionController;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ThemeController themeController;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private EditionService editionService;

    @Autowired
    private EditionController editionController;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private LocationController locationController;

    @Autowired
    private CaravanEnrollmentController caravanEnrollmentController;

    @Autowired
    private NoticeController noticeController;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CaravanController caravanController;

    @Autowired
    private CaravanRepository caravanRepository;

    @Autowired
    private CaravanService caravanService;

    @Autowired
    private CaravanTutoredEnrollmentService caravanTutoredEnrollmentService;

    @Autowired
    private CaravanTutoredEnrollmentController caravanTutoredEnrollmentController;

    @Autowired
    private CaravanTutoredEnrollmentRepository caravanTutoredEnrollmentRepository;

    @Autowired
    private FileController fileController;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TrackController trackController;

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserPermissionController userPermissionController;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityController activityController;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateController certificateController;

    @Autowired
    private RegistrationTypeController registrationTypeController;

    @Autowired
    private RegistrationTypeService registrationTypeService;

    @Autowired
    private RegistrationTypeRepository registrationTypeRepository;

    @Autowired
    private FeedbackController feedbackController;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SpeakerActivityService speakerActivityService;

    @Autowired
    private SpeakerActivityRepository speakerActivityRepository;

    @Autowired
    private InfoController infoController;

    @Autowired
    private URLController urlController;

    @Autowired
    private URLService urlService;

    @Autowired
    private URLRepository urlRepository;

    @Autowired
    private TutoredUserController tutoredUserController;

    @Autowired
    private TutoredUserService tutoredUserService;

    @Autowired
    private TutoredUserRepository tutoredUserRepository;

    @Autowired
    private IndividualRegistrationScheduleService individualRegistrationScheduleService;

    @Autowired
    private IndividualRegistrationScheduleRepository individualRegistrationScheduleRepository;

    @Autowired
    private PaymentController paymentController;

    @Test
    void contextLoads() {
        assertNotNull(userController, "User Controller Loading Error!");
        assertNotNull(userService, "User Service Loading Error!");
        assertNotNull(userRepository, "User Repository Loading Error!");
        assertNotNull(institutionController, "Institution Controller Loading Error!");
        assertNotNull(institutionService, "Institution Service Loading Error!");
        assertNotNull(institutionRepository, "Institution Repository Loading Error!");
        assertNotNull(themeService, "Theme Service Loading Error!");
        assertNotNull(themeRepository, "Theme Repository Loading Error!");
        assertNotNull(themeController, "Theme Controller Loading Error!");
        assertNotNull(editionController, "Edition Controller Loading Error!");
        assertNotNull(editionService, "Edition Service Loading Error!");
        assertNotNull(editionRepository, "Edition Repository Loading Error!");
        assertNotNull(locationController, "Location Controller Loading Error!");
        assertNotNull(caravanEnrollmentController, "Caravan Enrollment Controller Loading Error!");
        assertNotNull(noticeController, "Notice Controller Loading Error!");
        assertNotNull(noticeRepository, "Notice Repository Loading Error!");
        assertNotNull(noticeService, "Notice Service Loading Error!");
        assertNotNull(caravanController, "Caravan Controller Loading Error!");
        assertNotNull(caravanRepository, "Caravan Repository Loading Error!");
        assertNotNull(caravanService, "Caravan Service Loading Error!");
        assertNotNull(caravanTutoredEnrollmentController, "Caravan Tutored Controller Loading Error!");
        assertNotNull(caravanTutoredEnrollmentRepository, "Caravan Tutored Repository Loading Error!");
        assertNotNull(caravanTutoredEnrollmentService, "Caravan Tutored Service Loading Error!");
        assertNotNull(fileController, "File Controller Loading Error!");
        assertNotNull(fileRepository, "File Repository Loading Error!");
        assertNotNull(fileService, "File Service Loading Error!");
        assertNotNull(trackService, "Track Service Loading Error!");
        assertNotNull(trackRepository, "Track Repository Loading Error!");
        assertNotNull(trackController, "Track Controller Loading Error!");
        assertNotNull(userPermissionService, "User Permission Service Loading Error!");
        assertNotNull(userPermissionRepository, "User Permission Repository Loading Error!");
        assertNotNull(userPermissionController, "User Permission Controller Loading Error!");
        assertNotNull(activityService, "Activity Service Loading Error!");
        assertNotNull(activityRepository, "Activity Repository Loading Error!");
        assertNotNull(activityController, "Activity Controller Loading Error!");
        assertNotNull(certificateService, "Certificate Service Loading Error!");
        assertNotNull(certificateRepository, "Certificate Repository Loading Error!");
        assertNotNull(certificateController, "Certificate Controller Loading Error!");
        assertNotNull(registrationTypeService, "Registration Type Service Loading Error!");
        assertNotNull(registrationTypeRepository, "Registration Type Repository Loading Error!");
        assertNotNull(registrationTypeController, "Registration Type Controller Loading Error!");
        assertNotNull(feedbackService, "Feedback Service Loading Error!");
        assertNotNull(feedbackRepository, "Feedback Repository Loading Error!");
        assertNotNull(feedbackController, "Feedback Controller Loading Error!");
        assertNotNull(speakerActivityRepository, "Speaker Activity Repository Loading Error!");
        assertNotNull(speakerActivityService, "Speaker Activity Service Loading Error!");
        assertNotNull(urlRepository, "Url Repository Loading Error!");
        assertNotNull(urlController, "Url Controller Loading Error!");
        assertNotNull(urlService, "Url Service Loading Error!");
        assertNotNull(infoController, "info Controller Loading Error!");
        assertNotNull(tutoredUserController, "Tutored User Controller Loading Error!");
        assertNotNull(tutoredUserService, "Tutored User Service Loading Error!");
        assertNotNull(tutoredUserRepository, "Tutored User Repository Loading Error!");
        assertNotNull(individualRegistrationScheduleService, "Individual Registration Schedule Service Loading Error!");
        assertNotNull(individualRegistrationScheduleRepository, "Individual Registration Schedule Repository Loading Error!");
        assertNotNull(paymentController, "Payment Controller Loading Error!");
    }

}
