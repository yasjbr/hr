<% def size = colSize ?: 6 %>
<g:if test="${size == 6}">
    <lay:showElement value="${request?.id}" type="long"
                     label="${message(code: 'request.id.label', default: 'id')}"/>

    <lay:showElement value="${request?.requestStatus}" type="enum"
                     label="${message(code: 'allowanceRequest.requestStatus.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>
    <lay:showElement value="${request?.requestDate}" type="ZonedDate"
                     label="${message(code: 'request.requestDate.label', default: 'requestDate')}"/>

    <lay:showElement value="${request?.requestTypeDescription}" type="string"
                     label="${message(code: 'allowanceRequest.requestType.label', default: 'requestType')}"/>
    <g:if test="${hasEmployee}">
        <lay:showElement value="${request?.employee}" type="Employee"
                         label="${message(code: 'request.employee.label', default: 'employee')}"/>
    </g:if>

    <g:if test="${request?.extraInfo}">
        <lay:showElement value="${request?.extraInfo?.allLevels}" type="Boolean"
                         label="${message(code: 'request.allLevels.label', default: 'allLevels')}"/>
    </g:if>
</g:if>
<g:else>

    <lay:showElement value="${request?.id}" type="long"
                     label="${message(code: 'request.id.label', default: 'id')}"/>

    <lay:showElement value="${request?.requestStatus}" type="enum"
                     label="${message(code: 'allowanceRequest.requestStatus.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>

    <lay:showElement value="${request?.requestDate}" type="ZonedDate"
                     label="${message(code: 'request.requestDate.label', default: 'requestDate')}"/>

    <lay:showElement value="${request?.requestTypeDescription}" type="string"
                     label="${message(code: 'allowanceRequest.requestType.label', default: 'requestType')}"/>

    <g:if test="${hasEmployee}">
        <lay:showElement value="${request?.employee}" type="Employee"
                         label="${message(code: 'request.employee.label', default: 'employee')}"/>

    </g:if>
</g:else>