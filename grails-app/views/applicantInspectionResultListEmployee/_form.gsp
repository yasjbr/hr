
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicant" action="autocomplete" name="applicant.id" label="${message(code:'applicantInspectionResultListEmployee.applicant.label',default:'applicant')}" values="${[[applicantInspectionResultListEmployee?.applicant?.id,applicantInspectionResultListEmployee?.applicant?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicantInspectionResultList" action="autocomplete" name="applicantInspectionResultList.id" label="${message(code:'applicantInspectionResultListEmployee.applicantInspectionResultList.label',default:'applicantInspectionResultList')}" values="${[[applicantInspectionResultListEmployee?.applicantInspectionResultList?.id,applicantInspectionResultListEmployee?.applicantInspectionResultList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'applicantInspectionResultListEmployee.recordStatus.label',default:'recordStatus')}" value="${applicantInspectionResultListEmployee?.recordStatus}" />
</el:formGroup>