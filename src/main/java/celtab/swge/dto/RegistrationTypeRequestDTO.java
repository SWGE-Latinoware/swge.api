package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationTypeRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date initialDateTime;

    @Column(nullable = false)
    private Date finalDateTime;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "registrationType", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PromotionRequestDTO> promotions;
}
