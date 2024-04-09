package com.sde.converter.commons;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OBHeader implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String domainId;
    private String dateTimeIn;
    private String dateTimeOut;
    private Boolean success;
    private String statusCode;
    private String statusMessage;
    private List<OBErrorDetail> errorDetails;
    private List<OBSuccessDetail> successDetails;
    private String referenceNumber;
    private String username;
    private String userId;
    private String ipAddress;
    private List<String> roleCodeList;

    // these are project domain specific fields
    private String defaultOrganisationUnit;
    private String defaultOrganisation;
    private String defaultOrganisationUnitType;
    private String defaultOrganisationUnitCategory;
    private String defaultOrganisationUnitCurrency;
}
