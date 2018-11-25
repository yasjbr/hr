<el:hiddenField name="nextPhase" value="true" />

<lay:widget icon="icon-vcard-1" color="blue" class="col-md-12"
            title="${g.message(code: "recruitmentCycle.nextPhase.label")} : ${message(code:'EnumRequisitionAnnouncementStatus.'+nextPhaseName)}">
    <lay:widgetBody>

<el:formGroup>
    <el:dateField
            name="fromDate"
            size="8"
            class=" isRequired"
            label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.fromDate.label', default: 'from Date')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField
            name="toDate"
            size="8"
            class=""
            label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.toDate.label', default: 'to Date')}"/>
</el:formGroup>
    </lay:widgetBody>
</lay:widget>

<!-- ======================================================================= -->
