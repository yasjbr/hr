<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee List')}" />
<g:set var="tabEntity" value="${message(code: 'bordersSecurityCoordination.entity', default: 'bordersSecurityCoordination')}" />
<g:set var="tabEntities" value="${message(code: 'bordersSecurityCoordination.entities', default: 'bordersSecurityCoordination')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list bordersSecurityCoordination')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create bordersSecurityCoordination')}" />


<el:form action="#" style="display: none;" name="bordersSecurityCoordinationSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
    <el:hiddenField name="requestStatus" value="APPROVED" />
</el:form>
<g:render template="/bordersSecurityCoordination/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>