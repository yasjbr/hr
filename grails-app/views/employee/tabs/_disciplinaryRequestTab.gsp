<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'Organization List')}" />
<g:set var="tabEntity" value="${message(code: 'disciplinaryRequest.entity', default: 'disciplinaryRequest')}" />
<g:set var="tabEntities" value="${message(code: 'disciplinaryRequest.entities', default: 'disciplinaryRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list disciplinaryRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create disciplinaryRequest')}" />


<el:form action="#" style="display: none;" name="disciplinaryRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
    <el:hiddenField name="requestStatusList" value="APPROVED,ADD_PETITION_REQUEST" />
</el:form>
<g:render template="/disciplinaryRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>