<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'TrainingListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'trainingListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${trainingListEmployeeNote?.note}" type="String" label="${message(code:'trainingListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${trainingListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'trainingListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${trainingListEmployeeNote?.orderNo}" type="String" label="${message(code:'trainingListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${trainingListEmployeeNote?.traineeListEmployee}" type="TraineeListEmployee" label="${message(code:'trainingListEmployeeNote.traineeListEmployee.label',default:'traineeListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>