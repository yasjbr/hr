<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceList.entity', default: 'AllowanceList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'AllowanceList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'allowanceList', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${allowanceList?.code}" type="String"
                     label="${message(code: 'allowanceList.code.label', default: 'code')}"/>
    <lay:showElement value="${allowanceList?.name}" type="String"
                     label="${message(code: 'allowanceList.name.label', default: 'name')}"/>
    <lay:showElement value="${allowanceList?.receivingParty}" type="Enum"
                     label="${message(code: 'allowanceList.receivingParty.label', default: 'receivingParty')}"/>


    <lay:showElement value="${allowanceList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'allowanceList.trackingInfo.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>

    <lay:showElement value="${allowanceList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'allowanceList.transientData.sendDate.label', default: 'dateCreatedUTC')}"/>

    <lay:showElement value="${allowanceList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'allowanceList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>

    <lay:showElement value="${allowanceList?.transientData.receiveDate}" type="ZonedDate"
                     label="${message(code: 'allowanceList.transientData.receiveDate.label', default: 'dateCreatedUTC')}"/>

    <lay:showElement value="${allowanceList?.manualIncomeNo}" type="String"
                     label="${message(code: 'allowanceList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>

    <lay:showElement value="${allowanceList?.transientData?.numberOfCompetitorsValue}" type="String"
                     label="${message(code: 'allowanceList.transientData.numberOfCompetitorsValue.label', default: 'manualIncomeNo')}"/>
    <lay:showElement value="${allowanceList?.receivingParty}" type="enum"
                     label="${message(code: 'allowanceList.receivingParty.label', default: 'receivingParty')}"
                     messagePrefix="EnumReceivingParty"/>
    <lay:showElement value="${allowanceList?.currentStatus?.correspondenceListStatus}" type="Enum"
                     label="${message(code: 'allowanceList.currentStatus.label', default: 'currentStatus')}"/>

    <lay:showElement value="${allowanceList?.coverLetter}" type="String"
                     label="${message(code: 'allowanceList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>


<div class="clearfix form-actions text-center">
    <g:if test="${allowanceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'allowanceList', action: 'edit', params: [encodedId: allowanceList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>