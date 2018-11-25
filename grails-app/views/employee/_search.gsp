<el:formGroup>

    <el:textField name="id" size="6" class=""
                  label="${message(code: 'employee.id.label', default: 'id')}"/>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="employee"
            action="autocomplete"
            name="id2"
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
            controller="militaryRankType"
            action="autocomplete"
            name="militaryRankType.id"
            label="${message(code: 'militaryRankType.label', default: 'militaryRankType')}"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRankClassification"
            action="autocomplete"
            name="militaryRankClassification.id"
            label="${message(code: 'militaryRankClassification.label', default: 'militaryRankClassification')}"/>
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
    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     class=""
                     controller="employeeStatusCategory" action="autocomplete"
                      name="categoryStatusId" label="${message(code:'employee.categoryStatus.label',default:'categoryStatus')}" />
</el:formGroup>

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code:'employee.firm.label',default:'firm')}" />
    </el:formGroup>
</sec:ifAnyGranted>

