
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'petitionListEmployeeNote.note.label',default:'note')}" value="${petitionListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'petitionListEmployeeNote.noteDate.label',default:'noteDate')}" value="${petitionListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'petitionListEmployeeNote.orderNo.label',default:'orderNo')}" value="${petitionListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="petitionListEmployee" action="autocomplete" name="petitionListEmployee.id" label="${message(code:'petitionListEmployeeNote.petitionListEmployee.label',default:'petitionListEmployee')}" values="${[[petitionListEmployeeNote?.petitionListEmployee?.id,petitionListEmployeeNote?.petitionListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>