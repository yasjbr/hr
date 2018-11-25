<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmActiveModule.entity', default: 'FirmActiveModule List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'FirmActiveModule List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'firmActiveModule', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${firmActiveModule?.systemModule}" type="enum" label="${message(code:'firmActiveModule.systemModule.label',default:'systemModule')}" messagePrefix="EnumSystemModule" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'firmActiveModule',action:'list')}'"/>
</div>
</body>
</html>