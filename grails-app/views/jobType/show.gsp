<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'jobType.entity', default: 'JobType List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'JobType List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'jobType', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${jobType?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'jobType.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobType?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'jobType.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobType?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'jobType.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobType?.universalCode}" type="String"
                     label="${message(code: 'jobType.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'jobType', action: 'edit', params: [encodedId: jobType?.encodedId])}'"/>
</div>
</body>
</html>