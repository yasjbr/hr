
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicantInspectionResultListEmployee" action="autocomplete" name="applicantInspectionResultListEmployee.id" label="${message(code:'applicantInspectionResultListEmployeeNote.applicantInspectionResultListEmployee.label',default:'applicantInspectionResultListEmployee')}" values="${[[applicantInspectionResultListEmployeeNote?.applicantInspectionResultListEmployee?.id,applicantInspectionResultListEmployeeNote?.applicantInspectionResultListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'applicantInspectionResultListEmployeeNote.note.label',default:'note')}" value="${applicantInspectionResultListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'applicantInspectionResultListEmployeeNote.noteDate.label',default:'noteDate')}" value="${applicantInspectionResultListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'applicantInspectionResultListEmployeeNote.orderNo.label',default:'orderNo')}" value="${applicantInspectionResultListEmployeeNote?.orderNo}"/>
</el:formGroup>