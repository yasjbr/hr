<el:formGroup>
    <el:textField name="code" size="6"  class="" label="${message(code:'violationList.code.label',default:'code')}" />
    <el:textField name="name" size="6"  class="" label="${message(code:'violationList.name.label',default:'name')}" />
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="dateCreated"
              label="${message(code:'violationList.dateCreated.label')}"  />

    <el:range type="date" size="6" name="sendDate"
              label="${message(code:'violationList.transientData.sendDate.label')}"  />
</el:formGroup>

<el:formGroup>
    <el:textField name="manualOutgoingNo" size="6"  class="" label="${message(code:'violationList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />

    <el:integerField name="numberOfCompetitorsValue" size="6"
                     class="" label="${message(code:'violationList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
</el:formGroup>

<el:formGroup>
    <el:select
            valueMessagePrefix="EnumCorrespondenceListStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.values()}"
            name="currentStatus.correspondenceListStatus"
            size="6"
            class=""
            label="${message(code:'dispatchList.currentStatus.label',default:'currentStatus')}" />
</el:formGroup>