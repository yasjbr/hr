<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'AllowanceStopReason List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'allowanceStopReason', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${allowanceStopReason?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'allowanceStopReason.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceStopReason?.descriptionInfo?.latinName}" type="string"
                     label="${message(code: 'allowanceStopReason.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceStopReason?.descriptionInfo?.hebrewName}" type="string"
                     label="${message(code: 'allowanceStopReason.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceStopReason?.universalCode}" type="String"
                     label="${message(code: 'allowanceStopReason.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<el:row/>

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'allowanceStopReason', action: 'edit', params: [encodedId: "${allowanceStopReason?.encodedId}"])}'"/>
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>