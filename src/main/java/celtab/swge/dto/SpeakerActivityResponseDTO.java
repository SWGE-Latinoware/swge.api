package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpeakerActivityResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @ToString.Exclude
    private ActivityResponseDTO activity;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private UserResponseDTO speaker;

}
