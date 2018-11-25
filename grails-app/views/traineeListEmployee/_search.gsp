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
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="Pcore"
                     action="genderTypeAutoComplete"
                     name="genderType.id"
                     label="${message(code: 'applicant.transientData.genderType.label', default: 'genderType')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="applyingDate"
              label="${message(code: 'applicant.applyingDate.label')}"/>
    <el:select
            valueMessagePrefix="EnumApplicantStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values()}"
            name="applicantCurrentStatusValue"
            size="6"
            class=""
            label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="physicalInspectionMark" class="" size="6"
                  label="${message(code: 'applicant.physicalInspectionMark.label', default: 'id')}"/>
</el:formGroup>


<g:render template="/pcore/location/searchTemplate" model="[prefix: 'location']"/>




