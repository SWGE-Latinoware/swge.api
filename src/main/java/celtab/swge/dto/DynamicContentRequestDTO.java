package celtab.swge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicContentRequestDTO extends GenericDTO<Long> {

    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private CertificateRequestDTO certificate;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false, columnDefinition = "VARCHAR")
    private String content;

    @Column(nullable = false)
    private Integer fontSize;

    @Column(nullable = false)
    private String fontFamily;

    @Column(nullable = false)
    private String fontColor;

}
