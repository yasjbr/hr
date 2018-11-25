
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="department.id" label="${message(code:'joinedRecruitmentCycleDepartment.department.label',default:'department')}" />
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumRecruitmentCycleDepartmentStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus.values()}" name="recruitmentCycleDepartmentStatus" size="8"  class="" label="${message(code:'joinedRecruitmentCycleDepartment.recruitmentCycleDepartmentStatus.label',default:'recruitmentCycleDepartmentStatus')}" />
</el:formGroup>
