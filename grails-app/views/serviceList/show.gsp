<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />

<g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}" >
    <g:set var="entity" value="${message(code: 'recallToServiceList.entity', default: 'recallToServiceList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'recallToServiceList List')}" />
</g:if>
<g:else>
    <g:set var="entity" value="${message(code: 'endOfServiceList.entity', default: 'endOfServiceList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'endOfServiceList List')}" />
</g:else>
    <title>${title}</title>
</head>
<body>
<msg:page/>
<g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}" >
    <div style="margin-top: -46px">
        <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
            <btn:listButton onClick="window.location.href='${createLink(controller:'serviceList',action:'listReturnToServiceList')}'"/>
        </div></div>
    </div>
</g:if>
<g:else>
    <div style="margin-top: -46px">
        <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
            <btn:listButton onClick="window.location.href='${createLink(controller:'serviceList',action:'listEndOfServiceList')}'"/>
        </div></div>
    </div>
</g:else>

<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${serviceList?.code}" type="String" label="${message(code:'serviceList.code.label',default:'code')}" />
    <lay:showElement value="${serviceList?.name}" type="String" label="${message(code:'serviceList.name.label',default:'name')}" />
    <lay:showElement value="${serviceList?.receivingParty}" type="enum" label="${message(code:'serviceList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${serviceList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'serviceList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${serviceList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'serviceList.fromDate.label',default:'send date')}" />
    <lay:showElement value="${serviceList?.manualOutgoingNo}" type="String" label="${message(code:'serviceList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${serviceList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'serviceList.toDate.label',default:'send date')}" />
    <lay:showElement value="${serviceList?.manualIncomeNo}" type="String" label="${message(code:'serviceList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${serviceList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'serviceList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${serviceList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'serviceList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${serviceList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${serviceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'serviceList', action: 'edit', params: [encodedId: serviceList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>