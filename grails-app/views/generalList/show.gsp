<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalList.entity', default: 'generalList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'generalList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'generalList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${generalList?.code}" type="String" label="${message(code:'generalList.code.label',default:'code')}" />
    <lay:showElement value="${generalList?.name}" type="String" label="${message(code:'generalList.name.label',default:'name')}" />
    <lay:showElement value="${generalList?.transientData?.organizationName}" type="string" label="${message(code:'generalList.coreOrganizationId.label',default:'coreOrganizationId')}" />
    <lay:showElement value="${generalList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'generalList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${generalList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'generalList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${generalList?.manualOutgoingNo}" type="String" label="${message(code:'generalList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${generalList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'generalList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${generalList?.manualIncomeNo}" type="String" label="${message(code:'generalList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${generalList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'generalList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${generalList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'generalList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${generalList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${generalList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'generalList', action: 'edit', params: [encodedId: generalList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>