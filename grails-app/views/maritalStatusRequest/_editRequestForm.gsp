<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/maritalStatusRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.MARITAL_STATUS_EDIT_REQUEST,
                                           maritalStatusRequest: request]"/>
<g:render template="/maritalStatusRequest/form" model="[hideInterval: true, hideEmployeeInfo: true, maritalStatusRequest: request,editRequest:true]"/>
