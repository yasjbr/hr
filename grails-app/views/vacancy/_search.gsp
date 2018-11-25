<el:formGroup>

    <el:textField name="id" size="6" class=" "
                     label="${message(code: 'vacancy.id.label', default: 'id')}"/>


    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="recruitmentCycle"
                     action="autocomplete"
                     name="recruitmentCycle.id"
                     label="${message(code: 'vacancy.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="job"
                     action="autocomplete"
                     name="job.id"
                     label="${message(code: 'vacancy.job.label', default: 'job')}"/>

    <el:integerField name="numberOfPositions"
                     size="6"
                     class=" isNumber"
                     label="${message(code: 'vacancy.numberOfPositions.label', default: 'numberOfPositions')}"/>



</el:formGroup>

<el:formGroup>


    <el:select valueMessagePrefix="EnumVacancyStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.values()}"
               name="vacancyStatus"
               size="6"
               class=""
               label="${message(code: 'vacancy.vacancyStatus.label', default: 'vacancyStatus')}"/>

</el:formGroup>


