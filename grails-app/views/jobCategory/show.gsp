<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'jobCategory.entity', default: 'JobCategory List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'JobCategory List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'jobCategory', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${jobCategory?.descriptionInfo?.localName}" type="descriptionInfo"
                     label="${message(code: 'jobCategory.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobCategory?.descriptionInfo?.latinName}" type="descriptionInfo"
                     label="${message(code: 'jobCategory.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobCategory?.descriptionInfo?.hebrewName}" type="descriptionInfo"
                     label="${message(code: 'jobCategory.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${jobCategory?.description}" type="String"
                     label="${message(code: 'jobCategory.description.label', default: 'description')}"/>
    <lay:showElement value="${jobCategory?.universalCode}" type="String"
                     label="${message(code: 'jobCategory.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'jobCategory', action: 'edit', params: [encodedId: jobCategory?.encodedId])}'"/>
</div>
</body>
</html>