
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'dispatchListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[dispatchListEmployee?.currentEmployeeMilitaryRank?.id,dispatchListEmployee?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'dispatchListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[dispatchListEmployee?.currentEmploymentRecord?.id,dispatchListEmployee?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="dispatchList" action="autocomplete" name="dispatchList.id" label="${message(code:'dispatchListEmployee.dispatchList.label',default:'dispatchList')}" values="${[[dispatchListEmployee?.dispatchList?.id,dispatchListEmployee?.dispatchList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumDispatchType"  from="${ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.values()}" name="dispatchType" size="8"  class=" isRequired" label="${message(code:'dispatchListEmployee.dispatchType.label',default:'dispatchType')}" value="${dispatchListEmployee?.dispatchType}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="educationMajorId" size="8"  class=" isNumber" label="${message(code:'dispatchListEmployee.educationMajorId.label',default:'educationMajorId')}" value="${dispatchListEmployee?.educationMajorId}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'dispatchListEmployee.employee.label',default:'employee')}" values="${[[dispatchListEmployee?.employee?.id,dispatchListEmployee?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'dispatchListEmployee.fromDate.label',default:'fromDate')}" value="${dispatchListEmployee?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="locationId" size="8"  class=" isNumber" label="${message(code:'dispatchListEmployee.locationId.label',default:'locationId')}" value="${dispatchListEmployee?.locationId}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="nextVerificationDate"  size="8" class=" isRequired" label="${message(code:'dispatchListEmployee.nextVerificationDate.label',default:'nextVerificationDate')}" value="${dispatchListEmployee?.nextVerificationDate}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="organizationId" size="8"  class=" isNumber" label="${message(code:'dispatchListEmployee.organizationId.label',default:'organizationId')}" value="${dispatchListEmployee?.organizationId}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonths" size="8"  class=" isNumber" label="${message(code:'dispatchListEmployee.periodInMonths.label',default:'periodInMonths')}" value="${dispatchListEmployee?.periodInMonths}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'dispatchListEmployee.recordStatus.label',default:'recordStatus')}" value="${dispatchListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'dispatchListEmployee.toDate.label',default:'toDate')}" value="${dispatchListEmployee?.toDate}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="unstructuredLocation" size="8"  class="" label="${message(code:'dispatchListEmployee.unstructuredLocation.label',default:'unstructuredLocation')}" value="${dispatchListEmployee?.unstructuredLocation}"/>
</el:formGroup>