<el:formGroup>
    <el:textField name="id" size="6" class=""
                  label="${message(code: 'employee.id.label', default: 'id')}"/>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="person"
            action="autocomplete"
            name="personId"
            label="${message(code: 'employee.transientData.personDTO.localFullName.label', default: 'employee name')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'employee.currentEmployeeMilitaryRank.militaryRank.descriptionInfo.localName.label', default: 'militaryRank')}"/>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="pcore"
            action="governorateAutoComplete"
            name="governorateId"
            label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="department"
            action="autocomplete"
            name="department.id"
            label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    <el:textField name="financialNumber" size="6" class=""
                  label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="recentCardNo" size="6" class=""
                  label="${message(code: 'person.recentCardNo.label', default: 'recentCardNo')}"/>
</el:formGroup>