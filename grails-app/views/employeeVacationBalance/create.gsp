<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance List')}"/>
    <g:set var="title"
           value="${message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance List')}"/>
    <title>${title}</title>

    <g:render template="script"/>

</head>

<body>
<lay:widget title="${message(code: 'employeeVacationBalance.vacationMechanism.label', default: 'vacationMechanism')}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'employeeVacationBalance', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>

    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <g:render template="/employeeVacationBalance/form"/>

        </el:row>
    </lay:widgetBody>
</lay:widget>

</body>
</html>
