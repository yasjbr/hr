<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/vacationRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.REQUEST_FOR_EDIT_VACATION,
                                           vacationRequest: request]"/>
<g:render template="/vacationRequest/form" model="[hideInterval: true, hideEmployeeInfo: true]"/>
