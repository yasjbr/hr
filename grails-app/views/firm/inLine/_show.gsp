<g:set var="entity" value="${message(code: 'firm.entity', default: 'firm')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'firm')}" />
<g:render template="/firm/show" model="[firm:firm]" />