<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainer.entity', default: 'Trainer List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Trainer List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'trainer',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${trainer?.employeeId}" type="Long" label="${message(code:'trainer.employeeId.label',default:'employeeId')}" />
    <lay:showElement value="${trainer?.note}" type="String" label="${message(code:'trainer.note.label',default:'note')}" />
    <lay:showElement value="${trainer?.personId}" type="Long" label="${message(code:'trainer.personId.label',default:'personId')}" />
</lay:showWidget>
<el:row />

</body>
</html>