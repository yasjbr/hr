<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'promotionList.entity', default: 'DispatchList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'DispatchList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'promotionList', action: 'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${promotionList?.code}" type="String"
                     label="${message(code: 'promotionList.code.label', default: 'code')}"/>
    <lay:showElement value="${promotionList?.name}" type="String"
                     label="${message(code: 'promotionList.name.label', default: 'name')}"/>
    <lay:showElement value="${promotionList?.receivingParty}" type="Enum"
                     label="${message(code: 'promotionList.receivingParty.label', default: 'receivingParty')}"/>
    <lay:showElement value="${promotionList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'promotionList.trackingInfo.dateCreatedUTC.label', default: 'trackingInfo dateCreatedUTC')}"/>
    <lay:showElement value="${promotionList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'promotionList.transientData.sendDate.label', default: 'sendDate')}"/>
    <lay:showElement value="${promotionList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'promotionList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${promotionList?.transientData?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'promotionList.transientData.receiveDate.label', default: 'receiveDate')}"/>
    <lay:showElement value="${promotionList?.manualIncomeNo}" type="String"
                     label="${message(code: 'promotionList.manualIncomeNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${promotionList?.transientData?.numberOfCompetitorsValue}" type="Integer"
                     label="${message(code: 'promotionList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>
    <lay:showElement value="${promotionList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'promotionList.currentStatus.correspondenceListStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${promotionList?.coverLetter}" type="String"
                     label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row/>
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'promotionList', action: 'edit', params: [encodedId: promotionList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>