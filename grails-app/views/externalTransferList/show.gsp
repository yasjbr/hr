<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'externalTransferList.entity', default: 'ExternalTransferList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'ExternalTransferList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${externalTransferList?.code}" type="String"
                     label="${message(code: 'externalTransferList.code.label', default: 'code')}"/>
    <lay:showElement value="${externalTransferList?.name}" type="String"
                     label="${message(code: 'externalTransferList.name.label', default: 'name')}"/>

    <lay:showElement value="${externalTransferList?.receivingParty}" type="Enum"
                     label="${message(code:'externalTransferList.receivingParty.label',default:'receivingParty')}" />

    <lay:showElement value="${externalTransferList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'externalTransferList.trackingInfo.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>

     <lay:showElement value="${externalTransferList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'externalTransferList.transientData.sendDate.label', default: 'dateCreatedUTC')}"/>

    <lay:showElement value="${externalTransferList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'externalTransferList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>

    <lay:showElement value="${externalTransferList?.transientData?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'externalTransferList.transientData.receiveDate.label', default: 'dateCreatedUTC')}"/>

    <lay:showElement value="${externalTransferList?.manualIncomeNo}" type="String"
                     label="${message(code: 'externalTransferList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>

    <lay:showElement value="${externalTransferList?.transientData?.numberOfCompetitorsValue}" type="String"
                     label="${message(code: 'externalTransferList.transientData.numberOfCompetitorsValue.label', default: 'manualIncomeNo')}"/>

    <lay:showElement value="${externalTransferList?.currentStatus?.correspondenceListStatus}" type="Enum"
                     label="${message(code: 'externalTransferList.currentStatus.label', default: 'currentStatus')}"/>

    <lay:showElement value="${externalTransferList?.coverLetter}" type="String"
                     label="${message(code: 'externalTransferList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>

<g:if test="${externalTransferList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

    <div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'edit', params: [encodedId: externalTransferList?.encodedId])}'"/>
    </div>
</g:if>
</body>
</html>