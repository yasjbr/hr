
<el:formGroup>
    <el:textField
            name="name"
            size="8"
            class=" isRequired"
            label="${message(code:'recruitmentCycle.name.label',default:'name')}"
            value="${recruitmentCycle?.name}"/>
</el:formGroup>

<g:if test="${recruitmentCycle?.id}">
    <el:formGroup>
        <el:labelField
                name="currentRecruitmentCyclePhase"
                size="8"

                label="${message(code:'recruitmentCycle.currentRecruitmentCyclePhase.label',default:'currentRecruitmentCyclePhase')}"
                value="${message(code:'EnumRequisitionAnnouncementStatus.'+recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus)}"/>
    </el:formGroup>

</g:if>


<el:formGroup>
    <el:textArea
            name="description"
            size="8"
            class=""
            label="${message(code:'recruitmentCycle.description.label',default:'description')}"
            value="${recruitmentCycle?.description}"/>
</el:formGroup>