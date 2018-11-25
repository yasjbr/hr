<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personEducation.entity', default: 'PersonEducation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonEducation List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personEducation/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personEducation',action:'list')}'"/>
</div>
</body>
</html>