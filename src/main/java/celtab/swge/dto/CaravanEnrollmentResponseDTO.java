package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaravanEnrollmentResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "caravan_id")
    @ToString.Exclude
    private CaravanResponseDTO caravan;

    @Column(nullable = false)
    private Boolean accepted;

    @Column(nullable = false)
    private Boolean payed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserResponseDTO user;

    @Column(nullable = false)
    private Boolean confirmed;
}
