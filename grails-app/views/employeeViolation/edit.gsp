<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'EmployeeViolation List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmployeeViolation List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'employeeViolation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="employeeViolationForm" controller="employeeViolation" action="update">
                <el:hiddenField name="id" value="${employeeViolation?.id}"/>
                <g:render template="form"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>