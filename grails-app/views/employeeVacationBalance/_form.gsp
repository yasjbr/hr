<lay:widget title="${message(code: 'employeeVacationBalance.vacationMechanismType.label', default: 'employee')}">
    <lay:widgetBody>

        <form id="mechanismForm">

            <msg:warning label="${message(code:'employeeVacationBalance.warining.calculate.balance.message')}" />



            <el:formGroup>
                <el:select valueMessagePrefix="enumBalanceCalculationMechanism" onchange="selectChanged(this)"
                           from="${ps.gov.epsilon.hr.enums.vacation.v1.EnumBalanceCalculationMechanism.values()}"
                           name="Mechanism" size="8" class=""
                           label="${message(code: 'employeeVacationBalance.vacationMechanismType.label', default: 'vacationMechanismType')}"/>
            </el:formGroup>

            <el:formGroup>
                <el:integerField size="8"
                                 label="${message(code: 'employeeVacationBalance.year.label', default: 'year')}"
                                 maxLength="4" id="year" name="year"
                                 min="${employeeVacationBalance.transientData.currentYear - 3}"
                                 max="${employeeVacationBalance.transientData.currentYear + 3}"
                                 value="${employeeVacationBalance.transientData.currentYear}"/>
            </el:formGroup>


            <el:formGroup>
                <el:checkboxField
                        label="${message(code: 'employeeVacationBalance.recalculate.label', default: 'recalculate')}"
                        size="8" name="recalculate"/>
            </el:formGroup>
        </form>


        <el:row/>
    </lay:widgetBody>
</lay:widget>


<div id="employeeDiv" style="display: none;">
    <lay:widget title="${message(code: 'employeeVacationBalance.employee.label', default: 'employee')}">
        <lay:widgetBody>
            <msg:page/>

            <form id="employeeForm">
                <el:formGroup>
                    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                                     controller="employee" paramsGenerateFunction="employeeParams"
                                     action="autocomplete" name="employee.id" id="employeeId"
                                     label="${message(code: 'employeeVacationBalance.employee.label', default: 'employee')}"
                                     values="${[[employeeVacationBalance?.employee?.id, employeeVacationBalance?.employee?.descriptionInfo?.localName]]}"/>


                </el:formGroup>
                <el:row/>
            </form>
            <el:row/>
            <el:formGroup style="margin-top:1%;margin-right: 40%">
                <btn:button size="big" isSubmit="true" color="yellow" onClick="showEmployeeVacationsBalance();"
                            message="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance')}"
                            messageCode="${message(code: 'employeeVacationBalance.showEmployeeBalance.label', default: 'showEmployeeBalance')}"/>
                <btn:button id="calculateVacationsBalanceBtn" size="big" isSubmit="true" color="grey" onClick="calculateEmployeeYearlyBalance();"
                            message="${message(code: 'employeeVacationBalance.calculateEmployeeBalance.label', default: 'calculateEmployeeBalance')}"
                            messageCode="${message(code: 'employeeVacationBalance.calculateEmployeeBalance.label', default: 'calculateEmployeeBalance')}"/>
            </el:formGroup>
            <el:row/>
            <el:row/>
            <div id="employeeVacationConfigurationDiv" style="display: none;">
                <msg:info/>
                <lay:table styleNumber="1" id="employeeVacationsTable">
                    <lay:tableHead title="${message(code: ' ')}"/>
                    <lay:tableHead
                            title="${message(code: 'employeeVacationBalance.vacationConfiguration.vacationType.descriptionInfo.localName.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.balance.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.validFromDate.label')}"/>
                    <lay:tableHead title="${message(code: 'employeeVacationBalance.validToDate.label')}"/>
                </lay:table>
            </div>
        </lay:widgetBody>
    </lay:widget>
</div>


<div id="allEmployeeDiv" style="display: none;">
    <lay:widget
            title="${message(code: 'employeeVacationBalance.allEmployee.label', default: 'allEmployeesSelection')}">
        <lay:widgetBody>
            <msg:info
                    label="${message(code: 'employeeVacationBalance.allEmployeesSelection.label', default: 'allEmployee')}"/>
            <el:formGroup style="margin-top:1%;margin-right: 40%">
                <btn:button size="big" isSubmit="true" color="grey" onClick="calculateAllEmployeeYearlyBalance();"
                            message="${message(code: 'employeeVacationBalance.calculateEmployeeBalance.label', default: 'calculateEmployeeBalance')}"
                            messageCode="${message(code: 'employeeVacationBalance.calculateEmployeeBalance.label', default: 'calculateEmployeeBalance')}"/>
            </el:formGroup>

            <el:row/>
        </lay:widgetBody>
    </lay:widget>
</div>





