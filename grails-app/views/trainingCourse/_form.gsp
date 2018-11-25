
<el:formGroup>
    <el:textField name="arabicDescription" size="8"  class="" label="${message(code:'trainingCourse.arabicDescription.label',default:'arabicDescription')}" value="${trainingCourse?.arabicDescription}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="courseCode" size="8"  class=" isRequired" label="${message(code:'trainingCourse.courseCode.label',default:'courseCode')}" value="${trainingCourse?.courseCode}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:trainingCourse?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="englishDescription" size="8"  class="" label="${message(code:'trainingCourse.englishDescription.label',default:'englishDescription')}" value="${trainingCourse?.englishDescription}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="targetGroup" action="autocomplete" name="targetGroup.id" label="${message(code:'trainingCourse.targetGroup.label',default:'targetGroup')}" values="${[[trainingCourse?.targetGroup?.id,trainingCourse?.targetGroup?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="trainerCondition" action="autocomplete" name="trainerCondition.id" label="${message(code:'trainingCourse.trainerCondition.label',default:'trainerCondition')}" values="${[[trainingCourse?.trainerCondition?.id,trainingCourse?.trainerCondition?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="trainingClassification" action="autocomplete" name="trainingClassification.id" label="${message(code:'trainingCourse.trainingClassification.label',default:'trainingClassification')}" values="${[[trainingCourse?.trainingClassification?.id,trainingCourse?.trainingClassification?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumTrainingStatus"  from="${ps.gov.epsilon.hr.enums.training.v1.EnumTrainingStatus.values()}" name="trainingStatus" size="8"  class=" isRequired" label="${message(code:'trainingCourse.trainingStatus.label',default:'trainingStatus')}" value="${trainingCourse?.trainingStatus}" />
</el:formGroup>