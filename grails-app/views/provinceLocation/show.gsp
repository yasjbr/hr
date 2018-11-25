<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'provinceLocation.entity', default: 'ProvinceLocation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ProvinceLocation List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'provinceLocation',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${provinceLocation?.locationId}" type="Long" label="${message(code:'provinceLocation.locationId.label',default:'locationId')}" />
    <lay:showElement value="${provinceLocation?.province}" type="Province" label="${message(code:'provinceLocation.province.label',default:'province')}" />
</lay:showWidget>
<el:row />

</body>
</html>