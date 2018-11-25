<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>

    <el:textField class=" "
                  name="descriptionInfo.localName" size="6"
                  label="${message(code: 'descriptionInfo.localName.label', default: 'localName')}"
                  value=""/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="departmentType"
                     action="autocomplete" name="departmentTypeId"
                     label="${message(code: 'department.departmentType.label', default: 'departmentType')}"
                     values="${[[department?.departmentType?.id, department?.departmentType?.descriptionInfo?.localName]]}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" " controller="department" action="autocomplete"
                     name="functionalParentDept"
                     label="${message(code: 'department.functionalParentDept.label', default: 'functionalParentDept')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" " controller="department" action="autocomplete"
                     name="managerialParentDept"
                     label="${message(code: 'department.managerialParentDept.label', default: 'managerialParentDept')}"/>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="pcore"
                     action="locationAutoComplete"
                     name="locationId"
                     label="${message(code:'department.locationId.label',default:'location')}"/>
</el:formGroup>
<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="firm"
            action="autocomplete"
            name="firm.id"
            label="${message(code: 'firm.label', default: 'firm')}"
            values="${[[department?.firm?.id, department?.firm?.name]]}"/>
</el:formGroup>
</sec:ifAnyGranted>