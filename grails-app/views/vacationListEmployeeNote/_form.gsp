
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'vacationListEmployeeNote.note.label',default:'note')}" value="${vacationListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'vacationListEmployeeNote.noteDate.label',default:'noteDate')}" value="${vacationListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'vacationListEmployeeNote.orderNo.label',default:'orderNo')}" value="${vacationListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="vacationListEmployee" action="autocomplete" name="vacationListEmployee.id" label="${message(code:'vacationListEmployeeNote.vacationListEmployee.label',default:'vacationListEmployee')}" values="${[[vacationListEmployeeNote?.vacationListEmployee?.id,vacationListEmployeeNote?.vacationListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>