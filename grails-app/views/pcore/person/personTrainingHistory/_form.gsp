<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'traineeId'),
                                             name:'trainee.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personTrainingHistory?.trainee,
                                             isDisabled:isPersonDisabled,
                                             messageValue:message(code:'personTrainingHistory.trainee.label',default:'trainee')]" />


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"
                     class=" isRequired" controller="trainingCategory" action="autocomplete" name="trainingCategory.id"
                     label="${message(code:'personTrainingHistory.trainingCategory.label',default:'trainingCategory')}"
                     values="${[[personTrainingHistory?.trainingCategory?.id, personTrainingHistory?.trainingCategory?.descriptionInfo?.localName]]}" />
</el:formGroup>


<el:formGroup>
    <el:textField name="trainingName" size="8"  class=" " label="${message(code:'personTrainingHistory.trainingName.label',default:'trainingName')}" value="${personTrainingHistory?.trainingName}"/>
</el:formGroup>


<el:formGroup>
    <el:dateField name="trainingFromDate"  size="8" class=" " label="${message(code:'personTrainingHistory.trainingFromDate.label',default:'trainingFromDate')}" value="${personTrainingHistory?.trainingFromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="trainingToDate"  size="8" class=" " label="${message(code:'personTrainingHistory.trainingToDate.label',default:'trainingToDate')}" value="${personTrainingHistory?.trainingToDate}" />
</el:formGroup>


<el:formGroup>
    <el:dualAutocomplete class="isRequired" label="${message(code:'personTrainingHistory.organization.label',default:'organization')}"
                         id="organizationId" name="organization.id" action="autocomplete"
                         controller="organization" size="8"
                         values="${[[personTrainingHistory?.organization?.id,personTrainingHistory?.organization?.descriptionInfo?.localName]]}"
                         textName="instituteName"
                         textValue="${personTrainingHistory?.instituteName}"
    />
</el:formGroup>


<el:formGroup>
    <el:dualAutocomplete class="isRequired" label="${message(code:'personTrainingHistory.trainer.label',default:'trainer')}"
                         id="trainerId" name="trainer.id" action="autocomplete"
                         controller="person" size="8"
                         values="${[[personTrainingHistory?.trainer?.id,personTrainingHistory?.trainer?.localFullName]]}"
                         textName="trainerName"
                         textValue="${personTrainingHistory?.trainerName}"
    />
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"
                     class=" " controller="trainingDegree" action="autocomplete" name="trainingDegree.id" label="${message(code:'personTrainingHistory.trainingDegree.label',default:'trainingDegree')}" values="${[[personTrainingHistory?.trainingDegree?.id,personTrainingHistory?.trainingDegree?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personTrainingHistory.note.label',default:'note')}" value="${personTrainingHistory?.note}"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'personTrainingHistory.location.label',default:'location')}">
    <g:render template="/pcore/location/wrapper" model="[
            isCountryRequired         : true,
            hiddenDetails             : true,
            size                      : 8,
            location:personTrainingHistory?.location]" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${personTrainingHistory?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>
