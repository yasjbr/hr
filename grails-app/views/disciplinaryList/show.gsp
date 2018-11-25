<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryList.entity', default: 'DisciplinaryList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DisciplinaryList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<g:set var="sendDate" value="${disciplinaryList?.correspondenceListStatuses?.find{it.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.SUBMITTED }?.fromDate}" />

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryList',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${disciplinaryList?.code}" type="String" label="${message(code:'disciplinaryList.code.label',default:'code')}" />
    <lay:showElement value="${disciplinaryList?.name}" type="String" label="${message(code:'disciplinaryList.name.label',default:'name')}" />
    <lay:showElement value="${disciplinaryList?.receivingParty}" type="Enum" label="${message(code:'disciplinaryList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${disciplinaryList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'disciplinaryList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${disciplinaryList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'disciplinaryList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${disciplinaryList?.manualOutgoingNo}" type="String" label="${message(code:'disciplinaryList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${disciplinaryList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'disciplinaryList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${disciplinaryList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'disciplinaryList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${disciplinaryList?.coverLetter}" type="String"
                     label="${message(code: 'disciplinaryList.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <g:if test="${disciplinaryList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'disciplinaryList', action: 'edit', params: [encodedId: disciplinaryList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>