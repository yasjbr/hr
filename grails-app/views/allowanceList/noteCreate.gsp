<script type="text/javascript">
    function callBackFun(json) {
        if (json.success) {
            $('#allowanceRequestTableInAllowanceList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity"
       value="${message(code: 'allowanceListEmployeeNote.entity', default: 'allowanceListEmployeeNote')}"/>
<g:set var="noteFormTitle"
       value="${message(code: 'default.create.label', args: [noteFormEntity], default: 'allowanceListEmployeeNote')}"/>

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="allowanceListEmployeeNoteForm"
                         controller="allowanceListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="allowanceListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'allowanceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'allowanceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'allowanceListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>


    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#allowanceListEmployeeNoteForm').length;
        if (isCreate > 0) {
            $('#allowanceRequestTableInAllowanceList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#allowanceRequestTableInAllowanceList').find('a.modal-ajax_${id}').click();
    }
</script>