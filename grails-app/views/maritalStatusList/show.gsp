<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusList.entity', default: 'maritalStatusList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'maritalStatusList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${maritalStatusList?.code}" type="String" label="${message(code:'maritalStatusList.code.label',default:'code')}" />
    <lay:showElement value="${maritalStatusList?.name}" type="String" label="${message(code:'maritalStatusList.name.label',default:'name')}" />
    <lay:showElement value="${maritalStatusList?.receivingParty}" type="Enum" label="${message(code:'maritalStatusList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${maritalStatusList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'maritalStatusList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${maritalStatusList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'maritalStatusList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${maritalStatusList?.manualOutgoingNo}" type="String" label="${message(code:'maritalStatusList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${maritalStatusList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'maritalStatusList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${maritalStatusList?.manualIncomeNo}" type="String" label="${message(code:'maritalStatusList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${maritalStatusList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'maritalStatusList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${maritalStatusList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'maritalStatusList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${maritalStatusList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${maritalStatusList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'maritalStatusList', action: 'edit', params: [encodedId: maritalStatusList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>