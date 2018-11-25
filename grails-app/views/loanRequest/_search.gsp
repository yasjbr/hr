<el:formGroup>

    <el:textField name="id" size="6"
                  class=""
                  label="${message(code: 'loanRequest.id.label', default: 'id')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="job" action="autocomplete" name="requestedJob.id"
                     label="${message(code: 'loanRequest.requestedJob.label', default: 'requestedJob')}"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="requestedFromOrganizationId"
                     label="${message(code: 'loanRequest.requestedFromOrganizationId.label', default: 'requestedFromOrganizationId')}"/>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'loanRequest.fromDate.label')}"/>

</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'loanRequest.toDate.label')}"/>

    <el:integerField name="numberOfPositions" size="6"
                     class=" isNumber"
                     label="${message(code: 'loanRequest.numberOfPositions.label', default: 'numberOfPositions')}"/>

</el:formGroup>


<el:formGroup>

    <el:range type="date" name="requestDate" size="6" class=""
              label="${message(code: 'loanRequest.requestDate.label', default: 'requestDate')}"/>


    <g:if test="${!hideStatusSearch}">
        <el:select valueMessagePrefix="EnumRequestStatus"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'loanRequest.requestStatus.label', default: 'requestStatus')}"/>
    </g:if>

</el:formGroup>
<g:render template="/request/wrapperManagerialOrder" />


<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'employee.firm.label', default: 'firm')}"/>
    </el:formGroup>
</sec:ifAnyGranted>



<script>
    function organizationParams() {
        return {
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }
</script>