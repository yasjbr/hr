
<el:formGroup>


    <el:textField name="id" size="6"
                     class=""
                     label="${message(code:'internalTransferRequest.id.label',default:'id')}" />

    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>

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


    <el:autocomplete optionKey="id" optionValue="name" size="6"  controller="governorate" action="autocomplete"
                     name="fromGovernorate.id" label="${message(code:'externalTransferRequest.fromGovernorate.label',default:'fromGovernorate')}" />



</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""  controller="department" action="autocomplete"
                     name="fromDepartment.id" label="${message(code:'externalTransferRequest.fromDepartment.label',default:'fromDepartment')}" />


    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="toOrganizationId"
                     label="${message(code:'externalTransferRequest.toOrganizationId.label',default:'toOrganizationId')}" />

</el:formGroup>

<el:formGroup>

    <el:range type="date" name="effectiveDate"  size="6" class="" label="${message(code:'externalTransferRequest.effectiveDate.label',default:'effectiveDate')}" />


    <el:range type="date" name="requestDate" setMinDateFor="toRequestDate"  size="6" class=""
                  label="${message(code:'externalTransferRequest.requestDate.label',default:'requestDate')}" />

</el:formGroup>


<g:if test="${searchForList}">
    <el:hiddenField name="requestStatus"
                    value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW.toString()}"/>
</g:if>
<g:else>
    <el:formGroup>
        <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6"  class=""
                   label="${message(code:'externalTransferRequest.requestStatus.label',default:'requestStatus')}" />



        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                             controller="firm" action="autocomplete"
                             name="firmId" label="${message(code:'employee.firm.label',default:'firm')}" />
        </sec:ifAnyGranted>

    </el:formGroup>
</g:else>


<g:render template="/request/wrapperManagerialOrder" />

<script type="text/javascript">
    function sendFirmData(){
        return {"firm.id":"${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }


    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }

</script>