package celtab.swge.model;

import celtab.swge.model.enums.AuditAction;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.Date;

@Entity(name = "audits")
@ToString
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Audit extends BasicModel<Long> {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdDate = Date.from(Instant.now());

    @Column(nullable = false)
    private String responsibleUser;

    @Column(nullable = false)
    private String tableName;

    @Column
    private Long dataId;

    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private String newData;
}
