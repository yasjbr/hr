<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="tabEntity" value="${message(code: 'loanNoticeReplayRequest.entity', default: 'loanNoticeReplayRequest')}" />
<g:set var="tabEntities" value="${message(code: 'loanNoticeReplayRequest.entities', default: 'loanNoticeReplayRequest')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list loanNoticeReplayRequest')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create loanNoticeReplayRequest')}" />


<el:form action="#" style="display: none;" name="loanNoticeReplayRequestSearchForm">
    <el:hiddenField name="employee.id" value="${entityId}" />
    <el:hiddenField name="requestStatus" value="APPROVED" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>
<g:render template="/loanNoticeReplayRequest/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
</div>