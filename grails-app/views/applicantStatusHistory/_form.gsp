
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="applicant" action="autocomplete" name="applicant.id" label="${message(code:'applicantStatusHistory.applicant.label',default:'applicant')}" values="${[[applicantStatusHistory?.applicant?.id,applicantStatusHistory?.applicant?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumApplicantStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values()}" name="applicantStatus" size="8"  class=" isRequired" label="${message(code:'applicantStatusHistory.applicantStatus.label',default:'applicantStatus')}" value="${applicantStatusHistory?.applicantStatus}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'applicantStatusHistory.fromDate.label',default:'fromDate')}" value="${applicantStatusHistory?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'applicantStatusHistory.toDate.label',default:'toDate')}" value="${applicantStatusHistory?.toDate}" />
</el:formGroup>