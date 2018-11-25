<el:row/>
<g:render template="/employee/employeeShowWrapper" model="[employee: externalTransferRequest?.employee]"/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">


    <g:render template="/request/wrapperRequestShow" model="[request:externalTransferRequest]" />


    <lay:showElement value="${externalTransferRequest?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + externalTransferRequest?.currentEmploymentRecord?.department?.toString()}" type="String"
                     label="${message(code:'externalTransferRequest.oldEmploymentRecord.label',default:'oldEmploymentRecord')}" />


    <lay:showElement value="${externalTransferRequest?.fromFirm?.name}" type="String"
                     label="${message(code:'externalTransferRequest.fromFirm.label',default:'fromFirm')}" />

  <lay:showElement value="${externalTransferRequest?.transientData?.organizationDTO}" type="String"
                     label="${message(code:'externalTransferRequest.transientData.organizationDTO.label',default:'organization')}" />


    <lay:showElement value="${externalTransferRequest?.fromProvince?.descriptionInfo?.localName}" type="String"
                     label="${message(code:'externalTransferRequest.fromProvince.label',default:'fromProvince')}" />

    <lay:showElement value="${externalTransferRequest?.toProvince?.descriptionInfo?.localName}" type="String"
                     label="${message(code:'externalTransferRequest.toProvince.label',default:'toProvince')}" />


    <g:if test="${externalTransferRequest?.effectiveDate}">
     <lay:showElement value="${externalTransferRequest?.effectiveDate}" type="ZonedDate"
                      label="${message(code:'externalTransferRequest.effectiveDate.label',default:'effectiveDate')}" />


 </g:if>
    <g:if test="${externalTransferRequest?.hasClearance}">
        <lay:showElement value="${externalTransferRequest?.clearanceOrderNo}" type="String" label="${message(code:'externalTransferRequest.clearanceOrderNo.label',default:'clearanceOrderNo')}" />
        <lay:showElement value="${externalTransferRequest?.clearanceDate}" type="ZonedDate" label="${message(code:'externalTransferRequest.clearanceDate.label',default:'clearanceDate')}" />
        <lay:showElement value="${externalTransferRequest?.clearanceNote}" type="String" label="${message(code:'externalTransferRequest.clearanceNote.label',default:'clearanceNote')}" />

    </g:if>
    <g:if test="${externalTransferRequest?.hasTransferPermission}">
        <lay:showElement value="${externalTransferRequest?.transferPermissionOrderNo}" type="String" label="${message(code:'externalTransferRequest.transferPermissionOrderNo.label',default:'transferPermissionOrderNo')}" />
        <lay:showElement value="${externalTransferRequest?.transferPermissionDate}" type="ZonedDate" label="${message(code:'externalTransferRequest.transferPermissionDate.label',default:'transferPermissionDate')}" />
        <lay:showElement value="${externalTransferRequest?.transferPermissionNote}" type="String" label="${message(code:'externalTransferRequest.transferPermissionNote.label',default:'transferPermissionNote')}" />
    </g:if>

    <lay:showElement value="${externalTransferRequest?.requestStatus}" type="enum" label="${message(code:'externalTransferRequest.requestStatus.label',default:'requestStatus')}" messagePrefix="EnumRequestStatus" />
    <lay:showElement value="${externalTransferRequest?.requestStatusNote}" type="String" label="${message(code:'externalTransferRequest.requestStatusNote.label',default:'requestStatusNote')}" />
</lay:showWidget>
<el:row />
<g:render template="/request/wrapperManagerialOrderShow" model="[request: externalTransferRequest, colSize: 12]"/>
<el:row />
<g:render template="/request/wrapperShow" model="[request:externalTransferRequest]" />
<el:row />