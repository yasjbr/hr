<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="inspectionCategory" action="autocomplete" name="inspectionCategory.id" label="${message(code:'applicantInspectionCategoryResult.inspectionCategory.label',default:'inspectionCategory')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="requestDate"  size="8" class="" label="${message(code:'applicantInspectionCategoryResult.requestDate.label',default:'requestDate')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="receiveDate"  size="8" class="" label="${message(code:'applicantInspectionCategoryResult.receiveDate.label',default:'receiveDate')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumInspectionResult" from="${ps.gov.epsilon.hr.enums.v1.EnumInspectionResult.values()}" name="inspectionResult" size="8"  class="" label="${message(code:'applicantInspectionCategoryResult.inspectionResult.label',default:'inspectionResult')}" />
</el:formGroup>


