
<el:formGroup>
<el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                 controller="job" action="autocomplete" name="requestedJob.id"
                 label="${message(code:'loanNotice.requestedJob.label',default:'requestedJob')}" />

    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="requesterOrganizationId"
                     label="${message(code:'loanNotice.requesterOrganizationId.label',default:'requesterOrganizationId')}" />

</el:formGroup>



<el:formGroup>

    <el:integerField name="numberOfPositions" size="6"
                     class=" isNumber"
                     label="${message(code:'loanNotice.numberOfPositions.label',default:'numberOfPositions')}" />


    <el:range type="date" size="6" name="fromDate"
              label="${message(code:'loanNotice.fromDate.label')}"  />



</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="toDate"
              label="${message(code:'loanNotice.toDate.label')}"  />


    <el:select valueMessagePrefix="EnumLoanNoticeStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.values()}"
               name="loanNoticeStatus" size="6"  class=""
               label="${message(code:'loanNotice.loanNoticeStatus.label',default:'loanNoticeStatus')}" />

</el:formGroup>

<script>
    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }
</script>