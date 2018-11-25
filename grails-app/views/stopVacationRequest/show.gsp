<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'stopVacationRequest.entity', default: 'StopVacation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'StopVacation List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'stopVacationRequest',action:'list')}'"/>
    </div></div>
</div>

<g:render template="/vacationRequest/wrapper" model="[vacationRequest:stopVacationRequest?.vacationRequest]"/>



<lay:showWidget size="12" title="${title}">
    <g:render template="/request/wrapperRequestShow" model="[request: stopVacationRequest]"/>
    <lay:showElement value="${stopVacationRequest?.stopVacationDate}" type="ZonedDate" label="${message(code:'stopVacationRequest.stopVacationDate.label',default:'stopVacationDate')}" />
    <lay:showElement value="${stopVacationRequest?.stopVacationReason}" type="String" label="${message(code:'stopVacationRequest.stopVacationReason.label',default:'stopVacationReason')}" />
    <lay:showElement value="${stopVacationRequest?.byHR}" type="Boolean" label="${message(code:'stopVacationRequest.byHR.label',default:'byHR')}" />
    <lay:showElement value="${stopVacationRequest?.note}" type="String" label="${message(code:'stopVacationRequest.note.label',default:'note')}" />
    <lay:showElement value="${stopVacationRequest?.sendEmail}" type="Boolean" label="${message(code:'stopVacationRequest.sendEmail.label',default:'sendEmail')}" />
    <lay:showElement value="${stopVacationRequest?.sendSMS}" type="Boolean" label="${message(code:'stopVacationRequest.sendSMS.label',default:'sendSMS')}" />
</lay:showWidget>
<el:row />
<g:render template="/request/wrapperShow" model="[request: stopVacationRequest]"/>
</body>
</html>