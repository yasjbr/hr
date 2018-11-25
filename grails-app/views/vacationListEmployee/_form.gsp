
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'vacationListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[vacationListEmployee?.currentEmployeeMilitaryRank?.id,vacationListEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'vacationListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[vacationListEmployee?.currentEmploymentRecord?.id,vacationListEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'vacationListEmployee.recordStatus.label',default:'recordStatus')}" value="${vacationListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="vacationList" action="autocomplete" name="vacationList.id" label="${message(code:'vacationListEmployee.vacationList.label',default:'vacationList')}" values="${[[vacationListEmployee?.vacationList?.id,vacationListEmployee?.vacationList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="vacationRequest" action="autocomplete" name="vacationRequest.id" label="${message(code:'vacationListEmployee.vacationRequest.label',default:'vacationRequest')}" values="${[[vacationListEmployee?.vacationRequest?.id,vacationListEmployee?.vacationRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>