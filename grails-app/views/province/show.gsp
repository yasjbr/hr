<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'province.entity', default: 'Province List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Province List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'province', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${province?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'province.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${province?.descriptionInfo?.hebrewName}" type="String"
                     label="${message(code: 'province.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${province?.descriptionInfo?.latinName}" type="String"
                     label="${message(code: 'province.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${province?.note}" type="String"
                     label="${message(code: 'province.note.label', default: 'note')}"/>
    <lay:showElement value="${province?.universalCode}" type="String"
                     label="${message(code: 'province.universalCode.label', default: 'universalCode')}"/>
    <lay:showElement value="${province?.transientData?.locationDTOList}" type="Set"
                     label="${message(code: 'province.provinceLocations.label', default: 'provinceLocations')}"/>
</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'province', action: 'edit', params: [encodedId: province?.encodedId])}'"/>
<btn:backButton withPreviousLink="true"/>
</div>
</body>
</html>