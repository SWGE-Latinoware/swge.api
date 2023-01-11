package celtab.swge.model;

import celtab.swge.model.enums.UserRole;
import celtab.swge.model.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UserPermission extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @Column(nullable = false)
    private UserRole userRole;
}
