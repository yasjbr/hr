

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="person" action="autocomplete"
                     name="personId" label="${message(code:'externalReceivedTransferredPerson.personId.label',default:'person')}" />

    <el:textField name="orderNo" size="6"  class="" label="${message(code:'externalReceivedTransferredPerson.orderNo.label',default:'orderNo')}" />



</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     paramsGenerateFunction="organizationParams"
                     controller="organization" action="autocomplete"
                     name="fromOrganizationId"
                     label="${message(code:'externalReceivedTransferredPerson.fromOrganizationId.label',default:'fromOrganizationId')}" />


    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""  controller="department" action="autocomplete"
                     name="toDepartment.id" label="${message(code:'externalReceivedTransferredPerson.toDepartment.label',default:'toDepartment')}" />


</el:formGroup>

<el:formGroup>

    <el:range type="date" name="effectiveDate"  size="6" class=""
              label="${message(code:'externalReceivedTransferredPerson.effectiveDate.label',default:'effectiveDate')}" />

</el:formGroup>

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