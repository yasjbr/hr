<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonLanguageInfo List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personLanguageInfo/show" />
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personLanguageInfo',action:'list')}'"/>
</div>
</body>
</html>