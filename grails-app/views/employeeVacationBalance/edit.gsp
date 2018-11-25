<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmployeeVacationBalance List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeeVacationBalance',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="employeeVacationBalanceForm" controller="employeeVacationBalance" action="update">
                <g:render template="/employeeVacationBalance/form" model="[employeeVacationBalance:employeeVacationBalance]"/>
                <el:hiddenField name="id" value="${employeeVacationBalance?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>