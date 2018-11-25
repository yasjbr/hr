
<el:hiddenField name="withRemotingValues" value="true"/>

<el:formGroup>
    <el:textField name="applicant.id" class="" size="6"
                  label="${message(code: 'applicant.id.label', default: 'id')}"/>
    <el:textField name="personName" class="" size="6"
                  label="${message(code: 'applicant.personName.label', default: 'person Name')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacancy" action="autocomplete"
                     name="vacancy.id" label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
    <el:decimalField name="age" size="6" class=" isDecimal"
                     label="${message(code: 'applicant.age.label', default: 'age')}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6"
               class="" label="${message(code:'applicantInspectionResultListEmployee.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>
