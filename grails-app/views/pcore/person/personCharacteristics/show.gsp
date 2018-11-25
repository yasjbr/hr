<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonCharacteristics List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personCharacteristics/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personCharacteristics',action:'list')}'"/>
</div>
</body>
</html>