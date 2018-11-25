<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'vacationRequest List')}"/>
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
            <el:validatableResetForm name="vacationRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="vacationRequest" action="save">
                <g:render template="/vacationRequest/form" model="[vacationRequest: vacationRequest]"/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true"/>

                <el:formButton functionName="save" id="submitWithWorkflow"
                               onClick="submitWithWorkflowFunction();" withClose="true"
                               message="${message(code: 'workflow.btn.submitWithWorkflow.label', default: 'submitWithWorkflow')}"/>

                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'vacationRequest', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
