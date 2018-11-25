<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'request.entity', default: 'request')}" />
<g:set var="tabEntities" value="${message(code: 'request.entities', default: 'request')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list request')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create request')}" />


<el:form action="#" style="display: none;" name="requestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/request/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>