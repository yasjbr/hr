<el:validatableModalForm title="${message(code: 'profileNotice.endNotice.label')}"
                         name="endNoticeForm" width="70%"
                         callBackFunction="callBackFunction"
                         controller="profileNotice"
                         action="saveChangeStatus">
    <msg:modal/>
    <el:row/>
    %{--<lay:widget size="12">--}%
        %{--<lay:widgetBody>--}%
            <el:hiddenField name="encodedId" value="${profileNoticeId}"/>
            <el:hiddenField name="profileNoticeStatus"
                            value="${ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus.FINISHED}"/>
            <el:row>
                <el:textArea name="note" size="12" class="isRequired" labelSize="2"
                             label="${message(code: 'profileNoticeNote.note.label', default: 'Note')}"/>
            </el:row>
        %{--</lay:widgetBody>--}%
    %{--</lay:widget>--}%
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
