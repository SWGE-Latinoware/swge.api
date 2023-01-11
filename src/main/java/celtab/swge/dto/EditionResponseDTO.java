package celtab.swge.dto;

import celtab.swge.model.Language;
import celtab.swge.model.enums.EditionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class EditionResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String shortName;

    @Column(nullable = false, unique = true)
    private Integer year;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Date initialDate;

    @Column(nullable = false)
    private Date finalDate;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private InstitutionResponseDTO institution;

    private String description;

    @Column(nullable = false)
    private EditionType type;

    @ManyToOne
    @JoinColumn(name = "default_language_id")
    private Language defaultLanguage;

    @ManyToOne
    @JoinColumn(name = "default_dark_theme_id")
    private ThemeResponseDTO defaultDarkTheme;

    @ManyToOne
    @JoinColumn(name = "default_light_theme_id")
    private ThemeResponseDTO defaultLightTheme;

    @ManyToOne
    @JoinColumn(name = "logo_id")
    private FileResponseDTO logo;

    @ManyToOne
    @JoinColumn(name = "registration_type_id")
    private RegistrationTypeResponseDTO registrationType;
}
