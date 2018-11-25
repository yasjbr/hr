<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeInternalAssignation.entity', default: 'EmployeeInternalAssignation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeInternalAssignation List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeInternalAssignation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeInternalAssignationForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeInternalAssignation" action="save">
                <g:render template="/employeeInternalAssignation/form" model="[employeeInternalAssignation:employeeInternalAssignation]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
