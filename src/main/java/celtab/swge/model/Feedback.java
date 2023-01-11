package celtab.swge.model;

import celtab.swge.model.enums.FeedbackStatus;
import celtab.swge.model.user.User;
import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Feedback extends BasicModel<Long> {

    @Column(nullable = false)
    private String title;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date creationDateTime;

    @LastModifiedDate
    @Column(nullable = false)
    private Date modificationDateTime;

    @Column(nullable = false)
    private FeedbackStatus status;

    private String webVersion;

    private String apiVersion;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> description;

    /**
     * Frontend sends a zip file with 1 or more files.
     */
    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
