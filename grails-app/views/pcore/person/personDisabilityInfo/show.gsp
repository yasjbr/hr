<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonDisabilityInfo List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personDisabilityInfo/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personDisabilityInfo',action:'list')}'"/>
</div>
</body>
</html>