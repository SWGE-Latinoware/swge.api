package celtab.swge.model.registration;

import celtab.swge.model.Promotion;
import celtab.swge.model.enums.PaymentType;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Registration extends BasicRegistration {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndividualRegistration> individualRegistrations;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    private String identifier;

    @ColumnDefault("0")
    private PaymentType paymentType;
}
