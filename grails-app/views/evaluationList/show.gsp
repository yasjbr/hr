<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationList.entity', default: 'evaluationList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'evaluationList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationList?.code}" type="String" label="${message(code:'evaluationList.code.label',default:'code')}" />
    <lay:showElement value="${evaluationList?.name}" type="String" label="${message(code:'evaluationList.name.label',default:'name')}" />
    <lay:showElement value="${evaluationList?.receivingParty}" type="Enum" label="${message(code:'evaluationList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${evaluationList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'evaluationList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${evaluationList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'evaluationList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${evaluationList?.manualOutgoingNo}" type="String" label="${message(code:'evaluationList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${evaluationList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'evaluationList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${evaluationList?.manualIncomeNo}" type="String" label="${message(code:'evaluationList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${evaluationList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'evaluationList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${evaluationList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'evaluationList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${evaluationList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'evaluationList', action: 'edit', params: [encodedId: evaluationList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>