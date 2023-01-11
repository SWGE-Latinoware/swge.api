package celtab.swge.dto;

import celtab.swge.model.enums.RequestType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteRequestRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private Date requestDate;

    private String note;

    private String applicantContact;

    @Column(nullable = false)
    private RequestType requestType;
}
