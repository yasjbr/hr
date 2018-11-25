<el:validatableModalForm title="${message(code: titleCode)}"
                         name="lockProfileForm" width="70%"
                         callBackFunction="callBackFunction"
                         controller="employee"
                         action="saveEmployeeProfileStatus">
    <msg:modal/>
    <el:row/>
    <el:hiddenField name="encodedId" value="${employeeId}"/>
    <el:hiddenField name="profileStatus"
                    value="${profileStatus?.toString()}"/>
    <el:row>
        <el:textArea name="note" size="12" class="isRequired" labelSize="2"
                     label="${message(code: 'profileNoticeNote.note.label', default: 'Note')}"/>
    </el:row>
    <br/>
    <el:formButton isSubmit="true" functionName="save"/>
</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            window.location.reload();
        }
    }
</script>
