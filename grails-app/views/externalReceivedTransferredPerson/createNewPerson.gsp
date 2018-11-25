<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'person.entity', default: 'Person List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Person List')}"/>
    <title>${title}</title>
    <script>
        function performPostActionToEmployee(json) {
            if (json.success) {
                window.location.href = "${createLink(controller: 'externalReceivedTransferredPerson',action: 'createNewExternalReceived')}?personId=" + json.data.id;
            }
        }
    </script>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'externalReceivedTransferredPerson', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>

            <msg:page/>
            <el:validatableResetForm callBackFunction="performPostActionToEmployee" name="externalReceivedTransferredPersonForm"
                                     controller="externalReceivedTransferredPerson" action="saveNewPerson">
                <g:render template="/pcore/person/form"/>
                <el:row/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>

