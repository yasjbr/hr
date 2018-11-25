<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'committee.entity', default: 'Committee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Committee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'committee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${committee?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'committee.descriptionInfo.label',default:'descriptionInfo')}" />
</lay:showWidget>
<el:row />

</body>
</html>