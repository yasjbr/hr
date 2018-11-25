<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'attendanceType.entity', default: 'AttendanceType List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'AttendanceType List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'attendanceType', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${attendanceType?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'attendanceType.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${attendanceType?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'attendanceType.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${attendanceType?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'attendanceType.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${attendanceType?.universalCode}" type="String" label="${message(code:'attendanceType.universalCode.label',default:'universalCode')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'attendanceType',action:'edit',params: [encodedId:"${attendanceType?.encodedId}"] )}'"/>
</div>
</body>
</html>