<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionList.entity', default: 'petitionList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'petitionList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'petitionList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${petitionList?.code}" type="String" label="${message(code:'petitionList.code.label',default:'code')}" />
    <lay:showElement value="${petitionList?.name}" type="String" label="${message(code:'petitionList.name.label',default:'name')}" />
    <lay:showElement value="${petitionList?.receivingParty}" type="Enum" label="${message(code:'petitionList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${petitionList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'petitionList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${petitionList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'petitionList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${petitionList?.manualOutgoingNo}" type="String" label="${message(code:'petitionList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${petitionList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'petitionList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${petitionList?.manualIncomeNo}" type="String" label="${message(code:'petitionList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${petitionList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'petitionList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${petitionList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'petitionList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${petitionList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${petitionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'petitionList', action: 'edit', params: [encodedId: petitionList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>