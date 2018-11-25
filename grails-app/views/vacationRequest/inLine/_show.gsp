<g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'vacationRequest')}" />
<g:render template="/vacationRequest/show" model="[vacationRequest:vacationRequest]" />


<g:if test="${params.isEmployeeDisabled}">
    <div class="clearfix form-actions text-center">
        <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
    </div>
</g:if>