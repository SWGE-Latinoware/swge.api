package celtab.swge.dto;

import celtab.swge.model.enums.FeedbackStatus;
import celtab.swge.util.HashMapConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Date creationDateTime;

    @Column(nullable = false)
    private Date modificationDateTime;

    @Column(nullable = false)
    private FeedbackStatus status;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileResponseDTO file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserResponseDTO user;

    private String webVersion;

    private String apiVersion;
}
