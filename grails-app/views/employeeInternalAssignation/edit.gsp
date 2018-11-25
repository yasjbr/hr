<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeInternalAssignation.entity', default: 'EmployeeInternalAssignation List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmployeeInternalAssignation List')}" />
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
            <el:validatableForm name="employeeInternalAssignationForm" controller="employeeInternalAssignation" action="update">
                <g:render template="/employeeInternalAssignation/form" model="[employeeInternalAssignation:employeeInternalAssignation]"/>
                <el:hiddenField name="id" value="${employeeInternalAssignation?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>