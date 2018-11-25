<el:formGroup>
    <el:textField name="code" size="6"  class="" label="${message(code:'dispatchList.code.label',default:'code')}" />
    <el:textField name="name" size="6"  class="" label="${message(code:'dispatchList.name.label',default:'name')}" />
</el:formGroup>

<el:formGroup>

    <el:range type="date" size="6" name="dateCreated"
              label="${message(code:'dispatchList.trackingInfo.dateCreatedUTC.label')}"  />

</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="sendDate"
              label="${message(code:'dispatchList.transientData.sendDate.label')}"  />

    <el:textField name="manualOutgoingNo" size="6"  class="" label="${message(code:'dispatchList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="receiveDate"
              label="${message(code:'dispatchList.transientData.receiveDate.label')}"  />

    <el:textField name="manualIncomeNo" size="6"  class="" label="${message(code:'dispatchList.manualIncomeNo.label',default:'manualIncomeNo')}" />
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfCompetitorsValue" size="6"
                     class="" label="${message(code:'dispatchList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />

    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="6"
            class=""
            label="${message(code:'dispatchList.currentStatus.label',default:'currentStatus')}" />
</el:formGroup>