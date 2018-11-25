<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentList.entity', default: 'DispatchList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DispatchList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${recruitmentList?.code}" type="String" label="${message(code:'recruitmentList.code.label',default:'code')}" />
    <lay:showElement value="${recruitmentList?.name}" type="String" label="${message(code:'recruitmentList.name.label',default:'name')}" />
    <lay:showElement value="${recruitmentList?.receivingParty}" type="Enum" label="${message(code:'recruitmentList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${recruitmentList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'recruitmentList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${recruitmentList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'recruitmentList.fromDate.label',default:'send date')}" />
    <lay:showElement value="${recruitmentList?.manualOutgoingNo}" type="String" label="${message(code:'recruitmentList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${recruitmentList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'recruitmentList.toDate.label',default:'send date')}" />
    <lay:showElement value="${recruitmentList?.manualIncomeNo}" type="String" label="${message(code:'recruitmentList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${recruitmentList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'recruitmentList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${recruitmentList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'recruitmentList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${recruitmentList?.coverLetter}" type="String"
                     label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${recruitmentList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'recruitmentList', action: 'edit', params: [encodedId: recruitmentList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>