<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/dispatchRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.DISPATCH_EDIT_REQUEST,
                                           dispatchRequest: request]"/>
<g:render template="/dispatchRequest/form" model="[hideInterval: true, hideEmployeeInfo: true, dispatchRequest: request]"/>
