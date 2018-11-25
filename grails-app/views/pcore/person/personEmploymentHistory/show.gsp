<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonEmploymentHistory List')}" />
    <title>${title}</title>
</head>
<body>


<g:render template="/pcore/person/personEmploymentHistory/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personEmploymentHistory',action:'list')}'"/>
</div>
</body>
</html>