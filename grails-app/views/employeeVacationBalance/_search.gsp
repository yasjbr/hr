<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employee.id"
                     label="${message(code: 'employeeVacationBalance.employee.label', default: 'employee')}"/>



    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacationType"
                     action="autocomplete" name="vacationType.id"
                     label="${message(code: 'vacationConfiguration.vacationType.descriptionInfo.localName.label', default: 'vacationType')}"/>



    <el:integerField name="annualBalance" size="6" class=" isNumber"
                     label="${message(code: 'employeeVacationBalance.annualBalance.label', default: 'annualBalance')}"/>

</el:formGroup>

<el:formGroup>

    <el:integerField name="balance" size="6" class=" isNumber"
                     label="${message(code: 'employeeVacationBalance.balance.label', default: 'balance')}"/>


    <el:integerField name="numberOfTimesUsed" size="6" class=" isNumber"
                     label="${message(code: 'employeeVacationBalance.numberOfTimesUsed.label', default: 'numberOfTimesUsed')}"/>

</el:formGroup>



<el:formGroup>

    <el:integerField name="oldTransferBalance" size="6" class=" isNumber"
                     label="${message(code: 'employeeVacationBalance.oldTransferBalance.label', default: 'oldTransferBalance')}"/>

    <el:range type="date" size="6" name="validFromDate"
              label="${message(code: 'employeeVacationBalance.validFromDate.label')}"/>

</el:formGroup>



<el:formGroup>

    <el:range type="date" size="6" name="validToDate"
              label="${message(code: 'employeeVacationBalance.validToDate.label')}"/>
    <el:integerField name="vacationDueYear" size="6" class=" isNumber" value="${java.time.ZonedDateTime.now().year}"
                     label="${message(code: 'employeeVacationBalance.vacationDueYear.label', default: 'vacationDueYear')}"/>

</el:formGroup>

<el:formGroup>
    <el:select
            label="${message(code: 'employeeVacationBalance.isCurrent.label', default: 'isCurrent')}"
            name="isCurrent"
            size="6"
            class="" from="['true', 'false', 'all']" valueMessagePrefix="select"
            placeholder="${message(code: 'default.select.label', default: 'please select')}"/>

</el:formGroup>