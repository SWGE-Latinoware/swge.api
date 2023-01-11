package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicContentResponseDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "certificate_id")
    @ToString.Exclude
    private CertificateResponseDTO certificate;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer fontSize;

    @Column(nullable = false)
    private String fontFamily;

    @Column(nullable = false)
    private String fontColor;
}
