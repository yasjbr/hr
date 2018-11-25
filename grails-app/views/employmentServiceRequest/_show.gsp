<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: employmentServiceRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <lay:showElement value="${employmentServiceRequest?.id}" type="String" label="${message(code:'employmentServiceRequest.id.label',default:'id')}" />
    <lay:showElement value="${employmentServiceRequest?.requestDate}" type="ZonedDate" label="${message(code:'employmentServiceRequest.requestDate.label',default:'requestDate')}" />
    <lay:showElement value="${employmentServiceRequest?.requestStatus}" type="enum" label="${message(code:'employmentServiceRequest.requestStatus.label',default:'requestStatus')}" />

    <lay:showElement value="${employmentServiceRequest?.serviceActionReason}" type="ServiceActionReason"
                     label="${message(code: 'employmentServiceRequest.serviceActionReason.label', default: 'serviceActionReason')}"/>

    <lay:showElement value="${employmentServiceRequest?.expectedDateEffective}" type="ZonedDate"
                     label="${message(code: 'employmentServiceRequest.expectedDateEffective.label', default: 'expectedDateEffective')}"/>

    <lay:showElement value="${employmentServiceRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'employmentServiceRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: employmentServiceRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: employmentServiceRequest]"/>
<br/>
<el:row/>