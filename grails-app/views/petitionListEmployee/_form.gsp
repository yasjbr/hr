
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="petitionList" action="autocomplete" name="petitionList.id" label="${message(code:'petitionListEmployee.petitionList.label',default:'petitionList')}" values="${[[petitionListEmployee?.petitionList?.id,petitionListEmployee?.petitionList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="petitionRequest" action="autocomplete" name="petitionRequest.id" label="${message(code:'petitionListEmployee.petitionRequest.label',default:'petitionRequest')}" values="${[[petitionListEmployee?.petitionRequest?.id,petitionListEmployee?.petitionRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'petitionListEmployee.recordStatus.label',default:'recordStatus')}" value="${petitionListEmployee?.recordStatus}" />
</el:formGroup>