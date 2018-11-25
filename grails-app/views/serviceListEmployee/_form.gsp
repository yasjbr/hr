
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'serviceListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[serviceListEmployee?.currentEmployeeMilitaryRank?.id,serviceListEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'serviceListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[serviceListEmployee?.currentEmploymentRecord?.id,serviceListEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="dateEffective"  size="8" class=" isRequired" label="${message(code:'serviceListEmployee.dateEffective.label',default:'dateEffective')}" value="${serviceListEmployee?.dateEffective}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'serviceListEmployee.employee.label',default:'employee')}" values="${[[serviceListEmployee?.employee?.id,serviceListEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentServiceRequest" action="autocomplete" name="employmentServiceRequest.id" label="${message(code:'serviceListEmployee.employmentServiceRequest.label',default:'employmentServiceRequest')}" values="${[[serviceListEmployee?.employmentServiceRequest?.id,serviceListEmployee?.employmentServiceRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'serviceListEmployee.recordStatus.label',default:'recordStatus')}" value="${serviceListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="serviceActionReason" action="autocomplete" name="serviceActionReason.id" label="${message(code:'serviceListEmployee.serviceActionReason.label',default:'serviceActionReason')}" values="${[[serviceListEmployee?.serviceActionReason?.id,serviceListEmployee?.serviceActionReason?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="serviceList" action="autocomplete" name="serviceList.id" label="${message(code:'serviceListEmployee.serviceList.label',default:'serviceList')}" values="${[[serviceListEmployee?.serviceList?.id,serviceListEmployee?.serviceList?.descriptionInfo?.localName]]}" />
</el:formGroup>