<el:formGroup>
    <el:textField name="id" size="6"
                  class=""
                  label="${message(code: 'employeeViolation.id.label', default: 'id')}"/>
    <g:if test="${!hideEmployeeWrapper}">
        <g:render template="/employee/wrapper" model="[isSearch            : true,
                                                       disableFormGroupName: true,
                                                       size                : 6]"/>
    </g:if>

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
                     controller="disciplinaryCategory" action="autocomplete"
                     name="disciplinaryCategoryId"
                     label="${message(code: 'employeeViolation.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="disciplinaryReason" action="autocomplete"
                     name="disciplinaryReason.id"
                     label="${message(code: 'employeeViolation.disciplinaryReason.label', default: 'disciplinaryReason')}"/>
    <el:range type="date" size="6" name="violationDate"
              label="${message(code: 'employeeViolation.violationDate.label')}"/>

</el:formGroup>

<g:if test="${!hideStatusSearch}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumViolationStatus"
                   from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.values()}"
                   name="violationStatus" size="6" class=""
                   label="${message(code: 'employeeViolation.violationStatus.label', default: 'violationStatus')}"/>
        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                             controller="firm" action="autocomplete"
                             name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
        </sec:ifAnyGranted>

    </el:formGroup>
</g:if>