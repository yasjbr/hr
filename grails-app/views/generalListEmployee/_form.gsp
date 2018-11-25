
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'generalListEmployee.employee.label',default:'employee')}" values="${[[generalListEmployee?.employee?.id,generalListEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="employeeMilitaryRank.id" label="${message(code:'generalListEmployee.employeeMilitaryRank.label',default:'employeeMilitaryRank')}" values="${[[generalListEmployee?.employeeMilitaryRank?.id,generalListEmployee?.employeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="generalList" action="autocomplete" name="generalList.id" label="${message(code:'generalListEmployee.generalList.label',default:'generalList')}" values="${[[generalListEmployee?.generalList?.id,generalListEmployee?.generalList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'generalListEmployee.recordStatus.label',default:'recordStatus')}" value="${generalListEmployee?.recordStatus}" />
</el:formGroup>