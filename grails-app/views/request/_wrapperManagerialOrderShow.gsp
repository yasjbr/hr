<%def size= colSize?:6%>
<g:if test="${size==6}">
    <g:if test="${request?.internalOrderNumber}">
        <lay:showWidget size="6">
            <lay:showElement value="${request?.internalOrderNumber}" type="string"
                             label="${message(code: 'request.internalOrderNumber.label', default: 'Internal Order Number')}"/>

            <lay:showElement value="${request?.internalOrderDate}" type="ZonedDate"
                             label="${message(code: 'request.internalOrderDate.label', default: 'Internal Order Date')}"/>
        </lay:showWidget>
    </g:if>
    <g:if test="${request?.externalOrderNumber}">
        <lay:showWidget size="6">
            <lay:showElement value="${request?.externalOrderNumber}" type="string"
                             label="${message(code: 'request.externalOrderNumber.label', default: 'External Order Number')}"/>

            <lay:showElement value="${request?.externalOrderDate}" type="ZonedDate"
                             label="${message(code: 'request.externalOrderDate.label', default: 'External Order Date')}"/>
        </lay:showWidget>
    </g:if>
</g:if>
<g:else>
    <lay:showWidget size="12" title="${message(code: 'request.managerialOrderInfo.label')}">
    <g:if test="${request?.internalOrderNumber}">
        <lay:showElement value="${request?.internalOrderNumber}" type="string"
                         label="${message(code: 'request.internalOrderNumber.label', default: 'Internal Order Number')}"/>
        <lay:showElement value="${request?.internalOrderDate}" type="ZonedDate"
                         label="${message(code: 'request.internalOrderDate.label', default: 'Internal Order Date')}"/>
    </g:if>
    <g:if test="${request?.externalOrderNumber}">
        <lay:showElement value="${request?.externalOrderNumber}" type="string"
                         label="${message(code: 'request.externalOrderNumber.label', default: 'External Order Number')}"/>

        <lay:showElement value="${request?.externalOrderDate}" type="ZonedDate"
                         label="${message(code: 'request.externalOrderDate.label', default: 'External Order Date')}"/>
    </g:if>
    </lay:showWidget>
</g:else>