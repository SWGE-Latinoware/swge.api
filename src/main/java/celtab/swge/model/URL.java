package celtab.swge.model;

import celtab.swge.model.enums.URLType;
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
public class URL extends BasicModel<Long> {

    @Column(nullable = false)
    private String urlFragment;

    @Column(nullable = false)
    private URLType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer hash;

    private String email;
}
