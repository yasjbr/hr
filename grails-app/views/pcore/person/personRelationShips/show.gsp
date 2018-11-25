<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personRelationShips.entity', default: 'PersonRelationShips List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonRelationShips List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personRelationShips/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personRelationShips',action:'list')}'"/>
</div>
</body>
</html>