
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="generalListEmployee" action="autocomplete" name="generalListEmployee.id" label="${message(code:'generalListEmployeeNote.generalListEmployee.label',default:'generalListEmployee')}" values="${[[generalListEmployeeNote?.generalListEmployee?.id,generalListEmployeeNote?.generalListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'generalListEmployeeNote.note.label',default:'note')}" value="${generalListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'generalListEmployeeNote.noteDate.label',default:'noteDate')}" value="${generalListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'generalListEmployeeNote.orderNo.label',default:'orderNo')}" value="${generalListEmployeeNote?.orderNo}"/>
</el:formGroup>