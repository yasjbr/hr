
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="allowanceListEmployee" action="autocomplete" name="allowanceListEmployee.id" label="${message(code:'allowanceListEmployeeNote.allowanceListEmployee.label',default:'allowanceListEmployee')}" values="${[[allowanceListEmployeeNote?.allowanceListEmployee?.id,allowanceListEmployeeNote?.allowanceListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'allowanceListEmployeeNote.note.label',default:'note')}" value="${allowanceListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'allowanceListEmployeeNote.noteDate.label',default:'noteDate')}" value="${allowanceListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'allowanceListEmployeeNote.orderNo.label',default:'orderNo')}" value="${allowanceListEmployeeNote?.orderNo}"/>
</el:formGroup>