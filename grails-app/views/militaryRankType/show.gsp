<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'militaryRankType.entity', default: 'MilitaryRankType List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'MilitaryRankType List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'militaryRankType', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${militaryRankType?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankType.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankType?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankType.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankType?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankType.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankType?.universalCode}" type="String"
                     label="${message(code: 'militaryRankType.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'militaryRankType', action: 'edit', params: [encodedId: militaryRankType?.encodedId])}'"/>
</div>
</body>
</html>