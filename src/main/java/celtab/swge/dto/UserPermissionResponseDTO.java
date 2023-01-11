package celtab.swge.dto;


import celtab.swge.model.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPermissionResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserResponseDTO user;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionResponseDTO edition;

    @Column(nullable = false)
    private UserRole userRole;
}
