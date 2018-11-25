<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'militaryRank.entity', default: 'MilitaryRank List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'MilitaryRank List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'militaryRank', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${militaryRank?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'militaryRank.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${militaryRank?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'militaryRank.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${militaryRank?.descriptionInfo?.hebrewName }" type="DescriptionInfo" label="${message(code:'militaryRank.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${militaryRank?.nextMilitaryRank?.descriptionInfo?.localName}" type="string" label="${message(code:'militaryRank.nextMilitaryRank.label',default:'nextMilitaryRank')}" />
    <lay:showElement value="${militaryRank?.numberOfYearToPromote}" type="Short" label="${message(code:'militaryRank.numberOfYearToPromote.label',default:'numberOfYearToPromote')}" />
    <lay:showElement value="${militaryRank?.orderNo}" type="Short" label="${message(code:'militaryRank.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${militaryRank?.universalCode}" type="String" label="${message(code:'militaryRank.universalCode.label',default:'universalCode')}" />

</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'militaryRank',action:'edit',params:[encodedId: militaryRank?.encodedId])}'"/>
</div>
</body>
</html>