<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeStatus.entity', default: 'EmployeeStatus List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmployeeStatus List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeStatus',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="employeeStatusForm" controller="employeeStatus" action="update">
                <g:render template="/employeeStatus/form" model="[employeeStatus:employeeStatus]"/>
                <el:hiddenField name="id" value="${employeeStatus?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>