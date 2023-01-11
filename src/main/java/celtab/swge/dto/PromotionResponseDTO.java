package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromotionResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "registration_type_id")
    @ToString.Exclude
    private RegistrationTypeResponseDTO registrationType;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Date initialDateTime;

    @Column(nullable = false)
    private Date finalDateTime;

    private Integer vacancies;

    @Column(nullable = false)
    private Boolean isVoucher;

    @Transient
    private Integer remainingVacancies;
}
