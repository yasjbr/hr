
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#applicantTableInRecruitmentList').find('a.modal-ajax_${applicantId}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'recruitmentListEmployeeNote.entity', default: 'recruitmentListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'recruitmentListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="recruitmentListEmployeeNoteForm"
                         controller="recruitmentListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="recruitmentListEmployee.id" value="${recruitmentListEmployeeId}"/>
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

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>
    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#recruitmentListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#applicantTableInRecruitmentList').find('a.modal-ajax_${applicantId}').click();
        }
    });
    function closeForm() {
        $('#applicantTableInRecruitmentList').find('a.modal-ajax_${applicantId}').click();
    }
</script>