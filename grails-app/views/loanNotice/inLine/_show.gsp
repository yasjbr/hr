<g:set var="entity" value="${message(code: 'loanNotice.entity', default: 'loanNotice')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'loanNotice')}" />
<g:render template="/loanNotice/show" model="[loanNotice:loanNotice]" />