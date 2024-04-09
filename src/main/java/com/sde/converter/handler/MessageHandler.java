package com.sde.converter.handler;

import com.sde.converter.ThreadLocalConverterContextHolderStrategy;
import com.sde.converter.commons.*;
import com.sde.converter.utils.BeanUtil;
import com.sde.converter.utils.DateUtil;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageHandler {

    private static Logger log = LoggerFactory.getLogger(MessageHandler.class);

    public static OBBase handleResponseStatus(OBBase response, OBBase request, List<OBErrorDetail> errorList, MethodSignature methodSignature, boolean isThrownException) {
        if (response == null) {
            Method method = methodSignature.getMethod();

            try {
                @SuppressWarnings("unchecked")
                Class<OBBase> c = (Class<OBBase>) method.getReturnType();
                response = c.newInstance();
            } catch (Exception ie) {
                log.error(ie.getMessage(), ie);
                response = new OBBase();
            }

            if (request!=null && request.getObHeader() != null) {
                response.setObHeader(request.getObHeader());
            } else {
                response.setObHeader(new OBHeader());
            }
        }
        response.setObHeader(mapHeaderResponse(response.getObHeader(), errorList, response.getObHeader().getSuccessDetails(), isThrownException));
        if(response.getObHeader().getSuccessDetails() != null) {
            response.getObHeader().getSuccessDetails().removeIf(success -> Constants.MULTI_SUCCESS_TYPE.equals(success.getType()));
        }

        return response;
    }

    public static OBHeader getObHeader() {
        return getObHeader(null);
    }

    public static OBHeader getObHeader(String id) {
        OBHeader obHeader = new OBHeader();

        obHeader.setId(id);
        obHeader.setUsername(ThreadLocalConverterContextHolderStrategy.getSdeContext().getUsername());
        obHeader.setUserId(ThreadLocalConverterContextHolderStrategy.getSdeContext().getUserId());
        obHeader.setDomainId(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDomainId());
//        obHeader.setDefaultOrganization(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganization());
//        obHeader.setDefaultOrganizationUnit(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganizationUnit());
//        obHeader.setDefaultOrganizationUnitType(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganizationUnitType());
        obHeader.setRoleCodeList(ThreadLocalConverterContextHolderStrategy.getSdeContext().getRoleCodeList());
        obHeader.setIpAddress(ThreadLocalConverterContextHolderStrategy.getSdeContext().getIpAddress());

        return obHeader;
    }

    public static void mapFailResponseValueWithErrors(OBBase request, OBBase response, String errorCode) {
        mapFailResponseValueWithErrors(request, response, errorCode, new String[0]);
    }

    public static void mapFailResponseValueWithErrors(OBBase request, OBBase response, String errorCode, String... arguments) {
        response.setObHeader(new OBHeader());
        BeanUtil.copyProperties(request.getObHeader(), response.getObHeader());

        List<OBErrorDetail> errorList = new ArrayList<OBErrorDetail>();
        OBErrorDetail errorDetail = new OBErrorDetail();
        errorDetail.setCode(errorCode);
        errorDetail.setType(Constants.ERROR_TYPE);
        if(arguments.length > 0) {
            errorDetail.setArguments(arguments);
        }
        errorList.add(errorDetail);
        response.getObHeader().setErrorDetails(errorList);

        handleResponseStatus(response.getObHeader(), errorList);
    }

    public static void mapFailResponseValueWithErrors(OBBase request, OBBase response, List<OBErrorDetail> errorList) {
        // copy all request header to response header
        response.setObHeader(request.getObHeader());
        //BeanUtil.copyProperties(request.getObHeader(), response.getObHeader());
        handleResponseStatus(response.getObHeader(), errorList);
    }

    public static void mapSuccessResponseValue(OBBase request, OBBase response) {
        response.setObHeader(request.getObHeader());
        response.getObHeader().setSuccess(Boolean.TRUE);
        response.getObHeader().setStatusCode("AA");
        response.getObHeader().setDateTimeOut(DateUtil.retrieveDateNow());
    }

    public static void mapDefaultSuccessResponseValue(OBBase response)
    {
        response.getObHeader().setSuccess(Boolean.TRUE);
        response.getObHeader().setStatusCode("AA");
        response.getObHeader().setDateTimeOut(DateUtil.retrieveDateNow());
    }

    public static void mapSuccessResponseValue(OBHeader request, OBHeader response)
    {
        // copy all request header to response header
        BeanUtil.copyProperties(request, response);
        response.setSuccess(Boolean.TRUE);
        response.setStatusCode("AA");
        response.setDateTimeOut(DateUtil.retrieveDateNow());
    }

    public static void mapSuccessResponseValue(OBHeader request, OBHeader response, String referenceNumber)
    {
        mapSuccessResponseValue(request, response);
        response.setReferenceNumber(referenceNumber);
    }

    public static void mapSuccessResponseValue(OBHeader request, OBHeader response, String referenceNumber, List<OBSuccessDetail> successList)
    {
        mapSuccessResponseValue(request, response, referenceNumber);
        response.setSuccessDetails(successList);
    }

    /**
     * 1. Copy request properties into response properties
     * 2. Set parameter error list into response
     * 3. Handle status according to error (PS or AB)
     * 4. Set reference number into response
     * @param response
     * @param errorCode
     */
    public static void mapFailResponseValueWithErrors(OBHeader request, OBHeader response, String referenceNumber, List<OBErrorDetail> errorList) {
        BeanUtil.copyProperties(request, response);
        response.setErrorDetails(errorList);
        handleResponseStatus(response, errorList);
        response.setReferenceNumber(referenceNumber);
    }

    public static void mapFailResponseValueWithErrors(OBHeader request, OBHeader response, String referenceNumber, List<OBErrorDetail> errorList, List<OBSuccessDetail> successList) {
        BeanUtil.copyProperties(request, response);
        response.setErrorDetails(errorList);
        response.setSuccessDetails(successList);
        handleResponseStatus(response, errorList, successList);
        response.setReferenceNumber(referenceNumber);
    }


    /**
     * 1. Copy request properties into response properties
     * 2. Construct OBErrorDetail and set error code together with pass in additional arguments
     * 3. Set OBErrordetail into list
     * 4. Set error list into response
     * 5. Handle status according to error (PS or AB)
     * 6. Set reference number into response
     * @param response
     * @param errorCode
     */
    public static void mapFailResponseValueWithErrors(OBHeader request, OBHeader response, String referenceNumber, String errorCode, String... arguments) {
        BeanUtil.copyProperties(request, response);
        mapFailResponseValueWithErrors(response, errorCode, arguments);
        response.setReferenceNumber(referenceNumber);
    }

    /**
     * 1. Copy request properties into response properties
     * 2. Construct OBErrorDetail and set error code, default arguments to empty array
     * 3. Set OBErrordetail into list
     * 4. Set error list into response
     * 5. Handle status according to error (PS or AB)
     * 6. Set reference number into response
     * @param response
     * @param errorCode
     */
    public static void mapFailResponseValueWithErrors(OBHeader request, OBHeader response, String errorCode) {
        BeanUtil.copyProperties(request, response);
        mapFailResponseValueWithErrors(response, errorCode);
    }

    /**
     * 1. Construct OBErrorDetail and set error code, default arguments to empty array
     * 2. Set OBErrordetail into list
     * 3. Set error list into response
     * 4. Handle status according to error (PS or AB)
     * @param response
     * @param errorCode
     */
    public static void mapFailResponseValueWithErrors(OBHeader response, String errorCode)
    {
        mapFailResponseValueWithErrors(response, errorCode, new String[0]);
    }

    /**
     * 1. Construct OBErrorDetail and set error code together with pass in additional arguments
     * 2. Set OBErrordetail into list
     * 3. Set error list into response
     * 4. Handle status according to error (PS or AB)
     * @param response
     * @param errorCode
     */
    public static void mapFailResponseValueWithErrors(OBHeader response, String errorCode, String... arguments)
    {
        List<OBErrorDetail> errorList = new ArrayList<OBErrorDetail>();
        OBErrorDetail errorDetail = new OBErrorDetail();
        errorDetail.setCode(errorCode);
        errorDetail.setType(Constants.ERROR_TYPE);
        errorDetail.setArguments(arguments);
        errorList.add(errorDetail);
        response.setErrorDetails(errorList);
        handleResponseStatus(response, errorList);
    }

    public static OBHeader handleResponseStatus(OBHeader response, List<OBErrorDetail> errorList) {
        response = mapHeaderResponse(response, errorList, null, false);
        return response;
    }

    public static OBHeader handleResponseStatus(OBHeader response, List<OBErrorDetail> errorList, List<OBSuccessDetail> successList) {
        response = mapHeaderResponse(response, errorList, successList, false);
        return response;
    }

    public static OBHeader mapHeaderResponse(OBHeader response, List<OBErrorDetail> errorList, List<OBSuccessDetail> successList, boolean isThrownException) {
        if(isThrownException) {
            response.setErrorDetails(errorList);
            response.setSuccess(Boolean.FALSE);
            response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
        } else {
            if (response.getErrorDetails()!=null && response.getErrorDetails().size() > 0) {
                if (hasApproval(response.getSuccessDetails()))
                    response.setStatusCode(Constants.PENDING_APPROVAL_STATUS_CODE);
                else if (hasWarning(response.getErrorDetails()) && !hasError(response.getErrorDetails())) {
                    response.setSuccess(Boolean.TRUE);
                    response.setStatusCode(Constants.PARTIAL_SUCCESS_RESPONSE_STATUS_CODE);
                    if (!hasSuccess(response.getSuccessDetails()))
                        response.setStatusCode(Constants.WARNING_RESPONSE_STATUS_CODE);
                }
                else if (hasError(response.getErrorDetails())) {
                    response.setSuccess(Boolean.FALSE);
                    response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
                    if (hasSuccess(response.getSuccessDetails())) {
                        response.setSuccess(Boolean.TRUE);
                        response.setStatusCode(Constants.PARTIAL_SUCCESS_RESPONSE_STATUS_CODE);
                    }
                } else {
                    // logically this condition never happened
                    response.setSuccess(Boolean.FALSE);
                    response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
                }
            } else if (errorList!=null && errorList.size() > 0) {
                if (hasApproval(response.getSuccessDetails()))
                    response.setStatusCode(Constants.PENDING_APPROVAL_STATUS_CODE);
                else if (hasWarning(errorList) && !hasError(errorList)) {
                    response.setSuccess(Boolean.TRUE);
                    response.setStatusCode(Constants.PARTIAL_SUCCESS_RESPONSE_STATUS_CODE);
                    if (!hasSuccess(successList))
                        response.setStatusCode(Constants.WARNING_RESPONSE_STATUS_CODE);
                }
                else if (hasError(errorList)) {
                    response.setSuccess(Boolean.FALSE);
                    response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
                    if (hasSuccess(successList)) {
                        response.setSuccess(Boolean.TRUE);
                        response.setStatusCode(Constants.PARTIAL_SUCCESS_RESPONSE_STATUS_CODE);
                    }
                } else {
                    // logically this condition never happened
                    response.setSuccess(Boolean.FALSE);
                    response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
                }
                response.setErrorDetails(errorList);
            }
//            else if (Constants.YES.equals(ThreadLocalConverterContextHolderStrategy.getHasExceptionFlag())) {
//                response.setSuccess(Boolean.FALSE);
//                response.setStatusCode(Constants.FAIL_RESPONSE_STATUS_CODE);
//                response.setSuccessDetails(successList);
//            }
            else {
                response.setSuccess(Boolean.TRUE);
                response.setStatusCode(Constants.SUCCESS_RESPONSE_STATUS_CODE);
                response.setSuccessDetails(successList);

                if (hasApproval(response.getSuccessDetails()))
                    response.setStatusCode(Constants.PENDING_APPROVAL_STATUS_CODE);
            }
        }

        response.setDateTimeOut(DateUtil.retrieveDateNow());
        return response;
    }

    private static boolean hasError(List<OBErrorDetail> errorList) {
        if (errorList != null && !errorList.isEmpty()) {
            return errorList.stream().anyMatch(error -> Constants.ERROR_TYPE.equals(error.getType()) || error.getType() == null);
        }

        return false;
    }

    private static boolean hasWarning(List<OBErrorDetail> errorList) {
        if (errorList != null && !errorList.isEmpty()) {
            return errorList.stream().anyMatch(error -> Constants.WARNING_TYPE.equals(error.getType()));
        }

        return false;
    }

    private static boolean hasSuccess(List<OBSuccessDetail> succcessList) {
        if (succcessList != null && !succcessList.isEmpty()) {
            return succcessList.stream().anyMatch(success -> (Constants.SUCCESS_TYPE.equals(success.getType()) || Constants.MULTI_SUCCESS_TYPE.equals(success.getType())));
        }

        return false;
    }

    private static boolean hasApproval(List<OBSuccessDetail> succcessList) {
        if (succcessList != null && !succcessList.isEmpty()) {
            return succcessList.stream().anyMatch(success -> (Constants.APPROVAL_TYPE.equals(success.getType())));
        }

        return false;
    }


    public static void mapFailResponseValueWithErrors(OBHeader response, String errorCode,
                                                      TypedArgument... typedArguments) {
        List<OBErrorDetail> errorList = new ArrayList<OBErrorDetail>();
        OBErrorDetail errorDetail = new OBErrorDetail();
        errorDetail.setCode(errorCode);
        errorDetail.setType(Constants.ERROR_TYPE);
        errorDetail.setArgumentDetails(new LinkedList<OBArgumentDetail>());
        // TODO - Temporary backward compatibility
        String[] arguments = new String[typedArguments.length];

        for (int i = 0; i < typedArguments.length; i++) {
            TypedArgument eachTypedArgument = typedArguments[i];
            OBArgumentDetail obArgumentDetail = new OBArgumentDetail();
            obArgumentDetail.setArgument(eachTypedArgument.getArgument());
            obArgumentDetail.setType(eachTypedArgument.getArgumentType());
            errorDetail.getArgumentDetails().add(obArgumentDetail);
            // TODO - Temporary backward compatibility
            arguments[i] = eachTypedArgument.getArgument();
        }
        errorDetail.setArguments(arguments);
        errorList.add(errorDetail);
        response.setErrorDetails(errorList);
        handleResponseStatus(response, errorList);
    }
}
