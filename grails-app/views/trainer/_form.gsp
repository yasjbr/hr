
<el:formGroup>
    <el:textField name="employeeId" size="8"  class=" isNumber" label="${message(code:'trainer.employeeId.label',default:'employeeId')}" value="${trainer?.employeeId}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'trainer.note.label',default:'note')}" value="${trainer?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="personId" size="8"  class=" isRequired isNumber" label="${message(code:'trainer.personId.label',default:'personId')}" value="${trainer?.personId}" />
</el:formGroup>