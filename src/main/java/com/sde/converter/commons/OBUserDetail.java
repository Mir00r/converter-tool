package com.sde.converter.commons;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Data
@NoArgsConstructor
public class OBUserDetail extends OBBase implements UserDetails {

    private static final long serialVersionUID = 1L;
    private OBUserDetail oldObUserDetail;
    private String jobTitle;
    private Date effectiveDate;
    private String effectiveDateStr;
    private Date expiredDate;
    private String expiredDateStr;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobilePhone;
    private Date birthDate;
    private String birthDateStr;
    private String idType;
    private String idNumber;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String postcode;
    private String communication;
    private Date lastLoginTime;
    private String lastLoginTimeStr;
    private Date lastLogoutTime;
    private String lastLogoutTimeStr;
    private String passwordType;
    private String reportedTo;
//    private List<OBPermissionDetail> permissions;
//    private List<OBUserRoleDetail> userRoleDetailList;
//    private List<OBUserGroupDetail> userGroupDetailList;
//    private List<OBUserOrganizationUnitMpDetail> obUserOrganizationUnitMpDetails;
//    private OBUserOrganizationUnitMpDetail defaultOrganizationUnitMpDetail;
//    private Set<OBGrantedAuthorityDetail> authorities;
    private String createdBy;
    private Date createdDatetime;
    private String updatedBy;
    private Date updatedDatetime;
    private String domainId;
    private String statusCode;
    private String secondaryPassword;
    private String tac;
    private String token;
    private String forceChangePassword;
    private String password;
    private String tokenSerialNumber;
    private String username;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private String disabledFlag;
    private int loginAttemptCount;
    private String uploadDocId;
    private byte[] document;
    private String fileType;
    private String authMethod;
    private Date authIntervalDate;
    private String authIntervalDateStr;
    private String type;
    private String isOfficer;
    private String cifNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return null;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return false;
//    }
}
