<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationListEmployeeNote.entity', default: 'EvaluationListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationListEmployeeNote?.evaluationListEmployee}" type="EvaluationListEmployee" label="${message(code:'evaluationListEmployeeNote.evaluationListEmployee.label',default:'evaluationListEmployee')}" />
    <lay:showElement value="${evaluationListEmployeeNote?.note}" type="String" label="${message(code:'evaluationListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${evaluationListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'evaluationListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${evaluationListEmployeeNote?.orderNo}" type="String" label="${message(code:'evaluationListEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>