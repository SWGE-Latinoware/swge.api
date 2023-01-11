package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class DynamicContent extends BasicModel<Long> {

    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

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
