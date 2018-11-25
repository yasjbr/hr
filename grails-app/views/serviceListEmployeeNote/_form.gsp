
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'serviceListEmployeeNote.note.label',default:'note')}" value="${serviceListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'serviceListEmployeeNote.noteDate.label',default:'noteDate')}" value="${serviceListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'serviceListEmployeeNote.orderNo.label',default:'orderNo')}" value="${serviceListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="serviceListEmployee" action="autocomplete" name="serviceListEmployee.id" label="${message(code:'serviceListEmployeeNote.serviceListEmployee.label',default:'serviceListEmployee')}" values="${[[serviceListEmployeeNote?.serviceListEmployee?.id,serviceListEmployeeNote?.serviceListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>