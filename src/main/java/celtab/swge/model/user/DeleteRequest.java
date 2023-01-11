package celtab.swge.model.user;

import celtab.swge.model.BasicModel;
import celtab.swge.model.enums.RequestType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class DeleteRequest extends BasicModel<Long> {

    @Column(nullable = false)
    private Date requestDate;

    private String note;

    private String applicantContact;

    @Column(nullable = false)
    private RequestType requestType;

    @OneToMany(mappedBy = "deleteRequest")
    private List<Exclusion> exclusions;
}
