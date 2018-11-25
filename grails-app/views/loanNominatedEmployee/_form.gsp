
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'loanNominatedEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[loanNominatedEmployee?.currentEmployeeMilitaryRank?.id,loanNominatedEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'loanNominatedEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[loanNominatedEmployee?.currentEmploymentRecord?.id,loanNominatedEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="effectiveDate"  size="8" class=" isRequired" label="${message(code:'loanNominatedEmployee.effectiveDate.label',default:'effectiveDate')}" value="${loanNominatedEmployee?.effectiveDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'loanNominatedEmployee.employee.label',default:'employee')}" values="${[[loanNominatedEmployee?.employee?.id,loanNominatedEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'loanNominatedEmployee.fromDate.label',default:'fromDate')}" value="${loanNominatedEmployee?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanNoticeReplayList" action="autocomplete" name="loanNoticeReplayList.id" label="${message(code:'loanNominatedEmployee.loanNoticeReplayList.label',default:'loanNoticeReplayList')}" values="${[[loanNominatedEmployee?.loanNoticeReplayList?.id,loanNominatedEmployee?.loanNoticeReplayList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="loanNoticeReplayRequest" action="autocomplete" name="loanNoticeReplayRequest.id" label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.label',default:'loanNoticeReplayRequest')}" values="${[[loanNominatedEmployee?.loanNoticeReplayRequest?.id,loanNominatedEmployee?.loanNoticeReplayRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'loanNominatedEmployee.orderNo.label',default:'orderNo')}" value="${loanNominatedEmployee?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonth" size="8"  class=" isRequired isNumber" label="${message(code:'loanNominatedEmployee.periodInMonth.label',default:'periodInMonth')}" value="${loanNominatedEmployee?.periodInMonth}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'loanNominatedEmployee.recordStatus.label',default:'recordStatus')}" value="${loanNominatedEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'loanNominatedEmployee.toDate.label',default:'toDate')}" value="${loanNominatedEmployee?.toDate}" />
</el:formGroup>