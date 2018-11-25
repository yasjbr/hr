
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'returnFromAbsenceListEmployeeNote.note.label',default:'note')}" value="${returnFromAbsenceListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'returnFromAbsenceListEmployeeNote.noteDate.label',default:'noteDate')}" value="${returnFromAbsenceListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'returnFromAbsenceListEmployeeNote.orderNo.label',default:'orderNo')}" value="${returnFromAbsenceListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="returnFromAbsenceListEmployee" action="autocomplete" name="returnFromAbsenceListEmployee.id" label="${message(code:'returnFromAbsenceListEmployeeNote.returnFromAbsenceListEmployee.label',default:'returnFromAbsenceListEmployee')}" values="${[[returnFromAbsenceListEmployeeNote?.returnFromAbsenceListEmployee?.id,returnFromAbsenceListEmployeeNote?.returnFromAbsenceListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>