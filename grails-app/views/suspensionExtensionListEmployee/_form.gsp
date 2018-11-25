
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'suspensionExtensionListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[suspensionExtensionListEmployee?.currentEmployeeMilitaryRank?.id,suspensionExtensionListEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'suspensionExtensionListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[suspensionExtensionListEmployee?.currentEmploymentRecord?.id,suspensionExtensionListEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="effectiveDate"  size="8" class=" isRequired" label="${message(code:'suspensionExtensionListEmployee.effectiveDate.label',default:'effectiveDate')}" value="${suspensionExtensionListEmployee?.effectiveDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'suspensionExtensionListEmployee.fromDate.label',default:'fromDate')}" value="${suspensionExtensionListEmployee?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonth" size="8"  class=" isRequired isNumber" label="${message(code:'suspensionExtensionListEmployee.periodInMonth.label',default:'periodInMonth')}" value="${suspensionExtensionListEmployee?.periodInMonth}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'suspensionExtensionListEmployee.recordStatus.label',default:'recordStatus')}" value="${suspensionExtensionListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionExtensionList" action="autocomplete" name="suspensionExtensionList.id" label="${message(code:'suspensionExtensionListEmployee.suspensionExtensionList.label',default:'suspensionExtensionList')}" values="${[[suspensionExtensionListEmployee?.suspensionExtensionList?.id,suspensionExtensionListEmployee?.suspensionExtensionList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionExtensionRequest" action="autocomplete" name="suspensionExtensionRequest.id" label="${message(code:'suspensionExtensionListEmployee.suspensionExtensionRequest.label',default:'suspensionExtensionRequest')}" values="${[[suspensionExtensionListEmployee?.suspensionExtensionRequest?.id,suspensionExtensionListEmployee?.suspensionExtensionRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'suspensionExtensionListEmployee.toDate.label',default:'toDate')}" value="${suspensionExtensionListEmployee?.toDate}" />
</el:formGroup>