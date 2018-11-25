<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'inspectionCategory.entity', default: 'InspectionCategory List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'InspectionCategory List')}"/>
    <title>${title}</title>
</head>

<body>


<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'inspectionCategory', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${inspectionCategory?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'inspectionCategory.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspectionCategory?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'inspectionCategory.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspectionCategory?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'inspectionCategory.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${inspectionCategory?.description}" type="String"
                     label="${message(code: 'inspectionCategory.description.label', default: 'description')}"/>
    <lay:showElement value="${inspectionCategory?.isRequiredByFirmPolicy}" type="Boolean"
                     label="${message(code: 'inspectionCategory.isRequiredByFirmPolicy.label', default: 'isRequiredByFirmPolicy')}"/>
    <lay:showElement value="${inspectionCategory?.hasResultRate}" type="Boolean"
                     label="${message(code: 'inspectionCategory.hasResultRate.label', default: 'hasResultRate')}"/>
    <lay:showElement value="${inspectionCategory?.orderId}" type="short"
                     label="${message(code: 'inspectionCategory.orderId.label', default: 'orderId')}"/>
    <lay:showElement value="${inspectionCategory?.note}" type="String"
                     label="${message(code: 'inspectionCategory.note.label', default: 'note')}"/>
    <lay:showElement value="${inspectionCategory?.universalCode}" type="String"
                     label="${message(code: 'inspectionCategory.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<g:if test="${inspectionCategory?.committeeRoles}">
    <lay:showWidget size="12"
                    title="${message(code: 'inspectionCategory.committeeRole.label', default: 'committee role')}">
        <g:each in="${inspectionCategory?.committeeRoles?.committeeRole}" var="committeeRole" status="index">
            <lay:showElement label="${index + 1}" size="12" value="${committeeRole}"/>
        </g:each>
    </lay:showWidget>
</g:if>

<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'inspectionCategory', action: 'edit', params: [encodedId: inspectionCategory?.encodedId])}'"/>
</div>
</body>
</html>