<el:formGroup>
    <el:hiddenField name="save_promotionListEmployeeId"/>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'recruitmentListEmployeeNote.orderNo.label', default: 'orderNo')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate" size="8" class=" isRequired"
                  label="${message(code: 'recruitmentListEmployeeNote.noteDate.label', default: 'noteDate')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'recruitmentListEmployeeNote.note.label', default: 'note')}"
                 value=""/>
</el:formGroup>