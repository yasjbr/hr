
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicant" action="autocomplete" name="applicant.id" label="${message(code:'recruitmentListEmployee.applicant.label',default:'applicant')}" values="${[[recruitmentListEmployee?.applicant?.id,recruitmentListEmployee?.applicant?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'recruitmentListEmployee.note.label',default:'note')}" value="${recruitmentListEmployee?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'recruitmentListEmployee.orderNo.label',default:'orderNo')}" value="${recruitmentListEmployee?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'recruitmentListEmployee.recordStatus.label',default:'recordStatus')}" value="${recruitmentListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="recruitmentList" action="autocomplete" name="recruitmentList.id" label="${message(code:'recruitmentListEmployee.recruitmentList.label',default:'recruitmentList')}" values="${[[recruitmentListEmployee?.recruitmentList?.id,recruitmentListEmployee?.recruitmentList?.descriptionInfo?.localName]]}" />
</el:formGroup>