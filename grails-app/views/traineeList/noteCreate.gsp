
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#applicantTableInTraineeList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'trainingListEmployeeNote.entity', default: 'trainingListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'trainingListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="trainingListEmployeeNoteForm"
                         controller="trainingListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="traineeListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'trainingListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'trainingListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'trainingListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#trainingListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#applicantTableInTraineeList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#applicantTableInTraineeList').find('a.modal-ajax_${id}').click();
    }
</script>