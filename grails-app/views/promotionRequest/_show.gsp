<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: promotionRequest?.employee]"/>

<el:row/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <lay:showElement value="${promotionRequest?.id}" type="String"
                     label="${message(code: 'promotionRequest.id.label', default: 'id')}"/>
    <lay:showElement value="${promotionRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'promotionRequest.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${promotionRequest?.requestStatus}" type="enum"
                     label="${message(code: 'promotionRequest.requestStatus.label', default: 'requestStatus')}"/>
    <lay:showElement value="${promotionRequest?.requestType}" type="enum"
                     label="${message(code: 'promotionRequest.requestType.label', default: 'requestType')}"/>
    <g:if test="${promotionRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.SITUATION_SETTLEMENT}">
        <lay:showElement
                value="${promotionRequest?.transientData?.educationDegreeDTO?.descriptionInfo?.localName}"
                type="Long"
                label="${message(code: 'promotionRequest.educationDegreeId.label', default: 'educationDegreeId')}"/>
    </g:if>
    <lay:showElement value="${promotionRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'promotionRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: promotionRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: promotionRequest]"/>
<el:row/>
<el:row/>
