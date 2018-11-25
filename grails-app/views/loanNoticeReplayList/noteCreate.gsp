
<g:set var="noteFormEntity" value="${message(code: 'loanNominatedEmployeeNote.entity', default: 'loanNominatedEmployeeNote')}" />
<g:set var="noteFormTitle" value="${message(code: 'default.create.label',args:[noteFormEntity], default: 'loanNominatedEmployeeNote')}" />

<el:validatableModalForm title="${noteFormTitle}"
                         width="70%"
                         name="loanNominatedEmployeeNoteForm"
                         controller="loanNominatedEmployeeNote"
                         hideCancel="true"
                         hideClose="true"
                         action="save" callBackFunction="callBackFun">

    <msg:modal/>

    <el:formGroup>
        <el:hiddenField name="loanNominatedEmployee.encodedId" value="${encodedId}"/>
        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'loanNominatedEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'loanNominatedEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'loanNominatedEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="loadListScreen()" functionName="cancel"/>

</el:validatableModalForm>

<script type="text/javascript">

    function callBackFun() {
        $('#application-modal-main-content').modal("hide");
        loadListScreen();
    }
    function loadListScreen() {
        $('#loanNominatedEmployeeTable').find('a.modal-ajax_${ps.police.common.utils.v1.HashHelper.decode(encodedId)}').click();
    }
</script>