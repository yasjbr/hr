<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'employee List')}"/>
    <g:set var="tabEntity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest')}"/>
    <g:set var="tabEntities" value="${message(code: 'vacationRequest.entities', default: 'vacationRequest')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list vacationRequest')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create vacationRequest')}"/>


    <el:form action="#" style="display: none;" name="vacationRequestSearchForm">
        <el:hiddenField name="employee.id" value="${entityId}"/>
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>
        <el:hiddenField name="requestType[]"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.REQUEST_FOR_EDIT_VACATION}"/>
        <el:hiddenField name="requestType[]"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.REQUEST_FOR_VACATION_EXTENSION}"/>
        <el:hiddenField name="requestType[]"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.REQUEST_FOR_VACATION_STOP}"/>
        <el:hiddenField name="requestType[]" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.VACATION_REQUEST}"/>
        <el:hiddenField name="requestStatus" value="APPROVED"/>
    </el:form>
    <g:render template="/vacationRequest/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>
</div>