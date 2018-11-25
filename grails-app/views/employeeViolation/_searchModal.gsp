<el:formGroup>
    <el:textField name="id" size="6"
                     class=""
                     label="${message(code: 'employeeViolation.id.label', default: 'id')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>


</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="disciplinaryReason" action="autocomplete"
                     name="disciplinaryReason.id"
                     label="${message(code:'employeeViolation.disciplinaryReason.label',default:'disciplinaryReason')}" />
    <el:range type="date" size="6" name="violationDate"
              label="${message(code: 'employeeViolation.violationDate.label')}"/>

</el:formGroup>

<g:if test="${!hideStatusSearch}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumViolationStatus"
                   from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.values()}"
                   name="violationStatus" size="6"  class=""
                   label="${message(code:'employeeViolation.violationStatus.label',default:'violationStatus')}" />
    </el:formGroup>
</g:if>