<g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'jobRequisition')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'jobRequisition')}" />
<g:render template="/jobRequisition/show" model="[jobRequisition:jobRequisition]" />

<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>