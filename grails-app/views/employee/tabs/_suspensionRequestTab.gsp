<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'suspensionRequest.entity', default: 'suspensionRequest')}" />
<g:set var="tabEntities" value="${message(code: 'suspensionRequest.entities', default: 'suspensionRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list suspensionRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create suspensionRequest')}" />


<el:form action="#" style="display: none;" name="suspensionRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
    <el:hiddenField name="requestStatus" value="APPROVED" />
</el:form>
<g:render template="/suspensionRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:tabEntity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>