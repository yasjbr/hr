<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PersonTrainingHistory List')}" />
    <title>${title}</title>
</head>
<body>


<g:render template="/pcore/person/personTrainingHistory/show" />

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'personTrainingHistory',action:'list')}'"/>
</div>
</body>
</html>