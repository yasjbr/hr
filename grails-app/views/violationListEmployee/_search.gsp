<el:formGroup>
    <el:textField name="employeeViolation.id"
                     size="6"
                     class=""
                     label="${message(code: 'employeeViolation.id.label', default: 'id')}"/>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
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

    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="disciplinaryReason" action="autocomplete"
                     name="disciplinaryReason.id"
                     label="${message(code: 'employeeViolation.disciplinaryReason.label', default: 'disciplinaryReason')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="violationDate"
              label="${message(code: 'employeeViolation.violationDate.label')}"/>
    <el:select valueMessagePrefix="EnumViolationStatus"
               from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.values()}"
               name="violationStatus" size="6" class=""
               label="${message(code: 'employeeViolation.violationStatus.label', default: 'violationStatus')}"/>
</el:formGroup>