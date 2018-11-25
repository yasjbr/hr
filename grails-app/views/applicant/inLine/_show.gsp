<g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Applicant')}" />
<g:render template="/applicant/show" model="[applicant:applicant, isRecruitmentCycleTab:true]" />

<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>