<el:formGroup>
    <el:textField name="code"
                  size="6"
                  class=""
                  label="${message(code: 'vacationList.code.label', default: 'code')}"/>
    <el:textField name="name"
                  size="6"
                  class=""
                  label="${message(code: 'vacationList.name.label', default: 'name')}"/>
</el:formGroup>

<el:formGroup>

    <el:range type="date" size="6" name="dateCreated"
              label="${message(code: 'list.dateCreated.label')}"/>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'vacationList.fromDate.label')}"/>

</el:formGroup>

<el:formGroup>
    <el:textField name="manualOutgoingNo" size="6" class=""
                  label="${message(code: 'vacationList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>



    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'vacationList.toDate.label')}"/>

</el:formGroup>


<el:formGroup>
    <el:textField name="manualIncomeNo" size="6" class=""
                  label="${message(code: 'vacationList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>

   <el:integerField name="numberOfCompetitorsValue" size="6" class=""
                  label="${message(code: 'vacationList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>



</el:formGroup>

<el:formGroup>
    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="6"
            class=""
            label="${message(code: 'vacationList.currentStatus.label', default: 'currentStatus')}"/>
</el:formGroup>