<el:validatableModalForm title="${message(code: 'jobRequisition.reject.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="jobRequisition" action="setApprovedPositions">
    <msg:modal/>
    <el:hiddenField name="id" value="${jobRequisition?.id}"/>
    <el:formGroup>
        <el:textArea name="note" id="note" size="12"
                     label="${message(code: 'jobRequisition.note.label', default: 'note')}"
                     class=" isRequired"/>
    </el:formGroup>

    <el:row/>
    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>