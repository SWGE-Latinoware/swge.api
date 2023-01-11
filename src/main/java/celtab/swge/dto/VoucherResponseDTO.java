package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoucherResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionResponseDTO edition;
}
