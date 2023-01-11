package celtab.swge.dto;

import celtab.swge.model.enums.Gender;
import celtab.swge.model.enums.SpecialNeedsType;
import celtab.swge.util.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResponseDTO extends GenericDTO<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tagName;

    @Column(nullable = false)
    private String country;

    private String cellPhone;

    private Date birthDate;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    private String phone;

    private String github;

    private String orcid;

    private String website;

    private String lattes;

    private String linkedin;

    private Boolean alterPassword;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> bibliography;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionResponseDTO institution;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private FileResponseDTO userProfile;

    @Column(nullable = false)
    private Boolean admin;

    @Column(nullable = false)
    private Boolean emailCommunication;

    @Column(nullable = false)
    private Boolean socialCommunication;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean confirmed;

    private String otherNeeds;

    @Transient
    private Boolean completed;

    @Transient
    private Boolean googleConnected;

    @Transient
    private Boolean githubConnected;

    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private List<SpecialNeedsType> needsTypes;

    private Gender gender;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties(value = {"user"})
    @ToString.Exclude
    private List<UserPermissionResponseDTO> userPermissions;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties(value = {"user"})
    @ToString.Exclude
    private List<ExclusionResponseDTO> exclusionRequests;

    private Date lastLogin;
}
