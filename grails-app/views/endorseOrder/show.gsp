<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'endorseOrder.entity', default: 'EndorseOrder List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EndorseOrder List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${endorseOrder?.effectiveDate}" type="ZonedDate" label="${message(code:'endorseOrder.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${endorseOrder?.orderNo}" type="String" label="${message(code:'endorseOrder.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${endorseOrder?.orderDate}" type="ZonedDate" label="${message(code:'endorseOrder.orderDate.label',default:'orderDate')}" />
    <lay:showElement value="${endorseOrder?.note}" type="String" label="${message(code:'endorseOrder.note.label',default:'note')}" />
</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'endorseOrder',action:'edit',params:[encodedId: endorseOrder?.encodedId])}'"/>
    <btn:backButton />
</div>
</body>
</html>