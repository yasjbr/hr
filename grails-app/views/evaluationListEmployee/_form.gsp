
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeeEvaluation" action="autocomplete" name="employeeEvaluation.id" label="${message(code:'evaluationListEmployee.employeeEvaluation.label',default:'employeeEvaluation')}" values="${[[evaluationListEmployee?.employeeEvaluation?.id,evaluationListEmployee?.employeeEvaluation?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="evaluationList" action="autocomplete" name="evaluationList.id" label="${message(code:'evaluationListEmployee.evaluationList.label',default:'evaluationList')}" values="${[[evaluationListEmployee?.evaluationList?.id,evaluationListEmployee?.evaluationList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'evaluationListEmployee.firm.label',default:'firm')}" values="${[[evaluationListEmployee?.firm?.id,evaluationListEmployee?.firm?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'evaluationListEmployee.recordStatus.label',default:'recordStatus')}" value="${evaluationListEmployee?.recordStatus}" />
</el:formGroup>