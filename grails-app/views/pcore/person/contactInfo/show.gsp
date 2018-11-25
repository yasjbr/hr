<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'ContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<g:render template="/pcore/person/contactInfo/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'contactInfo',action:'list')}'"/>
</div>
</body>
</html>