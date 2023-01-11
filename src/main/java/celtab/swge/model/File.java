package celtab.swge.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class File extends BasicModel<Long> {

    @Column(nullable = false)
    private String name;

    private String format;

}
