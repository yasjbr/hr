<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'operationalTask.entity', default: 'OperationalTask List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'OperationalTask List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'operationalTask', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${operationalTask?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'operationalTask.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${operationalTask?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'operationalTask.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${operationalTask?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'operationalTask.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${operationalTask?.universalCode}" type="String" label="${message(code:'operationalTask.universalCode.label',default:'universalCode')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'operationalTask',action:'edit',params: [encodedId :operationalTask?.encodedId])}'"/>
</div>
</body>
</html>
