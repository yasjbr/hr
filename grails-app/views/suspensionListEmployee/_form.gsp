
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'suspensionListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[suspensionListEmployee?.currentEmployeeMilitaryRank?.id,suspensionListEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'suspensionListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[suspensionListEmployee?.currentEmploymentRecord?.id,suspensionListEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="effectiveDate"  size="8" class=" isRequired" label="${message(code:'suspensionListEmployee.effectiveDate.label',default:'effectiveDate')}" value="${suspensionListEmployee?.effectiveDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'suspensionListEmployee.employee.label',default:'employee')}" values="${[[suspensionListEmployee?.employee?.id,suspensionListEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'suspensionListEmployee.fromDate.label',default:'fromDate')}" value="${suspensionListEmployee?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonth" size="8"  class=" isRequired isNumber" label="${message(code:'suspensionListEmployee.periodInMonth.label',default:'periodInMonth')}" value="${suspensionListEmployee?.periodInMonth}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'suspensionListEmployee.recordStatus.label',default:'recordStatus')}" value="${suspensionListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionList" action="autocomplete" name="suspensionList.id" label="${message(code:'suspensionListEmployee.suspensionList.label',default:'suspensionList')}" values="${[[suspensionListEmployee?.suspensionList?.id,suspensionListEmployee?.suspensionList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionRequest" action="autocomplete" name="suspensionRequest.id" label="${message(code:'suspensionListEmployee.suspensionRequest.label',default:'suspensionRequest')}" values="${[[suspensionListEmployee?.suspensionRequest?.id,suspensionListEmployee?.suspensionRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumSuspensionType"  from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}" name="suspensionType" size="8"  class=" isRequired" label="${message(code:'suspensionListEmployee.suspensionType.label',default:'suspensionType')}" value="${suspensionListEmployee?.suspensionType}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'suspensionListEmployee.toDate.label',default:'toDate')}" value="${suspensionListEmployee?.toDate}" />
</el:formGroup>