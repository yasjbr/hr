<msg:page/>
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'interview.entity', default: 'interview')}" />
    <g:set var="tabEntities" value="${message(code: 'interview.entities', default: 'interview')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list interview')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create interview')}" />


    <el:form controller="applicant" action="getInterview" style="display: none;" name="interviewSearchForm">
        <el:hiddenField name="applicant.id" value="${entityId}" />
    </el:form>
    <g:render template="/interview/inLine/show"
              model="[interview:interview, isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>

</div>