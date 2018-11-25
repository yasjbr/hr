<el:formGroup>
    <el:hiddenField name="save_dispatchListEmployeeId"/>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'dispatchListEmployeeNote.orderNo.label', default: 'orderNo')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate" size="8" class=" isRequired"
                  label="${message(code: 'dispatchListEmployeeNote.noteDate.label', default: 'noteDate')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'dispatchListEmployeeNote.note.label', default: 'note')}"
                 value=""/>
</el:formGroup>