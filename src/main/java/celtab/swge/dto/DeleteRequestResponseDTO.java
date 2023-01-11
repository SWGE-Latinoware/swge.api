package celtab.swge.dto;

import celtab.swge.model.enums.RequestType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteRequestResponseDTO extends GenericDTO<Long> {
    @Column(nullable = false)
    private Date requestDate;

    private String note;

    private String applicantContact;

    @Column(nullable = false)
    private RequestType requestType;

    @OneToMany(mappedBy = "deleteRequest")
    @ToString.Exclude
    private List<ExclusionResponseDTO> exclusions;
}
