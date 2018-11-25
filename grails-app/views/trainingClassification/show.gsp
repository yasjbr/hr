<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingClassification.entity', default: 'TrainingClassification List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'TrainingClassification List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'trainingClassification', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${trainingClassification?.descriptionInfo?.localName}" type="String" label="${message(code:'trainingClassification.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingClassification?.descriptionInfo?.latinName}" type="String" label="${message(code:'trainingClassification.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingClassification?.descriptionInfo?.hebrewName}" type="String" label="${message(code:'trainingClassification.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingClassification?.code}" type="String" label="${message(code:'trainingClassification.code.label',default:'code')}" />
    <lay:showElement value="${trainingClassification?.universalCode}" type="String" label="${message(code:'trainingClassification.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'trainingClassification',action:'edit',params: [encodedId:trainingClassification?.encodedId,backFunction:'show'])}'"/>
</div>
</body>
</html>