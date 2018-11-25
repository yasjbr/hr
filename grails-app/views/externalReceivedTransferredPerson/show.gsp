<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ExternalReceivedTransferredPerson List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'externalReceivedTransferredPerson',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${externalReceivedTransferredPerson?.transientData?.personDTO}" type="String" label="${message(code:'externalReceivedTransferredPerson.personId.label',default:'personId')}" />
    <lay:showElement value="${externalReceivedTransferredPerson?.orderNo}" type="String" label="${message(code:'externalReceivedTransferredPerson.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${externalReceivedTransferredPerson?.transientData?.fromOrganizationDTO}" type="String" label="${message(code:'externalReceivedTransferredPerson.fromOrganizationId.label',default:'orderNo')}" />
    <lay:showElement value="${externalReceivedTransferredPerson?.toDepartment}" type="Department" label="${message(code:'externalReceivedTransferredPerson.toDepartment.label',default:'toDepartment')}" />
    <lay:showElement value="${externalReceivedTransferredPerson?.effectiveDate}" type="ZonedDate" label="${message(code:'externalReceivedTransferredPerson.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${externalReceivedTransferredPerson?.note}" type="String" label="${message(code:'externalReceivedTransferredPerson.note.label',default:'note')}" />
</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'externalReceivedTransferredPerson', action: 'edit', params: [encodedId: externalReceivedTransferredPerson?.encodedId])}'"/>
    <btn:backButton />
</div>

</body>
</html>