<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantEducation.entity', default: 'personEducation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'personEducation List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/applicantEducation/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personEducation',action:'list')}'"/>
</div>
</body>
</html>