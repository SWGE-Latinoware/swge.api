package celtab.swge.dto;

import celtab.swge.util.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(value = "id")
public class UserSimpleResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private FileResponseDTO userProfile;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> bibliography;
}
