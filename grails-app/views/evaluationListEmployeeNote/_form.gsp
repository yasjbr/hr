
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="evaluationListEmployee" action="autocomplete" name="evaluationListEmployee.id" label="${message(code:'evaluationListEmployeeNote.evaluationListEmployee.label',default:'evaluationListEmployee')}" values="${[[evaluationListEmployeeNote?.evaluationListEmployee?.id,evaluationListEmployeeNote?.evaluationListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'evaluationListEmployeeNote.note.label',default:'note')}" value="${evaluationListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'evaluationListEmployeeNote.noteDate.label',default:'noteDate')}" value="${evaluationListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'evaluationListEmployeeNote.orderNo.label',default:'orderNo')}" value="${evaluationListEmployeeNote?.orderNo}"/>
</el:formGroup>