
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'vacationListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'vacationListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class="" label="${message(code:'vacationListEmployee.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacationList" action="autocomplete" name="vacationList.id" label="${message(code:'vacationListEmployee.vacationList.label',default:'vacationList')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacationRequest" action="autocomplete" name="vacationRequest.id" label="${message(code:'vacationListEmployee.vacationRequest.label',default:'vacationRequest')}" />
</el:formGroup>
