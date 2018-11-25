<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'SuspensionRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'SuspensionRequest List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>

            <el:validatableResetForm callBackGeneralFunction="successCallBack" name="suspensionRequestForm"
                                     controller="suspensionRequest" action="selectEmployee">
                <msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>

                <el:formGroup>
                    <el:select valueMessagePrefix="EnumSuspensionType"
                               from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}"
                               name="suspensionType"
                               size="6" class=" isRequired"
                               label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

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
