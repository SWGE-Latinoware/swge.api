package celtab.swge.model.user;

import celtab.swge.auth.oauth2.OAuth2UserInfoFactory;
import celtab.swge.model.*;
import celtab.swge.model.enums.Gender;
import celtab.swge.model.registration.Registration;
import celtab.swge.util.HashMapConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class User extends BasicUser implements OAuth2UserInfoFactory {

    @Column(unique = true, nullable = false)
    private String email;

    private String country;

    private String zipCode;

    private String state;

    private String city;

    private String addressLine1;

    private String addressLine2;

    private String phone;

    private String github;

    private String orcid;

    private String website;

    private String lattes;

    private String linkedin;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "VARCHAR")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> bibliography;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean admin;

    @Column(nullable = false)
    private Boolean emailCommunication;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean socialCommunication;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean confirmed;

    @Transient
    private Boolean completed;

    private String googleId;

    private String githubId;

    @Transient
    private Boolean googleConnected;

    @Transient
    private Boolean githubConnected;

    private Boolean alterPassword;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private File userProfile;

    @OneToMany(mappedBy = "user")
    private List<Registration> registrations;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<UserPermission> userPermissions;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<URL> urls;

    @OneToMany(mappedBy = "reviewer")
    private List<TutoredUser> tutoredUsersReviewed;

    @OneToMany(mappedBy = "responsibleUser")
    private List<Activity> activitiesResponsible;

    @OneToMany(mappedBy = "user")
    private List<Exclusion> exclusionRequests;

    @OneToMany(mappedBy = "dpo")
    private List<Exclusion> exclusionsResponsible;

    private Gender gender;

    private Date lastLogin;

    @PostLoad
    private void loadCompleted() {
        completed = verifyComplete();
        googleConnected = googleId != null && !googleId.isBlank();
        githubConnected = githubId != null && !githubId.isBlank();
    }

    private boolean verifyComplete() {
        return (country != null && !country.isBlank()) &&
            (zipCode != null && !zipCode.isBlank()) &&
            (state != null && !state.isBlank()) &&
            (city != null && !city.isBlank()) &&
            (addressLine1 != null && !addressLine1.isBlank()) &&
            (!isFakeTempEmailForJwt(email)) && (!isFakeTempName(getName())) &&
            (!isFakeTempName(getTagName())) && (Boolean.FALSE.equals(alterPassword) || alterPassword == null);
    }
}
