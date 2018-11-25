<el:hiddenField name="employee.id" value="${externalTransferRequest?.employee?.id}"/>
<el:hiddenField name="toEmploymentRecord.employee.id" value="${externalTransferRequest?.employee?.id}"/>
<el:hiddenField name="fromFirm" value="${externalTransferRequest?.employee?.firm?.id}"/>
<g:render template="/employee/wrapperForm" model="[employee: externalTransferRequest?.employee]"/>


<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="firm" action="autocomplete"
                             name="fromFirm" disabled="true"
                             label="${message(code: 'externalTransferRequest.fromFirm.label', default: 'fromFirm')}"
                             values="${[[externalTransferRequest?.employee?.firm?.id,
                                         externalTransferRequest?.employee?.firm?.name]]}"/>


            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             paramsGenerateFunction="organizationParams"
                             controller="organization" action="autocomplete"
                             name="toOrganizationId"
                             label="${message(code: 'externalTransferRequest.toOrganizationId.label', default: 'toOrganizationId')}"
                             values="${[[externalTransferRequest?.toOrganizationId,
                                         externalTransferRequest?.transientData?.organizationDTO?.toString()]]}"/>
        </el:formGroup>




        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                             controller="province" action="autocomplete"
                             name="fromProvince"
                             label="${message(code: 'externalTransferRequest.fromProvince.label', default: 'fromProvince')}"
                             values="${[[externalTransferRequest?.fromProvince?.id,
                                         externalTransferRequest?.fromProvince?.descriptionInfo?.localName]]}"/>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                             controller="province" action="autocomplete"
                             name="toProvince"
                             label="${message(code: 'externalTransferRequest.toProvince.label', default: 'toProvince')}"
                             values="${[[externalTransferRequest?.toProvince?.id,
                                         externalTransferRequest?.toProvince?.descriptionInfo?.localName]]}"/>
        </el:formGroup>

        <el:formGroup>

            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'externalTransferRequest.requestDate.label', default: 'requestDate')}"
                          value="${externalTransferRequest?.requestDate ?: java.time.ZonedDateTime.now()}"/>



            <el:textField name="requestReason" size="6" class=""
                          label="${message(code: 'externalTransferRequest.requestReason.label', default: 'requestReason')}"
                          value="${externalTransferRequest?.requestReason}"/>

        </el:formGroup>

        <el:formGroup>

            <el:textArea name="requestStatusNote" size="6"
                         class=""
                         label="${message(code: 'externalTransferRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${externalTransferRequest?.requestStatusNote}"/>

        </el:formGroup>

        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate"
                        title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder"
                              model="[request: externalTransferRequest, formName: 'externalTransferRequestForm',parentFolder:'externalTransferList']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>

        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>

    </lay:widgetBody>
</lay:widget>


<script type="text/javascript">
    function sendFirmData() {
        return {"firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }


    function organizationParams() {
        return {
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }

</script>