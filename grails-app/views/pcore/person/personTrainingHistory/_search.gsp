<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="person" action="autocomplete" name="trainee.id" label="${message(code:'personTrainingHistory.trainee.label',default:'trainee')}" values="${[[personTrainingHistory?.trainee?.id,personTrainingHistory?.trainee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="person" action="autocomplete" name="trainer.id" label="${message(code:'personTrainingHistory.trainer.label',default:'trainer')}" values="${[[personTrainingHistory?.trainer?.id,personTrainingHistory?.trainer?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="instituteName" size="8"  class=" " label="${message(code:'personTrainingHistory.instituteName.label',default:'instituteName')}" value="${personTrainingHistory?.instituteName}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personTrainingHistory.note.label',default:'note')}" value="${personTrainingHistory?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="organization.id" label="${message(code:'personTrainingHistory.organization.label',default:'organization')}" values="${[[personTrainingHistory?.organization?.id,personTrainingHistory?.organization?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="trainerName" size="8"  class=" " label="${message(code:'personTrainingHistory.trainerName.label',default:'trainerName')}" value="${personTrainingHistory?.trainerName}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="trainingCategory" action="autocomplete" name="trainingCategory.id" label="${message(code:'personTrainingHistory.trainingClassification.label',default:'trainingCategory')}" values="${[[personTrainingHistory?.trainingCategory?.id, personTrainingHistory?.trainingCategory?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="trainingDegree" action="autocomplete" name="trainingDegree.id" label="${message(code:'personTrainingHistory.trainingDegree.label',default:'trainingDegree')}" values="${[[personTrainingHistory?.trainingDegree?.id,personTrainingHistory?.trainingDegree?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="trainingFromDate"  size="8" class=" " label="${message(code:'personTrainingHistory.trainingFromDate.label',default:'trainingFromDate')}" value="${personTrainingHistory?.trainingFromDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="trainingName" size="8"  class=" " label="${message(code:'personTrainingHistory.trainingName.label',default:'trainingName')}" value="${personTrainingHistory?.trainingName}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="trainingToDate"  size="8" class=" " label="${message(code:'personTrainingHistory.trainingToDate.label',default:'trainingToDate')}" value="${personTrainingHistory?.trainingToDate}" />
</el:formGroup>

<lay:wall title="${g.message(code: "location.label")}">
    <g:render template="/pcore/location/searchWrapper" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}"/>
    </el:formGroup>
</lay:wall>
