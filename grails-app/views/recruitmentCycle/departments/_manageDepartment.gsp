<g:set var="isAllowToRemoved" value="${isAllowToRemove == "true" ? "true" : "false"}"/>
<el:formGroup>
    <el:hiddenField name="manageDepartment" value="true"/>
    <el:dualListBox size="8"
                    class=" isRequired"
                    optionKey="id"
                    name="department.id"
                    moveOnSelect="false"
                    showFilterInputs="true"
                    isAllowToAdd="true"
                    isAllowToRemove="${isAllowToRemoved}"
                    from="${departmentsList}"
                    label="${message(code: 'joinedRecruitmentCycleDepartment.department.label', default: 'department')}"
                    values="${recruitmentCycle?.joinedRecruitmentCycleDepartment?.department}"/>
</el:formGroup>
<el:formGroup>
    <el:dualListBox size="8"
                    class=" isRequired"
                    optionKey="id" optionValue="descriptionInfo"
                    name="jobCategory.id"
                    moveOnSelect="false"
                    showFilterInputs="true"
                    isAllowToAdd="true"
                    isAllowToRemove="${isAllowToRemoved}"
                    from="${JobCategoriesList}"
                    label="${message(code: 'joinedRecruitmentCycleDepartment.jobCategory.label', default: 'jobCategory')}"
                    values="${recruitmentCycle?.joinedRecruitmentCycleJobCategory?.jobCategory}"/>
</el:formGroup>