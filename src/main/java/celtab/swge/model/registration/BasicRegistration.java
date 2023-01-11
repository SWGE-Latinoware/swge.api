package celtab.swge.model.registration;

import celtab.swge.model.BasicModel;
import celtab.swge.model.Edition;
import celtab.swge.util.UUIDUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class BasicRegistration extends BasicModel<Long> implements UUIDUtils {

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false)
    private Double finalPrice;

    @Column(nullable = false)
    private String userRegistrationId;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date registrationDateTime;

    @PrePersist
    @PreUpdate
    private void validateUserRegistrationId() {
        if (userRegistrationId == null) {
            userRegistrationId = getRandomUUIDString();
        }
    }

}
