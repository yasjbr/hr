<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'vacationRequest List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>

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
            <el:validatableResetForm callBackGeneralFunction="successCallBack" name="vacationRequestForm"
                                     controller="vacationRequest" action="selectEmployee">
                <msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>

                <el:formGroup>
                    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                                     controller="vacationType" action="autocomplete" name="vacationType.id"
                                     label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"
                                     values=""/>
                </el:formGroup>

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  : 6]"/>

                <el:formButton functionName="select" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>

        </el:row>
    </lay:widgetBody>
</lay:widget>

</body>
</html>
