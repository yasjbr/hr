<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <g:render template="/request/wrapperRequestShow" model="[request: dispatchRequest]"/>
    <lay:showElement value="${dispatchRequest?.dispatchType}" type="enum" label="${message(code: 'dispatchRequest.dispatchType.label', default: 'dispatchType')}" messagePrefix="EnumDispatchType"/>
    <lay:showElement value="${dispatchRequest?.fromDate}" type="ZonedDate"
                     label="${message(code: 'dispatchRequest.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${dispatchRequest?.toDate}" type="ZonedDate"
                     label="${message(code: 'dispatchRequest.toDate.label', default: 'toDate')}"/>

    <lay:showElement value="${dispatchRequest?.nextVerificationDate}" type="ZonedDate"
                     label="${message(code: 'dispatchRequest.nextVerificationDate.label', default: 'nextVerificationDate')}"/>
    <lay:showElement value="${dispatchRequest?.periodInMonths}" type="Short"
                     label="${message(code: 'dispatchRequest.periodInMonths.label', default: 'periodInMonths')}"/>

    <lay:showElement value="${dispatchRequest?.transientData?.locationDTO}" type="Long"
                     label="${message(code: 'dispatchRequest.location.label', default: 'locationId')}"/>

    <g:if test="${dispatchRequest?.organizationId}">
        <lay:showElement value="${dispatchRequest?.transientData?.organizationDTO?.descriptionInfo?.localName}"
                         type="Long"
                         label="${message(code: 'dispatchRequest.organization.label', default: 'organizationId')}"/>
    </g:if>
    <g:else>
        <lay:showElement value="${dispatchRequest?.organizationName}" type="String"
                         label="${message(code: 'dispatchRequest.organization.label', default: 'organizationId')}"/>
    </g:else>

    <g:if test="${dispatchRequest?.educationMajorId}">
        <lay:showElement value="${dispatchRequest?.transientData?.educationMajorDTO?.descriptionInfo?.localName}"
                         type="Long"
                         label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajor')}"/>
    </g:if>
    <g:elseif test="${dispatchRequest?.educationMajorName}">
        <lay:showElement value="${dispatchRequest?.educationMajorName}" type="String"
                         label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajor')}"/>
    </g:elseif>
    <g:else>
        <lay:showElement value="${dispatchRequest?.trainingName}" type="String"
                         label="${message(code: 'dispatchRequest.trainingName.label', default: 'trainingName')}"/>
    </g:else>
    <lay:showElement value="${dispatchRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'dispatchRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: dispatchRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: dispatchRequest]"/>
<br/>
<el:row/>