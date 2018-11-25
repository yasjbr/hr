<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonLiveStatus List')}" />
    <title>${title}</title>
</head>
<body>


<g:render template="/pcore/person/personLiveStatus/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personLiveStatus',action:'list')}'"/>
</div>
</body>
</html>