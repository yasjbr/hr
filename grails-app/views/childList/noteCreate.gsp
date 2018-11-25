
<script type="text/javascript">
    function callBackFun(json){
        if (json.success) {
            $('#childRequestTableInChildList').find('a.modal-ajax_${id}').click();
        }
    }
</script>

<g:set var="noteFormEntity" value="${message(code: 'childListEmployeeNote.entity', default: 'childListEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'childListEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="childListEmployeeNoteForm"
                         controller="childListEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">
    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="save_childListEmployeeId" value="${id}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'childListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'childListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'childListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#childListEmployeeNoteForm').length;
        if(isCreate > 0){
            $('#childRequestTableInChildList').find('a.modal-ajax_${id}').click();
        }
    });

    function closeForm() {
        $('#childRequestTableInChildList').find('a.modal-ajax_${id}').click();
    }
</script>