<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacationList.entity', default: 'VacationList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'VacationList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'vacationList', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacationList?.code}" type="String"
                     label="${message(code: 'vacationList.code.label', default: 'code')}"/>
    <lay:showElement value="${vacationList?.name}" type="String"
                     label="${message(code: 'vacationList.name.label', default: 'name')}"/>
    <lay:showElement value="${vacationList?.receivingParty}" type="enum"
                     label="${message(code: 'vacationList.receivingParty.label', default: 'receivingParty')}"
                     messagePrefix="EnumReceivingParty"/>

    <lay:showElement value="${vacationList?.trackingInfo?.dateCreatedUTC}" type="date"
                     label="${message(code: 'vacationList.trackingInfo.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>

        <lay:showElement value="${vacationList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'vacationList.transientData.sendDate.label', default: 'transientData.sendDate')}"/>
    <lay:showElement value="${vacationList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'vacationList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${vacationList?.transientData?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'vacationList.transientData.receiveDate.label', default: 'transientData.receiveDate')}"/>

    <lay:showElement value="${vacationList?.manualIncomeNo}" type="String"
                     label="${message(code: 'vacationList.manualIncomeNo.label', default: 'manualIncomeNo')}"/>
    <lay:showElement value="${vacationList?.transientData?.numberOfCompetitorsValue}" type="String"
                     label="${message(code: 'vacationList.transientData.numberOfCompetitorsValue.label', default: 'manualIncomeNo')}"/>
    <lay:showElement value="${vacationList?.currentStatus?.correspondenceListStatus}" type="Enum"
                     label="${message(code: 'vacationList.currentStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${vacationList?.coverLetter}" type="String"
                     label="${message(code: 'vacationList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <g:if test="${vacationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'vacationList', action: 'edit', params: [encodedId: vacationList?.encodedId])}'"/>

    </g:if>
    <btn:backButton withPreviousLink="true"/>

</div>
</body>
</html>