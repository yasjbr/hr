<g:set var="entity" value="${message(code: 'employee.entity', default: 'employee')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Employee')}" />
<g:render template="/employee/show" model="[employee:employee]" />

<g:if test="${isReadOnly || params['isReadOnly'] == "true"}">
    <div class="clearfix form-actions text-center">
        <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
    </div>
</g:if>