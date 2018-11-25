
<el:formGroup>
    
    <el:textField name="arabicDescription" size="8"  class="" label="${message(code:'trainingCourse.arabicDescription.label',default:'arabicDescription')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="courseCode" size="8"  class="" label="${message(code:'trainingCourse.courseCode.label',default:'courseCode')}" />
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:trainingCourse?.descriptionInfo,isSearch:true]" />
<el:formGroup>
    
    <el:textField name="englishDescription" size="8"  class="" label="${message(code:'trainingCourse.englishDescription.label',default:'englishDescription')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="targetGroup" action="autocomplete" name="targetGroup.id" label="${message(code:'trainingCourse.targetGroup.label',default:'targetGroup')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="trainerCondition" action="autocomplete" name="trainerCondition.id" label="${message(code:'trainingCourse.trainerCondition.label',default:'trainerCondition')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="TrainingClassification" action="autocomplete" name="trainingClassification.id" label="${message(code:'trainingCourse.TrainingClassification.label',default:'TrainingClassification')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumTrainingStatus" from="${ps.gov.epsilon.hr.enums.training.v1.EnumTrainingStatus.values()}" name="trainingStatus" size="8"  class="" label="${message(code:'trainingCourse.trainingStatus.label',default:'trainingStatus')}" />
</el:formGroup>
