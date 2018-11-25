<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="employee"
            action="autocomplete"
            name="employee.id"
            label="${message(code: 'employee.transientData.personDTO.localFullName.label', default: 'employee name')}"/>


    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'employee.currentEmployeeMilitaryRank.militaryRank.descriptionInfo.localName.label', default: 'militaryRank')}"/>

</el:formGroup>

<el:hiddenField name="idsToExclude[]" value="${generalList?.generalListEmployees?.employee?.id}"/>