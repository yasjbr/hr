<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'traineeList.entity', default: 'DispatchList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DispatchList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'traineeList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${traineeList?.code}" type="String" label="${message(code:'traineeList.code.label',default:'code')}" />
    <lay:showElement value="${traineeList?.name}" type="String" label="${message(code:'traineeList.name.label',default:'name')}" />
    <lay:showElement value="${traineeList?.receivingParty}" type="Enum" label="${message(code:'traineeList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${traineeList?.fromDate}" type="ZonedDate" label="${message(code:'traineeList.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${traineeList?.toDate}" type="ZonedDate" label="${message(code:'traineeList.toDate.label',default:'toDate')}" />
    <lay:showElement value="${traineeList?.transientData?.location}" type="String" label="${message(code:'traineeList.location.label',default:'locationId')}" />
    <lay:showElement value="${traineeList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'traineeList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${traineeList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'traineeList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${traineeList?.manualOutgoingNo}" type="String" label="${message(code:'traineeList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${traineeList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'traineeList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${traineeList?.manualIncomeNo}" type="String" label="${message(code:'traineeList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${traineeList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'traineeList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${traineeList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'traineeList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${traineeList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${traineeList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'traineeList', action: 'edit', params: [encodedId: traineeList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>