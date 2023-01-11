package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Institution extends BasicModel<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    private String shortName;

    private String country;

    private String state;

    private String city;

    private String cellPhone;

    private String phone;

    private String website;

    @OneToMany(mappedBy = "institution", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Space> spaces;
}
