<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'person.entity', default: 'Person List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Person List')}"/>
    <title>${title}</title>
</head>

<body>
<script>
    function performPostActionToMaritalStatusRequest(json) {
        %{--window.location.href = "${createLink(controller: 'maritalStatusRequest',action: 'createNewMaritalStatusRequest')}?employeeId=" + ${params.employeeId};--}%
        window.location.href = "${session['previousLink']}";
    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>

            <msg:page/>
            <el:validatableResetForm callBackFunction="performPostActionToMaritalStatusRequest" name="maritalStatusRequestForm" controller="maritalStatusRequest" action="saveNewPerson">
                <g:render template="/pcore/person/form"/>
                <el:row/>
                <el:formButton functionName="saveAndContinueButton" withPreviousLink="true" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>

