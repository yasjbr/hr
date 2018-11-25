<el:hiddenField name="interviewId" value="${entityId}"/>
<el:formGroup>
    <el:autocomplete optionKey="name" optionValue="name" size="6" class=""  controller="Applicant" action="autocomplete"
                     name="personName" label="${message(code: 'applicant.personName.label', default: 'person Name')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacancy" action="autocomplete"
                     name="vacancy.id" label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
</el:formGroup>

<el:formGroup>
    <el:decimalField name="age" size="6" class=" isDecimal"
                     label="${message(code: 'applicant.age.label', default: 'age')}"/>
    <el:range type="date" size="6" name="applyingDate" setMinDateFromFor="applyingDateTo"
              label="${message(code:'applicant.applyingDate.label')}"  />
</el:formGroup>

<el:formGroup>
    <el:select
            valueMessagePrefix="EnumApplicantStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values()}"
            name="applicantCurrentStatusValue"
            size="6"
            class="" value="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.NEW}"
            label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="recruitmentCycle" action="autocomplete" name="recruitmentCycle.id" label="${message(code:'applicant.recruitmentCycle.label',default:'recruitmentCycle')}" />


</el:formGroup>
