<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employmentCategory.entity', default: 'EmploymentCategory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmploymentCategory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'employmentCategory', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employmentCategory?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'employmentCategory.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${employmentCategory?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'employmentCategory.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${employmentCategory?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'employmentCategory.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${employmentCategory?.universalCode}" type="String" label="${message(code:'employmentCategory.universalCode.label',default:'universalCode')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'employmentCategory',action:'edit',params: [encodedId :employmentCategory?.encodedId] )}'"/>
</div>
</body>
</html>