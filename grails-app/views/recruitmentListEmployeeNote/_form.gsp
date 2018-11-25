
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'recruitmentListEmployeeNote.note.label',default:'note')}" value="${recruitmentListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'recruitmentListEmployeeNote.noteDate.label',default:'noteDate')}" value="${recruitmentListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'recruitmentListEmployeeNote.orderNo.label',default:'orderNo')}" value="${recruitmentListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="recruitmentListEmployee" action="autocomplete" name="recruitmentListEmployee.id" label="${message(code:'recruitmentListEmployeeNote.recruitmentListEmployee.label',default:'recruitmentListEmployee')}" values="${[[recruitmentListEmployeeNote?.recruitmentListEmployee?.id,recruitmentListEmployeeNote?.recruitmentListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>