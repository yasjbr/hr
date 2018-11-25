<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'childList.entity', default: 'childList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'childList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'childList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${childList?.code}" type="String" label="${message(code:'childList.code.label',default:'code')}" />
    <lay:showElement value="${childList?.name}" type="String" label="${message(code:'childList.name.label',default:'name')}" />
    <lay:showElement value="${childList?.receivingParty}" type="Enum" label="${message(code:'childList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${childList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'childList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${childList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'childList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${childList?.manualOutgoingNo}" type="String" label="${message(code:'childList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${childList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'childList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${childList?.manualIncomeNo}" type="String" label="${message(code:'childList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${childList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'childList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${childList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'childList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${childList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${childList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'childList', action: 'edit', params: [encodedId: childList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>