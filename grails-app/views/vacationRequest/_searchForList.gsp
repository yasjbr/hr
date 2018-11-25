<el:formGroup>
    <el:textField name="idList" size="6" class=" "
                     label="${message(code: 'vacationRequest.id.label', default: 'id')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employeeIdList"
                     label="${message(code: 'vacationRequest.employee.label', default: 'employee')}"/>
  </el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRankIdList"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacationType" action="autocomplete"
                     name="vacationTypeIdList"
                     label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>
</el:formGroup>

<el:formGroup>



    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'vacationRequest.fromDate.label')}"/>

    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'vacationRequest.toDate.label')}"/>


</el:formGroup>


<el:formGroup>

    <el:integerField name="numOfDaysList" size="6" class=" isNumber"
                     label="${message(code: 'vacationRequest.numOfDays.label', default: 'numOfDays')}"/>

    <el:checkboxField name="externalList" size="6" class=""
                      label="${message(code: 'vacationRequest.external.label', default: 'external')}"/>
</el:formGroup>


<el:hiddenField name="requestStatus" type="enum"
                value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>