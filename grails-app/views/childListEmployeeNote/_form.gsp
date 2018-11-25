
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="childListEmployee" action="autocomplete" name="childListEmployee.id" label="${message(code:'childListEmployeeNote.childListEmployee.label',default:'childListEmployee')}" values="${[[childListEmployeeNote?.childListEmployee?.id,childListEmployeeNote?.childListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'childListEmployeeNote.note.label',default:'note')}" value="${childListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'childListEmployeeNote.noteDate.label',default:'noteDate')}" value="${childListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'childListEmployeeNote.orderNo.label',default:'orderNo')}" value="${childListEmployeeNote?.orderNo}"/>
</el:formGroup>