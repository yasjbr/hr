<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ProfileNoticeCategory List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'profileNoticeCategory',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${profileNoticeCategory?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'profileNoticeCategory.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${profileNoticeCategory?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'profileNoticeCategory.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${profileNoticeCategory?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'profileNoticeCategory.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${profileNoticeCategory?.description}" type="String"
                     label="${message(code: 'profileNoticeCategory.description.label', default: 'description')}"/>
    <lay:showElement value="${profileNoticeCategory?.firm}" type="Firm" label="${message(code:'profileNoticeCategory.firm.label',default:'firm')}" />
    <lay:showElement value="${profileNoticeCategory?.universalCode}" type="String" label="${message(code:'profileNoticeCategory.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />

</body>
</html>