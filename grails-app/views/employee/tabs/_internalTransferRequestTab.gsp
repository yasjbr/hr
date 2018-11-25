<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'internalTransferRequest.entity', default: 'internalTransferRequest')}" />
<g:set var="tabEntities" value="${message(code: 'internalTransferRequest.entities', default: 'internalTransferRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list internalTransferRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create internalTransferRequest')}" />


<el:form action="#" style="display: none;" name="internalTransferRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="requestStatus" value="FINISHED" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/internalTransferRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>