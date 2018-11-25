<el:validatableModalForm title="${message(code: 'jobRequisition.previousWork.label')}"
                         width="50%"
                         name="sendDataForm"
                         controller="jobRequisition"
                         hideCancel="true"
                         hideClose="true"
                         action="save">
    <msg:modal/>

    <el:formGroup>
        <el:integerField name="periodInYears_"
                         size="12"
                         class="isNumber isRequired"
                         label="${message(code: 'jobRequisition.workExperience.periodInYears.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="12"
                         class=" "
                         controller="pcore"
                         action="professionTypeAutoComplete"
                         name="workExperience.professionType"
                         label="${message(code: 'jobRequisition.workExperience.professionType.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="12"
                         class=" "
                         controller="pcore"
                         action="competencyAutoComplete"
                         name="workExperience.competency"
                         label="${message(code: 'jobRequisition.workExperience.competency.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="otherSpecifications_"
                     size="12"
                     class=""
                     label="${message(code: 'jobRequisition.workExperience.otherSpecifications.label', default: 'workExperience')}"/>
    </el:formGroup>

    <el:row/>

    <el:formButton functionName="addButton"
                    onClick="addPreviousWork()" />

    <el:formButton functionName="close"
                    onClick="closePreviousWorkModal()" />

</el:validatableModalForm>