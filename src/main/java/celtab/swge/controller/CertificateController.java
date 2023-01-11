package celtab.swge.controller;

import celtab.swge.dto.CertificateRequestDTO;
import celtab.swge.dto.CertificateResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Certificate;
import celtab.swge.model.Edition;
import celtab.swge.model.URL;
import celtab.swge.model.enums.URLType;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.service.*;
import celtab.swge.util.ClassPathUtils;
import celtab.swge.util.UUIDUtils;
import celtab.swge.util.template_processor.CertificateTemplateProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.*;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/certificates")
public class CertificateController extends GenericController<Certificate, Long, CertificateRequestDTO, CertificateResponseDTO> implements ClassPathUtils, UUIDUtils {

    private static final String UBUNTU = "Ubuntu";
    private static final String ROBOTO = "Roboto";
    private static final String POPPINS = "Poppins";
    private static final String PLAYFAIR = "Playfair Display SC";
    private static final String FANTASQUE = "Fantasque Sans";
    private static final String APPLE_STORM = "Apple Storm";
    private static final String TIMES_ROMAN = "Times-Roman";
    private static final String HELVETICA = "Helvetica";
    private static final String COURIER = "Courier";
    private static final String LATO = "Lato";
    private static final String STRONG_OPEN = "<strong>";
    private static final String STRONG_CLOSE = "</strong>";
    private static final String BREAKLINE = "<br>";
    private static final String ITALIC_OPEN = "<em>";
    private static final String ITALIC_CLOSE = "</em>";
    private static final String UNDERLINE_OPEN = "<u>";
    private static final String UNDERLINE_CLOSE = "</u>";
    private final CertificateService certificateService;
    private final FileStorageProperties fileStorageProperties;
    private final UserService userService;
    private final TrackService trackService;
    private final ActivityService activityService;
    private final TutoredUserService tutoredUserService;
    private final URLService urlService;
    private final Map<FontIdentifier, String> externalFonts = Map.ofEntries(
        new SimpleEntry<>(new FontIdentifier(UBUNTU, true, false), "Ubuntu-B.ttf"),
        new SimpleEntry<>(new FontIdentifier(UBUNTU, true, true), "Ubuntu-BI.ttf"),
        new SimpleEntry<>(new FontIdentifier(UBUNTU, false, false), "Ubuntu-R.ttf"),
        new SimpleEntry<>(new FontIdentifier(UBUNTU, false, true), "Ubuntu-RI.ttf"),
        new SimpleEntry<>(new FontIdentifier(ROBOTO, false, false), "Roboto-Regular.ttf"),
        new SimpleEntry<>(new FontIdentifier(ROBOTO, false, true), "Roboto-Italic.ttf"),
        new SimpleEntry<>(new FontIdentifier(ROBOTO, true, true), "Roboto-BoldItalic.ttf"),
        new SimpleEntry<>(new FontIdentifier(ROBOTO, true, false), "Roboto-Bold.ttf"),
        new SimpleEntry<>(new FontIdentifier(POPPINS, false, false), "Poppins-Regular.ttf"),
        new SimpleEntry<>(new FontIdentifier(POPPINS, false, true), "Poppins-Italic.ttf"),
        new SimpleEntry<>(new FontIdentifier(POPPINS, true, true), "Poppins-BoldItalic.ttf"),
        new SimpleEntry<>(new FontIdentifier(POPPINS, true, false), "Poppins-Bold.ttf"),
        new SimpleEntry<>(new FontIdentifier(PLAYFAIR, false, false), "PlayfairDisplaySC-Regular.ttf"),
        new SimpleEntry<>(new FontIdentifier(PLAYFAIR, false, true), "PlayfairDisplaySC-Italic.ttf"),
        new SimpleEntry<>(new FontIdentifier(PLAYFAIR, true, true), "PlayfairDisplaySC-BoldItalic.ttf"),
        new SimpleEntry<>(new FontIdentifier(PLAYFAIR, true, false), "PlayfairDisplaySC-Bold.ttf"),
        new SimpleEntry<>(new FontIdentifier(LATO, false, false), "Lato-Regular.ttf"),
        new SimpleEntry<>(new FontIdentifier(LATO, false, true), "Lato-Italic.ttf"),
        new SimpleEntry<>(new FontIdentifier(LATO, true, true), "Lato-BoldItalic.ttf"),
        new SimpleEntry<>(new FontIdentifier(LATO, true, false), "Lato-Bold.ttf"),
        new SimpleEntry<>(new FontIdentifier(FANTASQUE, false, false), "FantasqueSansMono-Regular.ttf"),
        new SimpleEntry<>(new FontIdentifier(FANTASQUE, false, true), "FantasqueSansMono-Italic.ttf"),
        new SimpleEntry<>(new FontIdentifier(FANTASQUE, true, true), "FantasqueSansMono-BoldItalic.ttf"),
        new SimpleEntry<>(new FontIdentifier(FANTASQUE, true, false), "FantasqueSansMono-Bold.ttf"),
        new SimpleEntry<>(new FontIdentifier(APPLE_STORM, false, false), "AppleStormRg.otf"),
        new SimpleEntry<>(new FontIdentifier(APPLE_STORM, false, true), "AppleStormIta.otf"),
        new SimpleEntry<>(new FontIdentifier(APPLE_STORM, true, true), "AppleStormCBoIta.otf"),
        new SimpleEntry<>(new FontIdentifier(APPLE_STORM, true, false), "AppleStormCBo.otf")
    );
    private final Map<FontIdentifier, String> internalFonts = Map.ofEntries(
        new SimpleEntry<>(new FontIdentifier(TIMES_ROMAN, false, false), TIMES_ROMAN),
        new SimpleEntry<>(new FontIdentifier(TIMES_ROMAN, false, true), "Times-Italic"),
        new SimpleEntry<>(new FontIdentifier(TIMES_ROMAN, true, true), "Times-BoldItalic"),
        new SimpleEntry<>(new FontIdentifier(TIMES_ROMAN, true, false), "Times-Bold"),
        new SimpleEntry<>(new FontIdentifier(HELVETICA, false, false), HELVETICA),
        new SimpleEntry<>(new FontIdentifier(HELVETICA, false, true), "Helvetica-Oblique"),
        new SimpleEntry<>(new FontIdentifier(HELVETICA, true, true), "Helvetica-BoldOblique"),
        new SimpleEntry<>(new FontIdentifier(HELVETICA, true, false), "Helvetica-Bold"),
        new SimpleEntry<>(new FontIdentifier(COURIER, false, false), COURIER),
        new SimpleEntry<>(new FontIdentifier(COURIER, false, true), "Courier-Oblique"),
        new SimpleEntry<>(new FontIdentifier(COURIER, true, true), "Courier-BoldOblique"),
        new SimpleEntry<>(new FontIdentifier(COURIER, true, false), "Courier-Bold")
    );
    @Value("${url.frontend.address}")
    private String appAddress;

