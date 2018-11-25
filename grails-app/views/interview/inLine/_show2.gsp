<g:set var="entity" value="${message(code: 'interview.entity', default: 'interview')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'interview')}" />
<g:render template="/interview/show_withOutButtons" model="[interview:interview]" />