<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionList.entity', default: 'suspensionExtensionList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'suspensionExtensionList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionExtensionList?.code}" type="String" label="${message(code:'suspensionExtensionList.code.label',default:'code')}" />
    <lay:showElement value="${suspensionExtensionList?.name}" type="String" label="${message(code:'suspensionExtensionList.name.label',default:'name')}" />
    <lay:showElement value="${suspensionExtensionList?.receivingParty}" type="Enum" label="${message(code:'suspensionExtensionList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${suspensionExtensionList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'suspensionExtensionList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${suspensionExtensionList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'suspensionExtensionList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${suspensionExtensionList?.manualOutgoingNo}" type="String" label="${message(code:'suspensionExtensionList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${suspensionExtensionList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'suspensionExtensionList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${suspensionExtensionList?.manualIncomeNo}" type="String" label="${message(code:'suspensionExtensionList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${suspensionExtensionList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'suspensionExtensionList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${suspensionExtensionList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'suspensionExtensionList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${suspensionExtensionList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${suspensionExtensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'suspensionExtensionList', action: 'edit', params: [encodedId: suspensionExtensionList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>