<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingRecord.entity', default: 'TrainingRecord List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'TrainingRecord List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'trainingRecord',action:'list')}'"/>
    </div></div>
</div>
<g:render template="/trainingRecord/show" model="[title:title,trainingRecord:trainingRecord]" />
<el:row />

</body>
</html>