<g:set var="title"
       value="${message(code: 'stopVacationRequest.entity', default: 'stopVacationRequest')}"/>


<g:render template="/vacationRequest/wrapper" model="[vacationRequest: stopVacationRequest?.vacationRequest]"/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <g:render template="/request/wrapperRequestShow" model="[request: stopVacationRequest]"/>
    <lay:showElement value="${stopVacationRequest?.stopVacationDate}" type="ZonedDate"
                     label="${message(code: 'stopVacationRequest.stopVacationDate.label', default: 'stopVacationDate')}"/>
    <lay:showElement value="${stopVacationRequest?.stopVacationReason}" type="String"
                     label="${message(code: 'stopVacationRequest.stopVacationReason.label', default: 'stopVacationReason')}"/>
    <lay:showElement value="${stopVacationRequest?.byHR}" type="Boolean"
                     label="${message(code: 'stopVacationRequest.byHR.label', default: 'byHR')}"/>
    <lay:showElement value="${stopVacationRequest?.note}" type="String"
                     label="${message(code: 'stopVacationRequest.note.label', default: 'note')}"/>
    %{--<lay:showElement value="${stopVacationRequest?.sendEmail}" type="Boolean"--}%
                     %{--label="${message(code: 'stopVacationRequest.sendEmail.label', default: 'sendEmail')}"/>--}%
    %{--<lay:showElement value="${stopVacationRequest?.sendSMS}" type="Boolean"--}%
                     %{--label="${message(code: 'stopVacationRequest.sendSMS.label', default: 'sendSMS')}"/>--}%
</lay:showWidget>
<g:render template="/request/wrapperShow" model="[request: stopVacationRequest]"/>