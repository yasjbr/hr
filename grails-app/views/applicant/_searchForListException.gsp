<el:hiddenField name="traineeListIdException" value="${params.id}"/>
<el:hiddenField name="withRemotingValuesException" value="true"/>

<el:formGroup>
    <el:textField name="personNameException" class="" size="8"
                  label="${message(code: 'applicant.personName.label', default: 'person Name')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacancy" action="autocomplete"
                     name="vacancyException.id" label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
</el:formGroup>

<el:formGroup>
    <el:decimalField name="ageException" size="8" class=" isDecimal"
                     label="${message(code: 'applicant.age.label', default: 'age')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="applyingDateException" size="8" class=""
                  label="${message(code: 'applicant.applyingDate.label', default: 'applyingDate')}"/>
</el:formGroup>

