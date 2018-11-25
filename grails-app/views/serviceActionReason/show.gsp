<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'serviceActionReason.entity', default: 'ServiceActionReason List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'ServiceActionReason List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>
<el:row/>
<br/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'serviceActionReason', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">

    <g:render template="/DescriptionInfo/wrapperShow" model="[bean:serviceActionReason?.descriptionInfo]" />

    <lay:showElement value="${serviceActionReason?.serviceActionReasonType}" type="ServiceActionReasonType"
                     label="${message(code: 'serviceActionReason.serviceActionReasonType.label', default: 'serviceActionReasonType')}"/>

    <lay:showElement value="${serviceActionReason?.employeeStatusResult}" type="EmployeeStatus"
                     label="${message(code: 'serviceActionReason.employeeStatusResult.label', default: 'employeeStatusResult')}"/>

    %{--<lay:showElement value="${serviceActionReason?.allowReturnToService}" type="Boolean"--}%
                     %{--label="${message(code: 'serviceActionReason.allowReturnToService.label', default: 'allowReturnToService')}"/>--}%

    <lay:showElement value="${serviceActionReason?.universalCode}" type="String"
                     label="${message(code: 'serviceActionReason.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<el:row/>


<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'serviceActionReason', action: 'edit', params: [encodedId: serviceActionReason?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>