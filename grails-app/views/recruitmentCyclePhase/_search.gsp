
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'recruitmentCyclePhase.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="recruitmentCycle" action="autocomplete" name="recruitmentCycle.id" label="${message(code:'recruitmentCyclePhase.recruitmentCycle.label',default:'recruitmentCycle')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequisitionAnnouncementStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.values()}" name="requisitionAnnouncementStatus" size="8"  class="" label="${message(code:'recruitmentCyclePhase.requisitionAnnouncementStatus.label',default:'requisitionAnnouncementStatus')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'recruitmentCyclePhase.toDate.label',default:'toDate')}" />
</el:formGroup>
