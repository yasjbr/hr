
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#returnFromAbsenceRequestTableInReturnFromAbsenceList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'returnFromAbsenceListEmployeeNote.entity', default: 'returnFromAbsenceListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'returnFromAbsenceListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="returnFromAbsenceListEmployeeNoteForm"
                         controller="returnFromAbsenceListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="returnFromAbsenceListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'returnFromAbsenceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'returnFromAbsenceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'returnFromAbsenceListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#returnFromAbsenceListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#returnFromAbsenceRequestTableInReturnFromAbsenceList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#returnFromAbsenceRequestTableInReturnFromAbsenceList').find('a.modal-ajax_${id}').click();
    }
</script>