<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'AocCorrespondenceList List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: listController, action: listAction)}'"/>
    </div></div>
</div>
<br/><el:row/><br/>
<lay:showWidget size="12" title="${title}">
    %{--<lay:showElement value="${aocCorrespondenceList?.hrCorrespondenceList?.code}" type="String"--}%
                     %{--label="${message(code: 'allowanceList.code.label', default: 'code')}"/>--}%
    <lay:showElement value="${aocCorrespondenceList?.name}" type="String"
                     label="${message(code: 'aocCorrespondenceList.name.label', default: 'name')}"/>
    <lay:showElement value="${aocCorrespondenceList?.correspondenceType}" type="enum"
                     label="${message(code: 'aocCorrespondenceList.correspondenceType.label', default: 'correspondenceType')}"
                     messagePrefix="EnumCorrespondenceType"/>
    <lay:showElement value="${aocCorrespondenceList?.correspondenceDirection}" type="enum"
                     label="${message(code: 'aocCorrespondenceList.correspondenceDirection.label', default: 'correspondenceDirection')}"
                     messagePrefix="EnumCorrespondenceDirection"/>
    <lay:showElement value="${aocCorrespondenceList?.currentStatus}" type="enum"
                     label="${message(code: 'aocCorrespondenceList.currentStatus.label', default: 'currentStatus')}"
                     messagePrefix="EnumCorrespondenceStatus"/>
    <lay:showElement value="${aocCorrespondenceList?.receivingParty?.name}" type="string"
                     label="${message(code: 'aocCorrespondenceList.TO.name.label', default: 'receivingParty')}"/>
    <lay:showElement value="${aocCorrespondenceList?.sendingParty?.name}" type="string"
                     label="${message(code: 'aocCorrespondenceList.FROM.name.label', default: 'sendingParty')}"/>
    <lay:showElement value="${aocCorrespondenceList?.archivingDate}" type="ZonedDate"
                     label="${message(code: 'aocCorrespondenceList.archivingDate.label', args: [message(code: 'EnumCorrespondenceDirection.' + aocCorrespondenceList?.correspondenceDirection)], default: 'archivingDate')}"/>
    <lay:showElement value="${aocCorrespondenceList?.incomingSerial}" type="String"
                     label="${message(code: 'aocCorrespondenceList.incomingSerial.label', default: 'incomingSerial')}"/>
    <lay:showElement value="${aocCorrespondenceList?.outgoingSerial}" type="String"
                     label="${message(code: 'aocCorrespondenceList.outgoingSerial.label', default: 'outgoingSerial')}"/>
    <lay:showElement value="${aocCorrespondenceList?.deliveryDate}" type="ZonedDate"
                     label="${message(code: 'aocCorrespondenceList.deliveryDate.label', default: 'deliveryDate')}"/>
    <lay:showElement value="${aocCorrespondenceList?.deliveredBy}" type="String"
                     label="${message(code: 'aocCorrespondenceList.deliveredBy.label', default: 'deliveredBy')}"/>

    <lay:showElement value="${aocCorrespondenceList?.province?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'aocCorrespondenceList.province.label', default: 'province')}"/>

 <lay:showElement value="${aocCorrespondenceList?.provinceLocation?.transientData?.locationName}" type="String"
                     label="${message(code: 'aocCorrespondenceList.provinceLocation.label', default: 'provinceLocation')}"/>

    <lay:showElement value="${aocCorrespondenceList?.coverLetter}" type="String"
                     label="${message(code: 'allowanceList.coverLetter.label', default: 'coverLetter')}"/>
    <lay:showElement value="${aocCorrespondenceList?.notes}" type="String"
                     label="${message(code: 'aocCorrespondenceList.notes.label', default: 'notes')}"/>

</lay:showWidget>
<el:row/>

<g:if test="${!aocCorrespondenceList?.copyToPartyList?.isEmpty()}">
    <g:render template="copyToPartyList" model="[copyToPartyList:aocCorrespondenceList?.copyToPartyList, showActions:false]" />
</g:if>


<div class="clearfix form-actions text-center">
    <g:if test="${aocCorrespondenceList?.currentStatus == ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'aocCorrespondenceList', action: 'edit', params: [encodedId: aocCorrespondenceList?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" />
</div>


</body>
</html>