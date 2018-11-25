
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'suspensionListEmployeeNote.note.label',default:'note')}" value="${suspensionListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'suspensionListEmployeeNote.noteDate.label',default:'noteDate')}" value="${suspensionListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'suspensionListEmployeeNote.orderNo.label',default:'orderNo')}" value="${suspensionListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionListEmployee" action="autocomplete" name="suspensionListEmployee.id" label="${message(code:'suspensionListEmployeeNote.suspensionListEmployee.label',default:'suspensionListEmployee')}" values="${[[suspensionListEmployeeNote?.suspensionListEmployee?.id,suspensionListEmployeeNote?.suspensionListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>