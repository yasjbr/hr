<g:set var="entity" value="${message(code: 'bordersSecurityCoordination.entity', default: 'bordersSecurityCoordination')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'bordersSecurityCoordination')}" />
<g:render template="/bordersSecurityCoordination/show" model="[bordersSecurityCoordination:bordersSecurityCoordination]" />

<g:if test="${params.isEmployeeDisabled}">
    <div class="clearfix form-actions text-center">
        <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
    </div>
</g:if>