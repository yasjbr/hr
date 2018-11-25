<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonHealthHistory List')}" />
    <title>${title}</title>
</head>
<body>


<g:render template="/pcore/person/personHealthHistory/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personHealthHistory',action:'list')}'"/>
</div>
</body>
</html>