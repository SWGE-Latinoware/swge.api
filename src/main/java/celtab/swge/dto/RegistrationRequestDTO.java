package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationRequestDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String userRegistrationId;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private EditionRequestDTO edition;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserRequestDTO user;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndividualRegistrationRequestDTO> individualRegistrations;
}
