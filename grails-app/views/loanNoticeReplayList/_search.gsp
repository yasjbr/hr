<el:formGroup>
    <el:textField name="code" size="6"  class="" label="${message(code:'loanNoticeReplayList.code.label',default:'code')}" />
    <el:textField name="name" size="6"  class="" label="${message(code:'loanNoticeReplayList.name.label',default:'name')}" />
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="dateCreated"
              label="${message(code:'loanNoticeReplayList.trackingInfo.dateCreatedUTC.label')}"  />

    <el:range type="date" size="6" name="sendDate"
              label="${message(code:'loanNoticeReplayList.transientData.sendDate.label')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="manualOutgoingNo" size="6"  class="" label="${message(code:'loanNoticeReplayList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <el:range type="date" size="6" name="receiveDate"
              label="${message(code:'loanNoticeReplayList.transientData.receiveDate.label')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="manualIncomeNo" size="6"  class="" label="${message(code:'loanNoticeReplayList.manualIncomeNo.label',default:'manualIncomeNo')}" />
    <el:integerField name="numberOfCompetitorsValue" size="6"
                     class="" label="${message(code:'loanNoticeReplayList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
</el:formGroup>
<el:formGroup>
    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="6"
            class=""
            label="${message(code:'loanNoticeReplayList.currentStatus.label',default:'currentStatus')}" />
</el:formGroup>