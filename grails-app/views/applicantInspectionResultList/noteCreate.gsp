
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#applicantTableInApplicantInspectionResultList').find('a.black.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'applicantInspectionResultListEmployeeNote.entity', default: 'applicantInspectionResultListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'applicantInspectionResultListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="applicantInspectionResultListEmployeeNoteForm"
                         controller="applicantInspectionResultListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="applicantInspectionResultListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'applicantInspectionResultListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'applicantInspectionResultListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'applicantInspectionResultListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#applicantInspectionResultListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#applicantTableInApplicantInspectionResultList').find('a.black.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#applicantTableInApplicantInspectionResultList').find('a.black.modal-ajax_${id}').click();
    }
</script>