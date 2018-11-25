<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionList.entity', default: 'SuspensionList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'SuspensionList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'suspensionList', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionList?.code}" type="String"
                     label="${message(code: 'suspensionList.code.label', default: 'code')}"/>
    <lay:showElement value="${suspensionList?.name}" type="String"
                     label="${message(code: 'suspensionList.name.label', default: 'name')}"/>

    <lay:showElement value="${suspensionList?.receivingParty}" type="Enum"
                     label="${message(code: 'suspensionList.receivingParty.label', default: 'receivingParty')}"/>


    <lay:showElement value="${suspensionList?.trackingInfo?.dateCreatedUTC}" type="date"
                     label="${message(code: 'suspensionList.trackingInfo.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>
    <lay:showElement value="${suspensionList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'suspensionList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${suspensionList?.manualIncomeNo}" type="String"
                     label="${message(code: 'suspensionList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>
    <lay:showElement value="${suspensionList?.transientData?.numberOfCompetitorsValue}" type="String"
                     label="${message(code: 'suspensionList.transientData.numberOfCompetitorsValue.label', default: 'manualIncomeNo')}"/>
    <lay:showElement value="${suspensionList?.orderNo}" type="String"
                     label="${message(code: 'suspensionList.orderNo.label', default: 'orderNo')}"/>
    <lay:showElement value="${suspensionList?.receivingParty}" type="enum"
                     label="${message(code: 'suspensionList.receivingParty.label', default: 'receivingParty')}"
                     messagePrefix="EnumReceivingParty"/>
    <lay:showElement value="${suspensionList?.currentStatus?.correspondenceListStatus}" type="Enum"
                     label="${message(code: 'suspensionList.currentStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${suspensionList?.coverLetter}" type="String"
                     label="${message(code: 'suspensionList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>

<g:if test="${suspensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

    <div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'suspensionList', action: 'edit', params: [encodedId: suspensionList?.encodedId])}'"/>
    </div>
</g:if>
</body>
</html>