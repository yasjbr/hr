<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'dispatchList.entity', default: 'DispatchList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DispatchList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'dispatchList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${dispatchList?.code}" type="String" label="${message(code:'dispatchList.code.label',default:'code')}" />
    <lay:showElement value="${dispatchList?.name}" type="String" label="${message(code:'dispatchList.name.label',default:'name')}" />
    <lay:showElement value="${dispatchList?.receivingParty}" type="enum" label="${message(code:'dispatchList.receivingParty.label',default:'receivingParty')}" />
    %{--<lay:showElement value="${dispatchList?.dispatchListType}" type="enum" messagePrefix="EnumDispatchListType" label="${message(code:'dispatchList.dispatchListType.label',default:'name')}" />--}%
    <lay:showElement value="${dispatchList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'dispatchList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${dispatchList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'dispatchList.fromDate.label',default:'send date')}" />
    <lay:showElement value="${dispatchList?.manualOutgoingNo}" type="String" label="${message(code:'dispatchList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${dispatchList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'dispatchList.toDate.label',default:'send date')}" />
    <lay:showElement value="${dispatchList?.manualIncomeNo}" type="String" label="${message(code:'dispatchList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${dispatchList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'dispatchList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${dispatchList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'dispatchList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${dispatchList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${dispatchList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'dispatchList', action: 'edit', params: [encodedId: dispatchList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>