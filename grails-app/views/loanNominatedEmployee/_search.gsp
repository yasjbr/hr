
<el:formGroup>

    <el:textField name="loanNoticeReplayRequest.id" size="6"
                     class=""
                     label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.id.label',default:'id')}" />

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee"
                     action="autocomplete" name="employee.id" paramsGenerateFunction="employeeParams"
                     label="${message(code:'loanNominatedEmployee.employee.label',default:'employee')}" />

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
                     name="loanNoticeReplayRequest.requestedByOrganizationId"
                     label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.transientData.requestedByOrganizationDTO.label',default:'requestedFromOrganizationId')}" />




</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code:'loanNominatedEmployee.fromDate.label')}"  />


    <el:range type="date" size="6" name="toDate"
              label="${message(code:'loanNominatedEmployee.toDate.label')}"  />


</el:formGroup>


<el:formGroup>

    <el:range type="date" name="effectiveDate" size="6" class=""
              label="${message(code:'loanNominatedEmployee.effectiveDate.label',default:'effectiveDate')}" />



    <el:range type="date" name="loanNoticeReplayRequest.requestDate" size="6" class=""
              label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.requestDate.label',default:'requestDate')}" />


</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="loanNoticeReplayRequest.requestStatus" size="6"  class=""
               label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.requestStatus.label',default:'requestStatus')}" />
</el:formGroup>


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