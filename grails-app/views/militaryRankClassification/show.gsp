<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'militaryRankClassification.entity', default: 'MilitaryRankClassification List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'MilitaryRankClassification List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'militaryRankClassification',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${militaryRankClassification?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankClassification.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankClassification?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankClassification.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankClassification?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'militaryRankClassification.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${militaryRankClassification?.universalCode}" type="String"
                     label="${message(code: 'militaryRankClassification.universalCode.label', default: 'universalCode')}"/>

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'MilitaryRankClassification',action:'edit',params:[encodedId: militaryRankClassification?.encodedId])}'"/>
</div>

</body>
</html>