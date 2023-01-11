package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpeakerActivityRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityRequestDTO activity;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private UserRequestDTO speaker;

}
