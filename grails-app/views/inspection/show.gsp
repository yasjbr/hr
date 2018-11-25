<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'inspection.entity', default: 'Inspection List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Inspection List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'inspection', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${inspection?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'inspection.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspection?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'inspection.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspection?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'inspection.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspection?.inspectionCategory?.descriptionInfo?.localName}" type="inspectionCategory"
                     label="${message(code: 'inspection.inspectionCategory.label', default: 'inspectionCategory')}"/>
    <lay:showElement value="${inspection?.hasMark}" type="Boolean"
                     label="${message(code: 'inspection.hasMark.label', default: 'hasMark')}"/>
    <lay:showElement value="${inspection?.hasPeriod}" type="Boolean"
                     label="${message(code: 'inspection.hasPeriod.label', default: 'hasPeriod')}"/>
    <lay:showElement value="${inspection?.hasDates}" type="Boolean"
                     label="${message(code: 'inspection.hasDates.label', default: 'hasDates')}"/>
    %{--<lay:showElement value="${inspection?.isIncludedInLists}" type="Boolean"
                     label="${message(code: 'inspection.isIncludedInLists.label', default: 'isIncludedInLists')}"/>--}%
    <lay:showElement value="${inspection?.orderId}" type="short"
                     label="${message(code: 'inspection.orderId.label', default: 'orderId')}"/>
    <lay:showElement value="${inspection?.description}" type="String"
                     label="${message(code: 'inspection.description.label', default: 'description')}"/>
    <lay:showElement value="${inspection?.note}" type="String"
                     label="${message(code: 'inspection.note.label', default: 'note')}"/>
    <lay:showElement value="${inspection?.universalCode}" type="String"
                     label="${message(code: 'inspection.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<lay:showWidget size="12" title="${message(code: 'inspection.committeeRole.label', default: 'committee role')}">
    <g:each in="${inspection?.committeeRoles?.committeeRole}" var="committeeRole" status="index">
        <lay:showElement label="${index + 1}" size="12" value="${committeeRole}"/>
    </g:each>
</lay:showWidget>

<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'inspection', action: 'edit', params: [encodedId: inspection?.encodedId])}'"/>
</div>
</body>
</html>

