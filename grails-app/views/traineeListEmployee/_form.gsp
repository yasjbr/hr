
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicant" action="autocomplete" name="applicant.id" label="${message(code:'traineeListEmployee.applicant.label',default:'applicant')}" values="${[[traineeListEmployee?.applicant?.id,traineeListEmployee?.applicant?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'traineeListEmployee.note.label',default:'note')}" value="${traineeListEmployee?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'traineeListEmployee.orderNo.label',default:'orderNo')}" value="${traineeListEmployee?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'traineeListEmployee.recordStatus.label',default:'recordStatus')}" value="${traineeListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="traineeList" action="autocomplete" name="traineeList.id" label="${message(code:'traineeListEmployee.traineeList.label',default:'traineeList')}" values="${[[traineeListEmployee?.traineeList?.id,traineeListEmployee?.traineeList?.descriptionInfo?.localName]]}" />
</el:formGroup>