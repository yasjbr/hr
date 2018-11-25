<g:set var="entity" value="${message(code: 'interview.entity', default: 'interview')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'interview')}" />
<g:render template="/interview/show" model="[interview:interview, isRecruitmentCycleTab:true]" />

<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>