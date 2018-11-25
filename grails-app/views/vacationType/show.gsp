<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacationType.entity', default: 'VacationType List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'VacationType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'vacationType', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacationType?.descriptionInfo?.localName}" type="string" label="${message(code:'vacationType.descriptionInfo.localName.label',default:'local name')}" />
    <lay:showElement value="${vacationType?.descriptionInfo?.latinName}" type="string" label="${message(code:'vacationType.descriptionInfo.latinName.label',default:'latin name')}" />
    <lay:showElement value="${vacationType?.descriptionInfo?.hebrewName}" type="string" label="${message(code:'vacationType.descriptionInfo.hebrewName.label',default:'hebrew name')}" />
    <lay:showElement value="${vacationType?.transientData?.colorName}" type="string" label="${message(code:'vacationType.transientData.colorName.label',default:'color name')}" />

    <lay:showElement value="${vacationType?.excludedFromServicePeriod}" type="Boolean" label="${message(code:'vacationType.excludedFromServicePeriod.label',default:'excludedFromServicePeriod')}" />

    <lay:showElement value="${vacationType?.universalCode}" type="String" label="${message(code:'vacationType.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'vacationType',action:'edit',params:[encodedId:vacationType?.encodedId,backFunction:"show"])}'"/>
</div>
</body>
</html>