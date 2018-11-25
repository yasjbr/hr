<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'committeeRole.entity', default: 'CommitteeRole List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'CommitteeRole List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'committeeRole', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${committeeRole?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'committeeRole.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${committeeRole?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'committeeRole.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${committeeRole?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'committeeRole.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${committeeRole?.universalCode}" type="String" label="${message(code:'committeeRole.universalCode.label',default:'universalCode')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'committeeRole',action:'list')}'"/>
    <btn:editButton onClick="window.location.href='${createLink(controller:'committeeRole',action:'edit',params: [encodedId:committeeRole?.encodedId])}'"/>
</div>
</body>
</html>