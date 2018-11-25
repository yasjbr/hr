<el:formGroup>
    <el:textField name="employmentServiceRequest.id"
                     size="6"
                     class=" isNumber"
                     label="${message(code: 'employmentServiceRequest.id.label', default: 'id')}"
                     value=""/>
    <el:autocomplete
            preventSpaces="true"
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="employee"
            action="autocomplete"
            name="employee.id"
            label="${message(code: 'employmentServiceRequest.employee.label', default: 'employee')}"/>
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
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

    <el:range type="date" size="6" name="dateEffective" setMinDateFromFor="dateEffectiveTo"
              label="${message(code: 'serviceListEmployee.dateEffective.label')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6" class=""
                     controller="serviceActionReason"
                     action="autocomplete"
                     name="serviceActionReason.id"
                     label="${message(code: 'employmentServiceRequest.serviceActionReason.label', default: 'serviceActionReason')}"/>
    <el:select valueMessagePrefix="EnumListRecordStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus"
               size="6"
               class=""
               label="${message(code: 'serviceListEmployee.recordStatus.label', default: 'recordStatus')}"/>
</el:formGroup>