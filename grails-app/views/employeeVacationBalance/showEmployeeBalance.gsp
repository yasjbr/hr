<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance ')}"/>
    <g:set var="title"
           value="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance ')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<lay:widget
        title="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance')}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'employeeVacationBalance', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>

    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <form id="employeeForm">
                <el:formGroup>
                    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" "
                                     controller="employee" paramsGenerateFunction="employeeParams"
                                     action="autocomplete" name="employee.id" id="employeeId"
                                     label="${message(code: 'employeeVacationBalance.employee.label', default: 'employee')}"
                                     values="${[[employeeVacationBalance?.employee?.id, employeeVacationBalance?.employee?.descriptionInfo?.localName]]}"/>

                </el:formGroup>
                <el:row/>
                <el:formGroup>
                    <el:integerField name="vacationDueYear" size="8" class=" isNumber"
                                     value="${java.time.ZonedDateTime.now().year}"
                                     label="${message(code: 'employeeVacationBalance.vacationDueYear.label', default: 'vacationDueYear')}"/>

                </el:formGroup>

                <el:row/>
            </form>




            <el:row/>
            <el:formGroup style="margin-top:1%;margin-right: 40%">
                <btn:button size="big" isSubmit="true" color="yellow"
                            onClick="showEmployeeBalance();"
                            message="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance')}"
                            messageCode="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance')}"/>
            </el:formGroup>
            <el:row/>
            <el:row/>
            <div id="employeeVacationConfigurationDiv" style="display: none;">
                <msg:info/>
                <lay:table styleNumber="1" id="employeeVacationsTable">
                    <lay:tableHead title="${message(code: '')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.vacationConfiguration.vacationType.descriptionInfo.localName.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.balance.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.validFromDate.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.validToDate.label')}"/>
                    <lay:tableHead title="${message(code: 'default.action.label')}"/>
                </lay:table>
            </div>

        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>