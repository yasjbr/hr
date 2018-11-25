<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'externalTransferRequest.entity', default: 'externalTransferRequest')}" />
<g:set var="tabEntities" value="${message(code: 'externalTransferRequest.entities', default: 'externalTransferRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list externalTransferRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create externalTransferRequest')}" />


<el:form action="#" style="display: none;" name="externalTransferRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="requestStatusList" value="APPROVED,FINISHED" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/externalTransferRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>