package celtab.swge.dto;


import celtab.swge.model.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPermissionRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserRequestDTO user;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionRequestDTO edition;

    @Column(nullable = false)
    private UserRole userRole;
}
