<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceList.entity', default: 'returnFromAbsenceList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'returnFromAbsenceList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${returnFromAbsenceList?.code}" type="String" label="${message(code:'returnFromAbsenceList.code.label',default:'code')}" />
    <lay:showElement value="${returnFromAbsenceList?.name}" type="String" label="${message(code:'returnFromAbsenceList.name.label',default:'name')}" />
    <lay:showElement value="${returnFromAbsenceList?.receivingParty}" type="Enum" label="${message(code:'returnFromAbsenceList.receivingParty.label',default:'receivingParty')}" />
    <lay:showElement value="${returnFromAbsenceList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'returnFromAbsenceList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${returnFromAbsenceList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'returnFromAbsenceList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${returnFromAbsenceList?.manualOutgoingNo}" type="String" label="${message(code:'returnFromAbsenceList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${returnFromAbsenceList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'returnFromAbsenceList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${returnFromAbsenceList?.manualIncomeNo}" type="String" label="${message(code:'returnFromAbsenceList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${returnFromAbsenceList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'returnFromAbsenceList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${returnFromAbsenceList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'returnFromAbsenceList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${returnFromAbsenceList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>

</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${returnFromAbsenceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'returnFromAbsenceList', action: 'edit', params: [encodedId: returnFromAbsenceList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>