package celtab.swge.dto;

import celtab.swge.model.enums.URLType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class URLResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String urlFragment;

    @Column(nullable = false)
    private URLType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserSimpleResponseDTO user;

    private Integer hash;

    private String email;
}
