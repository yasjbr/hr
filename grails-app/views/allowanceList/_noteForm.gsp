<el:formGroup>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'allowanceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate" size="8" class=" ${isRequired}"
                  label="${message(code: 'allowanceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                  value="${isRequired?java.time.ZonedDateTime.now():''}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'allowanceListEmployeeNote.note.label', default: 'note')}"
                 value=""/>
</el:formGroup>