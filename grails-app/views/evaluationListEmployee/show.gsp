<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationListEmployee?.employeeEvaluation}" type="EmployeeEvaluation" label="${message(code:'evaluationListEmployee.employeeEvaluation.label',default:'employeeEvaluation')}" />
    <lay:showElement value="${evaluationListEmployee?.evaluationList}" type="EvaluationList" label="${message(code:'evaluationListEmployee.evaluationList.label',default:'evaluationList')}" />
    <lay:showElement value="${evaluationListEmployee?.evaluationListEmployeeNotes}" type="Set" label="${message(code:'evaluationListEmployee.evaluationListEmployeeNotes.label',default:'evaluationListEmployeeNotes')}" />
    <lay:showElement value="${evaluationListEmployee?.firm}" type="Firm" label="${message(code:'evaluationListEmployee.firm.label',default:'firm')}" />
    <lay:showElement value="${evaluationListEmployee?.recordStatus}" type="enum" label="${message(code:'evaluationListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
</lay:showWidget>
<el:row />

</body>
</html>