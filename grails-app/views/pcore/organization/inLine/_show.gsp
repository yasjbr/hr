<g:set var="entity" value="${message(code: 'organization.entity', default: 'Organization')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Organization')}" />
<g:render template="/organization/show" model="[organization:organization]" />