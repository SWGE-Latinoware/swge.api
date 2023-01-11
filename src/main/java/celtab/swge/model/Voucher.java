package celtab.swge.model;

import celtab.swge.util.UUIDUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Voucher extends BasicModel<Long> implements UUIDUtils {

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String voucherHash;

    @ManyToOne
    @JoinColumn(name = "edition_id")
    private Edition edition;

    @PrePersist
    @PreUpdate
    private void generateVoucherHash() {
        if (voucherHash == null) {
            voucherHash = getRandomUUIDString();
        }
    }
}
