
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="8"
            class=" isRequired"
            controller="department"
            action="autocomplete"
            name="department.id"
            label="${message(code:'joinedRecruitmentCycleDepartment.department.label',default:'department')}"
            values="${[[joinedRecruitmentCycleDepartment?.department?.id,joinedRecruitmentCycleDepartment?.department?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="8" class=" isRequired"
            controller="recruitmentCycle"
            action="autocomplete"
            name="recruitmentCycle.id"
            label="${message(code:'joinedRecruitmentCycleDepartment.recruitmentCycle.label',default:'recruitmentCycle')}"
            values="${[[joinedRecruitmentCycleDepartment?.recruitmentCycle?.id,joinedRecruitmentCycleDepartment?.recruitmentCycle?.name]]}" />
</el:formGroup>
<el:formGroup>
    <el:select
            valueMessagePrefix="EnumRecruitmentCycleDepartmentStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus.values()}"
            name="recruitmentCycleDepartmentStatus"
            size="8"
            class=" isRequired"
            label="${message(code:'joinedRecruitmentCycleDepartment.recruitmentCycleDepartmentStatus.label',default:'recruitmentCycleDepartmentStatus')}"
            value="${joinedRecruitmentCycleDepartment?.recruitmentCycleDepartmentStatus}" />
</el:formGroup>