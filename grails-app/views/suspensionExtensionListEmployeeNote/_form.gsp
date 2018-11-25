
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'suspensionExtensionListEmployeeNote.note.label',default:'note')}" value="${suspensionExtensionListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'suspensionExtensionListEmployeeNote.noteDate.label',default:'noteDate')}" value="${suspensionExtensionListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'suspensionExtensionListEmployeeNote.orderNo.label',default:'orderNo')}" value="${suspensionExtensionListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="suspensionExtensionListEmployee" action="autocomplete" name="suspensionExtinsionListEmployee.id" label="${message(code:'suspensionExtensionListEmployeeNote.suspensionExtinsionListEmployee.label',default:'suspensionExtinsionListEmployee')}" values="${[[suspensionExtensionListEmployeeNote?.suspensionExtinsionListEmployee?.id,suspensionExtensionListEmployeeNote?.suspensionExtinsionListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>