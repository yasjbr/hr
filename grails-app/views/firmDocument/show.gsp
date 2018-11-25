<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmDocument.entity', default: 'FirmDocument List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'FirmDocument List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'firmDocument', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${firmDocument?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'firmDocument.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${firmDocument?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'firmDocument.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${firmDocument?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'firmDocument.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'firmDocument',action:'edit',params: [encodedId :firmDocument?.encodedId])}'"/>
</div>
</body>
</html>