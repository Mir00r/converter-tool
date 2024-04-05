package com.sde.converter;

import java.util.List;

public class ConverterContext {

    private String userId;
    private String username;
    private String domainId;
    private List<String> roleCodeList;
    private String ipAddress;
    private String defaultOrganisationUnit;
    private String defaultOrganisation;
    private String defaultOrganisationUnitType;
    private String defaultOrganisationUnitCategory;
    private String defaultOrganisationCurrency;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public List<String> getRoleCodeList() {
        return roleCodeList;
    }

    public void setRoleCodeList(List<String> roleCodeList) {
        this.roleCodeList = roleCodeList;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDefaultOrganisationUnit() {
        return defaultOrganisationUnit;
    }

    public void setDefaultOrganisationUnit(String defaultOrganisationUnit) {
        this.defaultOrganisationUnit = defaultOrganisationUnit;
    }

    public String getDefaultOrganisation() {
        return defaultOrganisation;
    }

    public void setDefaultOrganisation(String defaultOrganisation) {
        this.defaultOrganisation = defaultOrganisation;
    }

    public String getDefaultOrganisationUnitType() {
        return defaultOrganisationUnitType;
    }

    public void setDefaultOrganisationUnitType(String defaultOrganisationUnitType) {
        this.defaultOrganisationUnitType = defaultOrganisationUnitType;
    }

    public String getDefaultOrganisationUnitCategory() {
        return defaultOrganisationUnitCategory;
    }

    public void setDefaultOrganisationUnitCategory(String defaultOrganisationUnitCategory) {
        this.defaultOrganisationUnitCategory = defaultOrganisationUnitCategory;
    }

    public String getDefaultOrganisationCurrency() {
        return defaultOrganisationCurrency;
    }

    public void setDefaultOrganisationCurrency(String defaultOrganisationCurrency) {
        this.defaultOrganisationCurrency = defaultOrganisationCurrency;
    }
}
