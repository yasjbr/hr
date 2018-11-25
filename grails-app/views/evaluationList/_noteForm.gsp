<el:formGroup>
    <el:hiddenField name="save_evaluationListEmployeeId"/>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'evaluationListEmployeeNote.orderNo.label', default: 'orderNo')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate" size="8" class=" isRequired"
                  label="${message(code: 'evaluationListEmployeeNote.noteDate.label', default: 'noteDate')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'evaluationListEmployeeNote.note.label', default: 'note')}"
                 value=""/>
</el:formGroup>