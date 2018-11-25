<g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'recruitmentCycle')}" />
<g:render template="/recruitmentCycle/tabs/infoTab" model="[recruitmentCycle:recruitmentCycle]" />