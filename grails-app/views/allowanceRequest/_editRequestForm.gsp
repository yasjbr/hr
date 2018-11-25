<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/allowanceRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.ALLOWANCE_EDIT_REQUEST,
                                           allowanceRequest: request]"/>
<g:render template="/allowanceRequest/form" model="[hideInterval: true, hideEmployeeInfo: true, allowanceRequest: request, hideManagerialOrderInfo:hideManagerialOrderInfo]"/>
