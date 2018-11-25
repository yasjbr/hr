<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'employmentServiceRequest.entity', default: 'employmentServiceRequest')}" />
<g:set var="tabEntities" value="${message(code: 'employmentServiceRequest.entities', default: 'employmentServiceRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list employmentServiceRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create employmentServiceRequest')}" />


<el:form action="#" style="display: none;" name="employmentServiceRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
    <el:hiddenField name="requestStatus" value="APPROVED" />
</el:form>
<g:render template="/employmentServiceRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>