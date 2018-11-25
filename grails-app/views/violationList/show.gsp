<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'violationList.entity', default: 'ViolationList List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'ViolationList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'violationList', action: 'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${violationList?.code}" type="String"
                     label="${message(code: 'violationList.code.label', default: 'code')}"/>
    <lay:showElement value="${violationList?.name}" type="String"
                     label="${message(code: 'violationList.name.label', default: 'name')}"/>
    <lay:showElement value="${violationList?.receivingParty}" type="Enum"
                     label="${message(code: 'violationList.receivingParty.label', default: 'receivingParty')}"/>
    <lay:showElement value="${violationList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate"
                     label="${message(code: 'violationList.trackingInfo.dateCreatedUTC.label', default: 'trackingInfo dateCreatedUTC')}"/>
    <lay:showElement value="${violationList?.transientData?.sendDate}" type="ZonedDate"
                     label="${message(code: 'violationList.transientData.sendDate.label', default: 'send date')}"/>
    <lay:showElement value="${violationList?.manualOutgoingNo}" type="String"
                     label="${message(code: 'violationList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"/>
    <lay:showElement value="${violationList?.transientData?.numberOfCompetitorsValue}" type="Integer"
                     label="${message(code: 'violationList.transientData.numberOfCompetitorsValue.label', default: 'numberOfCompetitorsValue')}"/>
    <lay:showElement value="${violationList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'violationList.currentStatus.correspondenceListStatus.label', default: 'currentStatus')}"/>
    <lay:showElement value="${violationList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row/>
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${violationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'violationList', action: 'edit', params: [encodedId: violationList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>