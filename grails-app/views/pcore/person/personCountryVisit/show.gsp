<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonCountryVisit List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personCountryVisit/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personCountryVisit',action:'list')}'"/>
</div>
</body>
</html>