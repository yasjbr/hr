<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'loanNoticeReplayList.entity', default: 'List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'loanNoticeReplayList', action: 'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanNoticeReplayList?.code}" type="String"
                     label="${message(code: 'loanNoticeReplayList.code.label', default: 'code')}"/>
    <lay:showElement value="${loanNoticeReplayList?.name}" type="String"
                     label="${message(code: 'loanNoticeReplayList.name.label', default: 'name')}"/>
    <lay:showElement value="${loanNoticeReplayList?.receivingParty}" type="Enum"
                     label="${message(code: 'loanNoticeReplayList.receivingParty.label', default: 'receivingParty')}"/>
    <lay:showElement value="${loanNoticeReplayList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'loanNoticeReplayList.trackingInfo.dateCreatedUTC.label', default: 'trackingInfo dateCreatedUTC')}"/>
    <lay:showElement value="${loanNoticeReplayList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'loanNoticeReplayList.fromDate.label', default: 'send date')}"/>
    <lay:showElement value="${loanNoticeReplayList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'loanNoticeReplayList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${loanNoticeReplayList?.transientData?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'loanNoticeReplayList.toDate.label', default: 'send date')}"/>
    <lay:showElement value="${loanNoticeReplayList?.manualIncomeNo}" type="String"
                     label="${message(code: 'loanNoticeReplayList.manualIncomeNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${loanNoticeReplayList?.transientData?.numberOfCompetitorsValue}" type="Integer"
                     label="${message(code: 'loanNoticeReplayList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>
    <lay:showElement value="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'loanNoticeReplayList.currentStatus.correspondenceListStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${loanNoticeReplayList?.coverLetter}" type="String"
                     label="${message(code: 'loanNoticeReplayList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'loanNoticeReplayList', action: 'edit', params: [encodedId: loanNoticeReplayList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>