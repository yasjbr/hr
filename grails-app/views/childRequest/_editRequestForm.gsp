<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/childRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.CHILD_EDIT_REQUEST,
                                           childRequest: request]"/>
<g:render template="/childRequest/form" model="[hideInterval: true, hideEmployeeInfo: true, childRequest: request]"/>
