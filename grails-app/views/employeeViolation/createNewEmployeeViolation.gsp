<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'EmployeeViolation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeViolation List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'employeeViolation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeViolationForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeViolation" action="save">
                <g:render template="/employeeViolation/form" model="[employeeViolation:employeeViolation]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
