<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'legalIdentifier.entity', default: 'LegalIdentifier List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LegalIdentifier List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/legalIdentifier/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'legalIdentifier',action:'list')}'"/>
</div>
</body>
</html>