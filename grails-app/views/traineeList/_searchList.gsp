<el:formGroup>
    <el:textField name="code"
                  size="8"
                  class=""
                  label="${message(code: 'traineeList.code.label', default: 'code')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=""
                  label="${message(code: 'traineeList.name.label', default: 'name')}"/>
</el:formGroup>

<el:formGroup>
    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="8"
            class=""
            label="${message(code: 'traineeList.currentStatus.label', default: 'currentStatus')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="trackingInfo.dateCreatedUTC"
                  size="8" class=""
                  label="${message(code: 'traineeList.trackingInfo.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>
</el:formGroup>


<el:formGroup>
    <el:dateField name="currentStatus.fromDate"
                  size="8" class=""
                  label="${message(code: 'traineeList.currentStatusFromDate.label', default: 'fromDate')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="currentStatus.toDate"
                  size="8" class=""
                  label="${message(code: 'traineeList.currentStatusToDate.label', default: 'toDate')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="manualIncomeNo"
                  size="8" class=""
                  label="${message(code: 'traineeList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="manualOutgoingNo"
                  size="8" class=""
                  label="${message(code: 'traineeList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="numberOfCompetitorsValue"
                     size="8" class=""
                     label="${message(code: 'traineeList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>
</el:formGroup>

