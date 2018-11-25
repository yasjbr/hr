<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultList.entity', default: 'applicantInspectionResultList List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'applicantInspectionResultList List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultList',action:'list')}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantInspectionResultList?.code}" type="String" label="${message(code:'applicantInspectionResultList.code.label',default:'code')}" />
    <lay:showElement value="${applicantInspectionResultList?.name}" type="String" label="${message(code:'applicantInspectionResultList.name.label',default:'name')}" />
    <lay:showElement value="${applicantInspectionResultList?.transientData?.organizationName}" type="string" label="${message(code:'applicantInspectionResultList.coreOrganizationId.label',default:'coreOrganizationId')}" />
    <lay:showElement value="${applicantInspectionResultList?.trackingInfo?.dateCreatedUTC}" type="ZonedDate" label="${message(code:'applicantInspectionResultList.trackingInfo.dateCreatedUTC.label',default:'trackingInfo dateCreatedUTC')}" />
    <lay:showElement value="${applicantInspectionResultList?.transientData?.sendDate}" type="ZonedDate" label="${message(code:'applicantInspectionResultList.transientData.sendDate.label',default:'send date')}" />
    <lay:showElement value="${applicantInspectionResultList?.inspectionCategory?.descriptionInfo?.localName}" type="String" label="${message(code:'applicantInspectionResultList.inspectionCategory.label',default:'inspectionCategory')}" />
    <lay:showElement value="${applicantInspectionResultList?.inspection?.descriptionInfo?.localName}" type="String" label="${message(code:'applicantInspectionResultList.inspection.label',default:'inspection')}" />
    <lay:showElement value="${applicantInspectionResultList?.manualOutgoingNo}" type="String" label="${message(code:'applicantInspectionResultList.manualOutgoingNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${applicantInspectionResultList?.transientData?.receiveDate}" type="ZonedDate" label="${message(code:'applicantInspectionResultList.transientData.receiveDate.label',default:'send date')}" />
    <lay:showElement value="${applicantInspectionResultList?.manualIncomeNo}" type="String" label="${message(code:'applicantInspectionResultList.manualIncomeNo.label',default:'manualOutgoingNo')}" />
    <lay:showElement value="${applicantInspectionResultList?.transientData?.numberOfCompetitorsValue}" type="Integer" label="${message(code:'applicantInspectionResultList.transientData.numberOfCompetitorsValue.label',default:'numberOfCompetitorsValue')}" />
    <lay:showElement value="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus}" type="enum" label="${message(code:'applicantInspectionResultList.currentStatus.correspondenceListStatus.label',default:'currentStatus')}" />
    <lay:showElement value="${applicantInspectionResultList?.coverLetter}" type="String" label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"/>
</lay:showWidget>
<el:row />
<br/>

<div class="clearfix form-actions text-center">
    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'applicantInspectionResultList', action: 'edit', params: [encodedId: applicantInspectionResultList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>