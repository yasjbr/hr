<el:formGroup>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'serviceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate" size="8" class=""
                  label="${message(code: 'serviceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'serviceListEmployeeNote.note.label', default: 'note')}"
                 value=""/>
</el:formGroup>