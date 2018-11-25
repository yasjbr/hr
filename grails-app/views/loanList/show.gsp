<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'loanList.entity', default: 'List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'loanList', action: 'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanList?.code}" type="String"
                     label="${message(code: 'loanList.code.label', default: 'code')}"/>
    <lay:showElement value="${loanList?.name}" type="String"
                     label="${message(code: 'loanList.name.label', default: 'name')}"/>
    <lay:showElement value="${loanList?.receivingParty}" type="Enum"
                     label="${message(code: 'loanList.receivingParty.label', default: 'receivingParty')}"/>

    <lay:showElement value="${loanList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'loanList.trackingInfo.dateCreatedUTC.label', default: 'trackingInfo dateCreatedUTC')}"/>
    <lay:showElement value="${loanList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'loanList.fromDate.label', default: 'send date')}"/>
    <lay:showElement value="${loanList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'loanList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${loanList?.transientData?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'loanList.toDate.label', default: 'send date')}"/>
    <lay:showElement value="${loanList?.manualIncomeNo}" type="String"
                     label="${message(code: 'loanList.manualIncomeNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${loanList?.transientData?.numberOfCompetitorsValue}" type="Integer"
                     label="${message(code: 'loanList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>
    <lay:showElement value="${loanList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'loanList.currentStatus.correspondenceListStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${loanList?.coverLetter}" type="String"
                     label="${message(code: 'loanList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${loanList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'loanList', action: 'edit', params: [encodedId: loanList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>