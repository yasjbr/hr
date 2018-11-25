<el:validatableModalForm title="${message(code: 'jobRequisition.accept.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="jobRequisition" action="setApprovedPositions">
    <msg:modal/>

    <el:hiddenField name="id" value="${jobRequisition?.id}"/>

    <el:formGroup>
        <el:integerField name="acceptNumberOfApprovedPositions" id="acceptNumberOfApprovedPositions" size="12"
                         label="${message(code: 'jobRequisition.numberOfApprovedPositions.label', default: 'numberOfApprovedPositions')}"
                         class=" isRequired" value="${jobRequisition?.numberOfPositions}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="note" size="12"
                     label="${message(code: 'jobRequisition.note.label', default: 'note')}"
                     class=" "/>
    </el:formGroup>

    <el:row/>
    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>