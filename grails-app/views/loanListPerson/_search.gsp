<el:formGroup>

    <el:textField name="loanRequest.id" size="6"
                     class=""
                     label="${message(code:'loanListPerson.loanRequest.id.label',default:'id')}" />


    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="job" action="autocomplete" name="loanRequest.requestedJob.id"
                     label="${message(code:'loanListPerson.loanRequest.requestedJob.label',default:'requestedJob')}" />

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="loanRequest.requestedFromOrganizationId"
                     label="${message(code:'loanListPerson.requestedFromOrganizationId.label',default:'requestedFromOrganizationId')}" />

    <el:range type="date" size="6" name="loanRequest.fromDate"
              label="${message(code:'loanListPerson.loanRequest.fromDate.label')}"  />



</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="loanRequest.toDate"
              label="${message(code:'loanListPerson.loanRequest.toDate.label')}"  />

    <el:integerField name="loanRequest.numberOfPositions" size="6"
                     class=" isNumber"
                     label="${message(code:'loanListPerson.loanRequest.numberOfPositions.label',default:'numberOfPositions')}" />


</el:formGroup>


<el:formGroup>


    <el:range type="date" name="effectiveDate" size="6" class=""
              label="${message(code:'loanListPerson.effectiveDate.label',default:'effectiveDate')}" />

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="loanRequest.requestStatus" size="6"  class=""
               label="${message(code:'loanListPerson.loanRequest.requestStatus.label',default:'requestStatus')}" />

</el:formGroup>


<script>
    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }
</script>
