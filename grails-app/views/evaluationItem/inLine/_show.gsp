<g:set var="entity" value="${message(code: 'evaluationItem.entity', default: 'evaluationItem')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'evaluationItem')}" />
<g:render template="/evaluationItem/show" model="[evaluationItem:evaluationItem]" />

<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>