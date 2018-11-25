<g:render template="/employee/wrapperForm" model="[employee:externalTransferRequest?.employee]"  />


<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <lay:showWidget size="6">

            <lay:showElement value="${externalTransferRequest?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + externalTransferRequest?.currentEmploymentRecord?.department?.toString()}" type="String"
                             label="${message(code:'externalTransferRequest.oldEmploymentRecord.label',default:'oldEmploymentRecord')}" />

            <lay:showElement value="${externalTransferRequest?.transientData?.organizationDTO}" type="String"
                             label="${message(code:'externalTransferRequest.transientData.organizationDTO.label',default:'organization')}" />

            <lay:showElement value="${externalTransferRequest?.effectiveDate}" type="ZonedDate"
                             label="${message(code:'externalTransferRequest.effectiveDate.label',default:'effectiveDate')}" />

            <lay:showElement value="${externalTransferRequest?.id}" type="long"
                             label="${message(code: 'request.id.label', default: 'id')}"/>

            <lay:showElement value="${externalTransferRequest?.requestDate}" type="ZonedDate"
                             label="${message(code: 'request.requestDate.label', default: 'requestDate')}"/>

            <lay:showElement value="${externalTransferRequest?.requestStatus}" type="enum"
                             label="${message(code: 'allowanceRequest.requestStatus.label', default: 'requestStatus')}"
                             messagePrefix="EnumRequestStatus"/>
        </lay:showWidget>


        <lay:showWidget size="6">

            <lay:showElement value="${externalTransferRequest?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                             label="${message(code:'request.dateCreatedUTC.label',default:'dateCreatedUTC')}" />

            <lay:showElement value="${externalTransferRequest?.trackingInfo?.createdBy}" type="String"
                             label="${message(code:'request.createdBy.label',default:'createdBy')}" />

            <lay:showElement value="${externalTransferRequest?.trackingInfo?.lastUpdatedUTC}" type="ZonedDate"
                             label="${message(code:'request.lastUpdatedUTC.label',default:'lastUpdatedUTC')}" />

            <lay:showElement value="${externalTransferRequest?.trackingInfo?.lastUpdatedBy}" type="String"
                             label="${message(code:'request.lastUpdatedBy.label',default:'lastUpdatedBy')}" />


            <lay:showElement value="${externalTransferRequest?.requestReason}" type="String"
                             label="${message(code:'request.requestReason.label',default:'requestReason')}" />

            <lay:showElement value="${externalTransferRequest?.requestStatusNote}"
                             type="String" label="${message(code:'request.requestStatusNote.label',default:'requestStatusNote')}" />

        </lay:showWidget>


    </lay:widgetBody>
</lay:widget>

<el:row />
<el:row />