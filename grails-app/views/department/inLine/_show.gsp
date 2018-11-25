<g:set var="entity" value="${message(code: 'department.entity', default: 'Department')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Department')}" />
<g:render template="/department/show" model="[department:department]" />