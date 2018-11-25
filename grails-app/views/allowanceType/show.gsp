<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceType.entity', default: 'AllowanceType List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'AllowanceType List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'allowanceType', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${allowanceType?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'allowanceType.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceType?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'allowanceType.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceType?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'allowanceType.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${allowanceType?.transientData?.relationshipTypeName}" type="string"
                     label="${message(code: 'allowanceType.transientData.relationshipTypeName.label', default: 'relationshipTypeName')}"/>
    <lay:showElement value="${allowanceType?.universalCode}" type="String"
                     label="${message(code: 'allowanceType.universalCode.label', default: 'universalCode')}"/>
</lay:showWidget>
<el:row/>

<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'allowanceType',action:'edit',params: [encodedId:"${allowanceType?.encodedId}"] )}'"/>
    
</div>

</body>
</html>