<el:hiddenField name="employeeId" value="${employeeId}"/>
<el:formGroup>
    <el:textField name="id" size="6"
                  class=""
                  label="${message(code: 'disciplinaryRequest.id.label', default: 'id')}"/>
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
            controller="department"
            action="autocomplete"
            name="department.id"
            label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

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
                     controller="disciplinaryCategory" action="autocomplete" name="disciplinaryCategory.id"
                     label="${message(code: 'disciplinaryRequest.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>

    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'disciplinaryRequest.requestDate.label')}"/>
</el:formGroup>


<el:formGroup>
    <el:select
            valueMessagePrefix="EnumRequestStatus"
            from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
            name="requestStatus"
            size="6"
            class=""
            label="${message(code: 'disciplinaryRequest.requestStatus.label', default: 'requestStatus')}"/>

    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </sec:ifAnyGranted>

</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />