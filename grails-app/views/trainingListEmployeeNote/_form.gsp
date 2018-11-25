
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'trainingListEmployeeNote.note.label',default:'note')}" value="${trainingListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'trainingListEmployeeNote.noteDate.label',default:'noteDate')}" value="${trainingListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'trainingListEmployeeNote.orderNo.label',default:'orderNo')}" value="${trainingListEmployeeNote?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="traineeListEmployee" action="autocomplete" name="traineeListEmployee.id" label="${message(code:'trainingListEmployeeNote.traineeListEmployee.label',default:'traineeListEmployee')}" values="${[[trainingListEmployeeNote?.traineeListEmployee?.id,trainingListEmployeeNote?.traineeListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>