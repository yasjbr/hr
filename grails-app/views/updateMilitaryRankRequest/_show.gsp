<g:render template="/employee/employeeShowWrapper" model="[employee: updateMilitaryRankRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <lay:showElement value="${updateMilitaryRankRequest?.id}" type="String"
                     label="${message(code: 'updateMilitaryRankRequest.id.label', default: 'id')}"/>
    <lay:showElement value="${updateMilitaryRankRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'updateMilitaryRankRequest.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${updateMilitaryRankRequest?.requestStatus}" type="Enum"
                     label="${message(code: 'updateMilitaryRankRequest.requestStatus.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>
    <lay:showElement value="${updateMilitaryRankRequest?.requestType}" type="Enum"
                     label="${message(code: 'updateMilitaryRankRequest.requestType.label', default: 'requestType')}"
                     messagePrefix="EnumRequestType"/>
    <lay:showElement value="${updateMilitaryRankRequest?.dueDate}" type="ZonedDate"
                     label="${message(code: 'updateMilitaryRankRequest.dueDate.label', default: 'dueDate')}"/>

    <g:if test="${updateMilitaryRankRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.UPDATE_MILITARY_RANK_TYPE}">
        <lay:showElement value="${updateMilitaryRankRequest?.oldRankType}" type="MilitaryRankType"
                         label="${message(code: 'updateMilitaryRankRequest.oldRankType.label', default: 'oldRankType')}"/>
        <lay:showElement value="${updateMilitaryRankRequest?.newRankType}" type="MilitaryRankType"
                         label="${message(code: 'updateMilitaryRankRequest.newRankType.label', default: 'newRankType')}"/>
    </g:if>
    <g:else>
    <lay:showElement value="${updateMilitaryRankRequest?.oldRankClassification}" type="militaryRankClassification"
                     label="${message(code: 'updateMilitaryRankRequest.oldRankClassification.label', default: 'oldRankClassification')}"/>
    <lay:showElement value="${updateMilitaryRankRequest?.newRankClassification}" type="militaryRankClassification"
                     label="${message(code: 'updateMilitaryRankRequest.newRankClassification.label', default: 'newRankClassification')}"/>
    </g:else>

    <lay:showElement value="${updateMilitaryRankRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'updateMilitaryRankRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: updateMilitaryRankRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: updateMilitaryRankRequest]"/>
<br/>
<el:row/>







