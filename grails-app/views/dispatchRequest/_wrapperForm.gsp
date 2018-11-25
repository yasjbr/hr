<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "dispatchRequest.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">
            <lay:showElement value="${dispatchRequest?.dispatchType}" type="enum" label="${message(code: 'dispatchRequest.dispatchType.label', default: 'dispatchType')}" messagePrefix="EnumDispatchType"/>
            <lay:showElement value="${dispatchRequest?.fromDate}" type="ZonedDate"
                             label="${message(code: 'dispatchRequest.fromDate.label', default: 'fromDate')}"/>

            <g:if test="${dispatchRequest?.organizationId}">
                <lay:showElement value="${dispatchRequest?.transientData?.organizationDTO?.descriptionInfo?.localName}"
                                 type="Long"
                                 label="${message(code: 'dispatchRequest.organization.label', default: 'organizationId')}"/>
            </g:if>
            <g:else>
                <lay:showElement value="${dispatchRequest?.organizationName}" type="String"
                                 label="${message(code: 'dispatchRequest.organization.label', default: 'organizationId')}"/>
            </g:else>
            <lay:showElement value="${dispatchRequest?.transientData?.locationDTO}" type="Long"
                             label="${message(code: 'dispatchRequest.location.label', default: 'locationId')}"/>

        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${dispatchRequest?.periodInMonths}" type="Short"
                             label="${message(code: 'dispatchRequest.periodInMonths.label', default: 'periodInMonths')}"/>
            <lay:showElement value="${dispatchRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'dispatchRequest.toDate.label', default: 'toDate')}"/>

            <g:if test="${dispatchRequest?.educationMajorId}">
                <lay:showElement value="${dispatchRequest?.transientData?.educationMajorDTO?.descriptionInfo?.localName}"
                                 type="Long"
                                 label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajor')}"/>
            </g:if>
            <g:else>
                <lay:showElement value="${dispatchRequest?.educationMajorName}" type="String"
                                 label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajor')}"/>
            </g:else>

        </lay:showWidget>
        <el:row/>
    </lay:widgetBody>
</lay:widget>
