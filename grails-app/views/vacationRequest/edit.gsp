<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'vacationRequest List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>
    <g:render template="/request/workflowScript" model="[formName: 'vacationRequestForm']"/>


</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'vacationRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="vacationRequestForm" controller="vacationRequest" action="update">
                <g:render template="/vacationRequest/form" model="[vacationRequest: vacationRequest]"/>
                <el:hiddenField name="id" value="${vacationRequest?.id}"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="save" id="submitWithWorkflow"
                               onClick="submitWithWorkflowFunction();" withClose="true"
                               message="${message(code: 'workflow.btn.submitWithWorkflow.label', default: 'submitWithWorkflow')}"/>

                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>