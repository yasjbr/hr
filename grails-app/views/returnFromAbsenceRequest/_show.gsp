<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'returnFromAbsenceRequest', action: 'list')}'"/>
    </div></div>
</div>

<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: returnFromAbsenceRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <lay:showElement value="${returnFromAbsenceRequest?.id}" type="String"
                     label="${message(code: 'returnFromAbsenceRequest.id.label', default: 'id')}"/>
    <lay:showElement value="${returnFromAbsenceRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'returnFromAbsenceRequest.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${returnFromAbsenceRequest?.requestStatus}" type="enum"
                     label="${message(code: 'returnFromAbsenceRequest.requestStatus.label', default: 'requestStatus')}"/>

    <lay:showElement value="${returnFromAbsenceRequest?.absence?.id}" type="String"
                     label="${message(code: 'returnFromAbsenceRequest.absence.label', default: 'absence')}"/>

    <lay:showElement value="${returnFromAbsenceRequest?.actualAbsenceReason}" type="enum"
                     label="${message(code: 'returnFromAbsenceRequest.actualAbsenceReason.label', default: 'actualAbsenceReason')}"
                     messagePrefix="EnumAbsenceReason"/>
    <lay:showElement value="${returnFromAbsenceRequest?.actualReturnDate}" type="ZonedDate"
                     label="${message(code: 'returnFromAbsenceRequest.actualReturnDate.label', default: 'actualReturnDate')}"/>
    <lay:showElement value="${returnFromAbsenceRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: returnFromAbsenceRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: returnFromAbsenceRequest]"/>
<br/>
<el:row/>
<div class="clearfix form-actions text-center">
    <g:if test="${returnFromAbsenceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'returnFromAbsenceRequest', action: 'edit', params: [encodedId: returnFromAbsenceRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${returnFromAbsenceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || returnFromAbsenceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || returnFromAbsenceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || returnFromAbsenceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="returnFromAbsenceList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'returnFromAbsenceRequest', action: 'goToList',
                            params: [encodedId: returnFromAbsenceRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>