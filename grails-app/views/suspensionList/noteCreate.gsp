<script type="text/javascript">
    function callBackFun(json) {
        if (json.success) {
            $('#suspensionRequestTableInSuspensionList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity"
       value="${message(code: 'suspensionListEmployeeNote.entity', default: 'suspensionListEmployeeNote')}"/>
<g:set var="noteFormTitle"
       value="${message(code: 'default.create.label', args: [noteFormEntity], default: 'suspensionListEmployeeNote')}"/>

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="suspensionListEmployeeNoteForm"
                         controller="suspensionListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="suspensionListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'suspensionListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'suspensionListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'suspensionListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>


    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#suspensionListEmployeeNoteForm').length;
        if (isCreate > 0) {
            $('#suspensionRequestTableInSuspensionList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#suspensionRequestTableInSuspensionList').find('a.modal-ajax_${id}').click();
    }
</script>