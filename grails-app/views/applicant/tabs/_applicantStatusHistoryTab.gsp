<msg:page/>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'applicantStatusHistory.entity', default: 'interview')}" />
    <g:set var="tabEntities" value="${message(code: 'applicantStatusHistory.entities', default: 'interview')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list interview')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create interview')}" />


    <el:form action="#" style="display: none;" name="applicantStatusHistorySearchForm">
        <el:hiddenField name="applicant.id" value="${entityId}" />
    </el:form>
    <g:render template="/applicantStatusHistory/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_COLUMNS']"/>

</div>