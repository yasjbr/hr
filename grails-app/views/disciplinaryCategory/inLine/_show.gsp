<g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'disciplinaryCategory')}" />
<g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'disciplinaryCategory')}" />
<g:render template="/disciplinaryCategory/show" model="[disciplinaryCategory:disciplinaryCategory]" />