<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}"/>
    <g:set var="tabEntity" value="${message(code: 'dispatchRequest.label', default: 'dispatchRequest')}"/>
    <g:set var="tabEntities" value="${message(code: 'dispatchRequest.entities', default: 'dispatchRequest')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list dispatchRequest')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create dispatchRequest')}"/>


    <el:form action="#" style="display: none;" name="dispatchRequestSearchForm">
        <el:hiddenField name="employee.id" value="${entityId}"/>
        <el:hiddenField name="requestStatus" value="APPROVED"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.DISPATCH_EXTEND_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.DISPATCH_EDIT_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.DISPATCH_STOP_REQUEST}"/>
        <el:hiddenField name="requestType[]" value="${EnumRequestType.DISPATCH_REQUEST}"/>
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>
    </el:form>
    <g:render template="/dispatchRequest/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>
</div>