<el:formGroup>
    <el:textField
            name="id"
            size="8"
            class=""
            label="${message(code: 'recruitmentCycle.id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField
            name="name"
            size="8"
            class=""
            label="${message(code: 'recruitmentCycle.name.label', default: 'name')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date"
              name="fromDate"
              size="8"
              class=""
              label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.fromDate.label', default: 'fromDate')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date"
              name="toDate"
              size="8"
              class=""
              label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.toDate.label', default: 'toDate')}"/>
</el:formGroup>


<el:formGroup>
    <el:select
            valueMessagePrefix="EnumRequisitionAnnouncementStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.values()}"
            name="requisitionAnnouncementStatus"
            size="8"
            class=""
            label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.requisitionAnnouncementStatus.label', default: 'requisitionAnnouncementStatus')}"/>
</el:formGroup>
