<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonArrestHistory List')}" />
    <title>${title}</title>
</head>
<body>

<g:render template="/pcore/person/personArrestHistory/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>
</body>
</html>