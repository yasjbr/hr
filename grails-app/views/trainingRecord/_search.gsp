
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'trainingRecord.employee.label',default:'employee')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employeePromotion" action="autocomplete" name="employeeMilitaryRank.id" label="${message(code:'trainingRecord.employeeMilitaryRank.label',default:'employeeMilitaryRank')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentRecord" action="autocomplete" name="employmentRecord.id" label="${message(code:'trainingRecord.employmentRecord.label',default:'employmentRecord')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'trainingRecord.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'trainingRecord.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="locationId" size="8"  class=" isNumber" label="${message(code:'trainingRecord.locationId.label',default:'locationId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'trainingRecord.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="numberOfTrainee" size="8"  class=" isNumber" label="${message(code:'trainingRecord.numberOfTrainee.label',default:'numberOfTrainee')}" />
    
</el:formGroup>
<el:formGroup>
    <el:integerField name="organizationId" size="8"  class=" isNumber" label="${message(code:'trainingRecord.organizationId.label',default:'organizationId')}" />
    
</el:formGroup>
<el:formGroup>
    
    <el:textField name="organizationName" size="8"  class="" label="${message(code:'trainingRecord.organizationName.label',default:'organizationName')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'trainingRecord.toDate.label',default:'toDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="trainer" action="autocomplete" name="trainer.id" label="${message(code:'trainingRecord.trainer.label',default:'trainer')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="trainerName" size="8"  class="" label="${message(code:'trainingRecord.trainerName.label',default:'trainerName')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="trainingClassification" action="autocomplete" name="trainingClassification.id" label="${message(code:'trainingRecord.trainingClassification.label',default:'trainingClassification')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="trainingCourse" action="autocomplete" name="trainingCourse.id" label="${message(code:'trainingRecord.trainingCourse.label',default:'trainingCourse')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="trainingName" size="8"  class="" label="${message(code:'trainingRecord.trainingName.label',default:'trainingName')}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="unstructuredLocation" size="8"  class="" label="${message(code:'trainingRecord.unstructuredLocation.label',default:'unstructuredLocation')}" />
</el:formGroup>
