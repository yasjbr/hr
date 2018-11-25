<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'absence.entity', default: 'absence')}" />
<g:set var="tabEntities" value="${message(code: 'absence.entities', default: 'absence')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list absence')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create absence')}" />


<el:form action="#" style="display: none;" name="absenceSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/absence/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>