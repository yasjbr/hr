
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#maritalStatusRequestTableInMaritalStatusList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'maritalStatusEmployeeNote.entity', default: 'maritalStatusEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'maritalStatusEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="maritalStatusEmployeeNoteForm"
                         controller="maritalStatusEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="save_maritalStatusListEmployeeId" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'maritalStatusEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'maritalStatusEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'maritalStatusEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    %{--<el:formGroup>--}%
        %{--<el:checkboxField name="rejectRequest" size="8" label="${message(code: 'childRequest.reject.label', default: 'reject note')}" />--}%
    %{--</el:formGroup>--}%

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#maritalStatusEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#maritalStatusRequestTableInMaritalStatusList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#maritalStatusRequestTableInMaritalStatusList').find('a.modal-ajax_${id}').click();
    }
</script>