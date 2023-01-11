package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class RegistrationType extends BasicModel<Long> {

    @Column(nullable = false)
    private Date initialDateTime;

    @Column(nullable = false)
    private Date finalDateTime;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "registrationType", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Promotion> promotions;
}
