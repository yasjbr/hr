<el:formGroup>
    <el:textField name="code" size="6"  class="" label="${message(code:'loanList.code.label',default:'code')}" />
    <el:textField name="name" size="6"  class="" label="${message(code:'loanList.name.label',default:'name')}" />
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="dateCreated"
              label="${message(code:'loanList.trackingInfo.dateCreatedUTC.label')}"  />

    <el:range type="date" size="6" name="sendDate"
              label="${message(code:'loanList.transientData.sendDate.label')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="manualOutgoingNo" size="6"  class="" label="${message(code:'loanList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <el:range type="date" size="6" name="receiveDate"
              label="${message(code:'loanList.transientData.receiveDate.label')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="manualIncomeNo" size="6"  class="" label="${message(code:'loanList.manualIncomeNo.label',default:'manualIncomeNo')}" />
    <el:integerField name="numberOfCompetitorsValue" size="6"
                     class="" label="${message(code:'loanList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
</el:formGroup>
<el:formGroup>
    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="6"
            class=""
            label="${message(code:'loanList.currentStatus.label',default:'currentStatus')}" />
</el:formGroup>