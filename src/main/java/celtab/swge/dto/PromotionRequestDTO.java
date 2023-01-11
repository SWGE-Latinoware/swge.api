package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromotionRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "registration_type_id")
    private RegistrationTypeRequestDTO registrationType;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Date initialDateTime;

    @Column(nullable = false)
    private Date finalDateTime;

    @Column(nullable = false)
    private Boolean isVoucher;

    private Integer vacancies;
}
