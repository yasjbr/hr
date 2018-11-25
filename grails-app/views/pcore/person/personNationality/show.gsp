<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personNationality.entity', default: 'PersonNationality List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonNationality List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personNationality/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personNationality',action:'list')}'"/>
</div>
</body>
</html>