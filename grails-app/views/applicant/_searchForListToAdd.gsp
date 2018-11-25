<el:hiddenField name="recruitmentListId" value="${params.recruitmentListId}"/>
<el:hiddenField name="withRemotingValues" value="true"/>

<el:formGroup>
    <el:textField name="personNameToAdd" class="" size="8"
                  label="${message(code: 'applicant.personName.label', default: 'person Name')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacancy" action="autocomplete"
                     name="vacancy.idToAdd" label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
</el:formGroup>

<el:formGroup>
    <el:decimalField name="ageToAdd" size="8" class=" isDecimal"
                     label="${message(code: 'applicant.age.label', default: 'age')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="applyingDateToAdd" size="8" class=""
                  label="${message(code: 'applicant.applyingDate.label', default: 'applyingDate')}"/>
</el:formGroup>

