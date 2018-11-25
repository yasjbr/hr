<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}"/>
    <g:set var="tabEntity" value="${message(code: 'employee.allowance.label', default: 'allowanceRequest')}"/>
    <g:set var="tabEntities" value="${message(code: 'allowanceRequest.entities', default: 'allowanceRequest')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list allowanceRequest')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create allowanceRequest')}"/>


    <el:form action="#" style="display: none;" name="allowanceRequestSearchForm">
        <el:hiddenField name="employee.id" value="${entityId}"/>
        <el:hiddenField name="requestStatus" value="APPROVED"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.ALLOWANCE_CONTINUE_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.ALLOWANCE_EDIT_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.ALLOWANCE_STOP_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.ALLOWANCE_REQUEST}"/>
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>
    </el:form>
    <g:render template="/allowanceRequest/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>
</div>