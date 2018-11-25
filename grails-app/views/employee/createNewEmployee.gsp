<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Employee List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Employee List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'employee', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callLoadingFunction="performPostActionWithEncodedId" name="employeeForm" controller="employee" action="save">
                <g:render template="form"/>
                <el:row/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>