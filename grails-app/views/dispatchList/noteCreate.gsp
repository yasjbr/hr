
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#applicantTableInRecruitmentList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'dispatchListEmployeeNote.entity', default: 'dispatchListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'dispatchListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="dispatchListEmployeeNoteForm"
                         controller="dispatchListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="save_dispatchListEmployeeId" value="${id}"/>
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

    %{--<el:formGroup>--}%
        %{--<el:checkboxField name="rejectRequest" size="8" label="${message(code: 'dispatchRequest.reject.label', default: 'note')}" />--}%
    %{--</el:formGroup>--}%

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#dispatchListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#dispatchRequestTableInDispatchList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#dispatchRequestTableInDispatchList').find('a.modal-ajax_${id}').click();
    }
</script>