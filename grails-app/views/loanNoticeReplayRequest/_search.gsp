
<el:formGroup>

    <el:textField name="id" size="6"
                     class=""
                     label="${message(code:'loanNoticeReplayRequest.id.label',default:'id')}" />

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee"
                     action="autocomplete" name="employee.id" paramsGenerateFunction="employeeParams"
                     label="${message(code:'loanNoticeReplayRequest.employee.label',default:'employee')}" />

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

    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="requestedByOrganizationId"
                     label="${message(code:'loanNoticeReplayRequest.transientData.requestedByOrganizationDTO.label',default:'requestedFromOrganizationId')}" />

</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code:'loanNoticeReplayRequest.fromDate.label')}"  />

    <el:range type="date" size="6" name="toDate"
              label="${message(code:'loanNoticeReplayRequest.toDate.label')}"  />

</el:formGroup>


<el:formGroup>
    <el:range type="date" name="requestDate" size="6" class=""
              label="${message(code:'loanNoticeReplayRequest.requestDate.label',default:'requestDate')}" />
    <g:if test="${!hideStatusSearch}">
            <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                       name="requestStatus" size="6"  class=""
                       label="${message(code:'loanNoticeReplayRequest.requestStatus.label',default:'requestStatus')}" />
    </g:if>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />



<script>
    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }

    function employeeParams() {
        return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value}'}
    }
</script>