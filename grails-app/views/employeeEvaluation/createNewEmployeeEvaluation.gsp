<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeEvaluation.entity', default: 'DispatchRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DispatchRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'employeeEvaluation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeEvaluationForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeEvaluation" action="save">
                <g:render template="/employeeEvaluation/form" model="[employeeEvaluation:employeeEvaluation]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" onClick="window.location.href='${createLink(controller: 'employeeEvaluation', action: 'create')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