    public CertificateController(CertificateService certificateService, ModelMapper modelMapper, ObjectMapper objectMapper, FileStorageProperties fileStorageProperties, UserService userService, TrackService trackService, ActivityService activityService, TutoredUserService tutoredUserService, URLService urlService) {
        super(certificateService, modelMapper, objectMapper);
        this.certificateService = certificateService;
        this.fileStorageProperties = fileStorageProperties;
        this.userService = userService;
        this.trackService = trackService;
        this.activityService = activityService;
        this.tutoredUserService = tutoredUserService;
        this.urlService = urlService;
    }

    public static byte[] getQRCode(String text, int width, int height) throws WriterException, IOException {
        var qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        var pngOutputStream = new ByteArrayOutputStream();
        var con = new MatrixToImageConfig();

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
        return pngOutputStream.toByteArray();
    }

    @Override
    @PostMapping
    @AdministratorFilter
    @Transactional
    public CertificateResponseDTO create(@RequestBody CertificateRequestDTO certificate) {
        try {
            certificate.getDynamicContents().forEach(dynamicContent -> dynamicContent.setCertificate(certificate));
            return super.create(certificate);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @AdministratorFilter
    @Transactional
    public CertificateResponseDTO update(@RequestBody CertificateRequestDTO certificate) {
        return super.update(certificate);
    }

    @Override
    @GetMapping("/{id}")
    public CertificateResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping("/filter")
    public Page<CertificateResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @GetMapping("/unique/edition/name")
    public Boolean verifyUniqueName(@RequestParam String name, @RequestParam Long editionId) {
        return certificateService.findByNameAndEdition(name, editionId) != null;
    }

    private String registerFont(FontIdentifier identifier) throws ControllerException {
        var externalFont = externalFonts.get(identifier);
        if (externalFont != null) {
            try {
                FontFactory.register(getResourceFromClassPath("fonts/" + externalFont).getPath(), externalFont);
                return externalFont;
            } catch (IOException e) {
                throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            var internalFont = internalFonts.get(identifier);
            if (internalFont == null) {
                throw new ControllerException(BAD_REQUEST, "Font Not Found!");
            }
            return internalFont;
        }
    }

    @GetMapping("/pdf/{certId}")
    @Transactional
    public ResponseEntity<byte[]> generatePDF(
        @PathVariable Long certId,
        @RequestParam(defaultValue = "false") Boolean isTutored,
        @RequestParam Long userId,
        @RequestParam(required = false) Long trackId,
        @RequestParam(required = false) Long activityId
    ) {
        var cert = service.findOne(certId);

        if (cert == null) throw new ControllerException(NOT_FOUND, "Certificate not found!");

        if (Instant.now().isBefore(cert.getAvailabilityDateTime().toInstant()))
            throw new ControllerException(BAD_REQUEST, "Certificate is not available!");

        var user = (Boolean.TRUE.equals(isTutored)) ?
            tutoredUserService.findOne(userId) :
            userService.findOne(userId);

        if (user == null) throw new ControllerException(NOT_FOUND, "User not found!");

        var track = trackService.findOne(trackId);

        var activity = activityService.findOne(activityId);

        if (track == null && activity == null)
            throw new ControllerException(NOT_FOUND, "Track and Activity not found!");

        var trackAttendeeEquals = false;
        if (track != null && track.getAttendeeCertificate() != null) {
            trackAttendeeEquals = track.getAttendeeCertificate().getId().equals(certId);
        }
        var trackSpeakerEquals = false;
        if (track != null && track.getSpeakerCertificate() != null) {
            trackSpeakerEquals = track.getSpeakerCertificate().getId().equals(certId);
        }
        var activityAttendeeEquals = false;
        if (activity != null && activity.getAttendeeCertificate() != null) {
            activityAttendeeEquals = activity.getAttendeeCertificate().getId().equals(certId);
        }
        var activitySpeakerEquals = false;
        if (activity != null && activity.getAttendeeCertificate() != null) {
            activitySpeakerEquals = activity.getSpeakerCertificate().getId().equals(certId);
        }

        if (!trackAttendeeEquals && !trackSpeakerEquals && !activityAttendeeEquals && !activitySpeakerEquals) {
            throw new ControllerException(BAD_REQUEST, "Certificate don't belong to selected track or activity");
        }

        Edition edition;
        if (track != null) {
            edition = track.getEdition();
        } else {
            edition = activity.getEdition();
        }

        if (track != null && track
            .getActivities()
            .stream()
            .noneMatch(act -> {
                if (Boolean.FALSE.equals(isTutored)) return act
                    .getIndividualRegistrations()
                    .stream()
                    .anyMatch(
                        individualRegistration -> Objects.equals(individualRegistration.getRegistration().getUser().getId(), userId)
                    );
                return act
                    .getTutoredIndividualRegistrations()
                    .stream()
                    .anyMatch(
                        individualRegistration -> Objects.equals(individualRegistration.getRegistration().getTutoredUser().getId(), userId)
                    );
            }) && track
            .getActivities()
            .stream()
            .noneMatch(act ->
                act
                    .getSpeakers()
                    .stream()
                    .anyMatch(speak ->
                        speak
                            .getSpeaker()
                            .getId()
                            .equals(userId))
            )) {
            throw new ControllerException(BAD_REQUEST, "User not registered in track!");
        }

        if (track != null && activity != null && track
            .getActivities()
            .stream()
            .noneMatch(act -> Objects.equals(activityId, act.getId()
            ))) {
            throw new ControllerException(BAD_REQUEST, "Activity not listed on track!");
        }

        if (activity != null && (
            (Boolean.FALSE.equals(isTutored) &&
                activity
                    .getIndividualRegistrations()
                    .stream()
                    .noneMatch(regs ->
                        Objects
                            .equals(regs.getRegistration().getUser().getId(), userId))) ||
                (Boolean.TRUE.equals(isTutored) && activity
                    .getTutoredIndividualRegistrations()
                    .stream()
                    .noneMatch(regs ->
                        Objects
                            .equals(regs.getRegistration().getTutoredUser().getId(), userId)))) &&
            activity
                .getSpeakers()
                .stream()
                .noneMatch(speak ->
                    speak
                        .getSpeaker()
                        .getId()
                        .equals(userId)
                )) {

            throw new ControllerException(BAD_REQUEST, "User is not registered on selected activity!");
        }

        var validationURL = new URL();
        validationURL.setType(URLType.CERTIFICATE_VALIDATION);
        validationURL.setUrlFragment(getRandomUUIDString());

        var certificateTemplateProcessor = new CertificateTemplateProcessor(user, track, edition, activity);

        var contents = cert.getDynamicContents();

        var pdf = new Document(PageSize.A4.rotate());
        var regexTextTags = "(?=<[\\w-/]+>)|(?<=<[\\w-/]>)|(?<=>)";

        ByteArrayOutputStream fOut;
        try {
            fOut = new ByteArrayOutputStream();
            var wr = PdfWriter.getInstance(pdf, fOut);

            pdf.open();

            var pcb = wr.getDirectContent();

            var fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

            var path = fileStorageLocation.resolve(cert.getBackgroundImage().getId().toString()).normalize();

            var backgroundImg = Image.getInstance(path.toUri().toURL());
            backgroundImg.setAbsolutePosition(0, 0);

            wr.getDirectContentUnder().addImage(backgroundImg, pdf.getPageSize().getWidth(), 0, 0, pdf.getPageSize().getHeight(), 0, 0);

            var validationLink = appAddress + (appAddress.endsWith("/") ? "validation/" : "/validation/") + validationURL.getUrlFragment();

            if (Boolean.TRUE.equals(cert.getAllowQrCode())) {
                var qrCode = Image.getInstance(getQRCode(validationLink, 500, 500));
                qrCode.setAbsolutePosition(0, 0);

                wr.getDirectContentUnder().addImage(qrCode, 100, 0, 0, 100, 0, 0);
            }

            pcb.beginText();

            var anchor = new Anchor(validationLink, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255)));
            anchor.setName("validationLink");
            anchor.setReference(validationLink);

            pdf.add(anchor);

            contents.forEach(content -> {

                var identifierRegular = new FontIdentifier(content.getFontFamily(), false, false);
                var identifierBold = new FontIdentifier(content.getFontFamily(), true, false);
                var identifierItalic = new FontIdentifier(content.getFontFamily(), false, true);
                var identifierBoldItalic = new FontIdentifier(content.getFontFamily(), true, true);

                var regularFamily = registerFont(identifierRegular);
                var boldFamily = registerFont(identifierBold);
                var italicFamily = registerFont(identifierItalic);
                var boldItalicFamily = registerFont(identifierBoldItalic);

                var encoding = BaseFont.WINANSI;
                if (regularFamily.endsWith(".ttf") || regularFamily.endsWith(".otf"))
                    encoding = BaseFont.IDENTITY_H;

                var bfRegular = FontFactory.getFont(regularFamily, encoding).getBaseFont();
                var bfBold = FontFactory.getFont(boldFamily, encoding).getBaseFont();
                var bfItalic = FontFactory.getFont(italicFamily, encoding).getBaseFont();
                var bfBoldItalic = FontFactory.getFont(boldItalicFamily, encoding).getBaseFont();

                var contentText = certificateTemplateProcessor.processTemplate(content.getContent());

                var margin = pdf.getPageSize().getWidth() - content.getX();
                var lineHeight = bfRegular.getAscentPoint(contentText, content.getFontSize()) - bfRegular.getDescentPoint(contentText, content.getFontSize());

                var texts = new ArrayList<>(List.of(String.join("@@@@", String.join("@@@@", contentText.split(" ")).split(regexTextTags)).split("@@@@")));
                texts.removeAll(List.of(" ", ""));
                var lines = new ArrayList<>(List.of(""));

                var lineIndex = 0;

                for (var text : texts) {
                    var word = text.replaceAll(regexTextTags, "");
                    var lineText = lines.get(lineIndex).replaceAll(regexTextTags, "");
                    var wordWidth = bfRegular.getWidthPoint(word, content.getFontSize());
                    var lineWidth = bfRegular.getWidthPoint(lineText, content.getFontSize());

                    if (content.getX() + wordWidth + lineWidth >= margin || text.equals(BREAKLINE)) {
                        lineIndex++;
                        lines.add(lineIndex, "");
                    }

                    if (!text.equals(BREAKLINE))
                        lines.set(lineIndex, lines.get(lineIndex).concat(String.format(" %s", text)));
                }
                var tags = List.of(BREAKLINE, STRONG_OPEN, STRONG_CLOSE, ITALIC_OPEN, ITALIC_CLOSE, UNDERLINE_OPEN, UNDERLINE_CLOSE);
                var bold = false;
                var italic = false;
                var underlined = false;
                var lineY = 0f;

                for (var line : lines) {
                    var lineX = content.getX().floatValue();
                    var words = new ArrayList<>(List.of(line.split(regexTextTags)));
                    words.removeAll(List.of(" ", ""));

                    for (var word : words) {

                        var lineWithoutTags = line.replaceAll("<[\\w-/]+>", "");
                        var writeWord = word.trim();
                        var selectedFont = bfRegular;
                        var ident = (margin - content.getX() - bfRegular.getWidthPoint(lineWithoutTags, content.getFontSize())) / 2;
                        ident = (ident > 0) ? ident : 0;

                        switch (word) {
                            case STRONG_OPEN:
                            case STRONG_CLOSE:
                                bold = word.equals(STRONG_OPEN);
                                break;
                            case ITALIC_OPEN:
                            case ITALIC_CLOSE:
                                italic = word.equals(ITALIC_OPEN);
                                break;
                            case UNDERLINE_OPEN:
                            case UNDERLINE_CLOSE:
                                underlined = word.equals(UNDERLINE_OPEN);
                                break;
                            default:

                                if (italic && bold) {
                                    selectedFont = bfBoldItalic;
                                } else if (bold) {
                                    selectedFont = bfBold;
                                } else if (italic) {
                                    selectedFont = bfItalic;
                                }

                                pcb.setFontAndSize(selectedFont, content.getFontSize());
                                pcb.setColorFill(Color.decode(content.getFontColor()));

                                if (writeWord.startsWith(","))
                                    lineX -= selectedFont.getWidthPoint(" ", content.getFontSize());

                                pcb.setTextMatrix(lineX + ident, content.getY() - lineY);

                                pcb.showText(writeWord);

                                break;
                        }

                        if (underlined) {

                            var width = selectedFont.getWidthPoint(writeWord, content.getFontSize());
                            if (lineX > content.getX().floatValue() && lineX + ident + width < margin && tags.contains(writeWord))
                                width = -selectedFont.getWidthPoint(" ", content.getFontSize());
                            if (lineX == content.getX().floatValue() && tags.contains(writeWord))
                                width = 0;

                            pcb.setColorStroke(Color.decode(content.getFontColor()));
                            pcb.moveTo(lineX + ident, content.getY() - 3f - lineY);
                            pcb.lineTo(lineX + ident + width, content.getY() - 3f - lineY);
                            pcb.stroke();

                            pcb.moveTo(content.getX(), content.getY() - lineY);
                        }
                        var wordWidth = 0f;
                        if (writeWord.startsWith(",")) {
                            wordWidth = selectedFont.getWidthPoint(writeWord, content.getFontSize());
                        } else {
                            wordWidth = selectedFont.getWidthPoint(String.format(" %s", writeWord), content.getFontSize());
                        }

                        if (!tags.contains(writeWord)) lineX += wordWidth;
                    }
                    lineY += lineHeight + 5;

                }


            });

            pcb.endText();
            pcb.sanityCheck();

        } catch (DocumentException | IOException | WriterException e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }

        pdf.addTitle(cert.getName());
        pdf.close();

        validationURL.setHash(Arrays.hashCode(fOut.toByteArray()));
        urlService.save(validationURL);

        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cert.getName() + ".pdf\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
            .body(fOut.toByteArray());
    }

    @Data
    @AllArgsConstructor
    private static class FontIdentifier {

        private String fontFamily;

        private Boolean bold;

        private Boolean italic;
    }
}
