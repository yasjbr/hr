<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeExternalAssignation List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeExternalAssignation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employeeExternalAssignationForm" callLoadingFunction="performPostActionWithEncodedId" controller="employeeExternalAssignation" action="save">
                <g:render template="/employeeExternalAssignation/form" model="[employeeExternalAssignation:employeeExternalAssignation]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
