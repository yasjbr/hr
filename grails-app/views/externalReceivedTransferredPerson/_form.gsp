

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "externalReceivedTransferredPerson.transferInfo.label")}">
    <lay:widgetBody>


        <el:formGroup>
            <el:autocomplete optionKey="id" isDisabled="true" optionValue="name" size="8" class=" " controller="person" action="autocomplete"
                             name="personId" label="${message(code:'externalReceivedTransferredPerson.personId.label',default:'person')}"
                             values="${[[externalReceivedTransferredPerson?.transientData?.personDTO?.id,
                                         externalReceivedTransferredPerson?.transientData?.personDTO?.localFullName]]}" />

            <el:hiddenField name="personId" value="${externalReceivedTransferredPerson?.transientData?.personDTO?.id}" />
        </el:formGroup>

        <el:formGroup>
            <el:textField name="orderNo" size="8"  class=""
                          label="${message(code:'externalReceivedTransferredPerson.orderNo.label',default:'orderNo')}"
                          value="${externalReceivedTransferredPerson?.orderNo}"/>
        </el:formGroup>

        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="8"
                             paramsGenerateFunction="organizationParams"
                             controller="organization" action="autocomplete"
                             name="fromOrganizationId" class=" isRequired"
                             label="${message(code:'externalReceivedTransferredPerson.fromOrganizationId.label',default:'fromOrganizationId')}"
                             values="${[[externalReceivedTransferredPerson?.transientData?.fromOrganizationDTO?.id,
                                         externalReceivedTransferredPerson?.transientData?.fromOrganizationDTO?.descriptionInfo?.localName]]}" />

        </el:formGroup>

        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                             controller="department" action="autocomplete" name="toDepartment.id"
                             label="${message(code:'externalReceivedTransferredPerson.toDepartment.label',default:'toDepartment')}"
                             values="${[[externalReceivedTransferredPerson?.toDepartment?.id,
                                         externalReceivedTransferredPerson?.toDepartment?.descriptionInfo?.localName]]}" />
        </el:formGroup>


        <el:formGroup>
            <el:dateField name="effectiveDate"  size="8" class=" isRequired" isMaxDate="true"
                          label="${message(code:'externalReceivedTransferredPerson.effectiveDate.label',default:'effectiveDate')}"
                          value="${externalReceivedTransferredPerson?.effectiveDate}" />
        </el:formGroup>


        <el:formGroup>
            <el:textArea name="note" size="8"  class="" label="${message(code:'externalReceivedTransferredPerson.note.label',default:'note')}"
                         value="${externalReceivedTransferredPerson?.note}"/>
        </el:formGroup>


    </lay:widgetBody>

</lay:widget>



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