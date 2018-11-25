<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonMaritalStatus List')}" />
    <title>${title}</title>
</head>
<body>


<g:render template="/pcore/person/personMaritalStatus/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personMaritalStatus',action:'list')}'"/>
</div>
</body>
</html>