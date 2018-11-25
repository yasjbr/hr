
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#employmentServiceTableInServiceList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'serviceListEmployeeNote.entity', default: 'serviceListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'serviceListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="serviceListEmployeeNoteForm"
                         controller="serviceListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="serviceListEmployee.id" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'serviceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'serviceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'serviceListEmployeeNote.note.label', default: 'note')}"
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
        var isCreate = $(this).find('#serviceListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#employmentServiceTableInServiceList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#employmentServiceTableInServiceList').find('a.modal-ajax_${id}').click();
    }
</script>