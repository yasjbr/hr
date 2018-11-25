<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employeeIdList"
                     label="${message(code: 'suspensionRequest.employee.label', default: 'employee')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRankIdList"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

</el:formGroup>


<el:formGroup>

    <el:select valueMessagePrefix="EnumSuspensionType"
               from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}" name="suspensionTypeList"
               size="6" class=""
               label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

    <el:range type="date" size="6" name="fromDateList"
              label="${message(code: 'suspensionRequest.fromDate.label')}"/>

</el:formGroup>



<el:formGroup>

    <el:range type="date" size="6" name="toDateList"
              label="${message(code: 'suspensionRequest.toDate.label')}"/>

    <el:integerField name="periodInMonthList" size="6" class=" isNumber"
                     label="${message(code: 'suspensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>

</el:formGroup>

<el:formGroup>

    <el:select valueMessagePrefix="EnumListRecordStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6" class=""
               label="${message(code: 'suspensionRequest.requestStatus.label', default: 'requestStatus')}"/>

</el:formGroup>