<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmSetting.entity', default: 'FirmSetting List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'FirmSetting List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'firmSetting', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${firmSetting?.propertyName}" type="String" label="${message(code:'firmSetting.propertyName.label',default:'propertyName')}" />
    <lay:showElement value="${firmSetting?.propertyValue}" type="String" label="${message(code:'firmSetting.propertyValue.label',default:'propertyValue')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'firmSetting',action:'edit',params: [encodedId :firmSetting?.encodedId])}'"/>

</div>
</body>
</html>