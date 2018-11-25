<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingRejectionReason.entity', default: 'TrainingRejectionReason List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'TrainingRejectionReason List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'trainingRejectionReason',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${trainingRejectionReason?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'trainingRejectionReason.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingRejectionReason?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'trainingRejectionReason.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingRejectionReason?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'trainingRejectionReason.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingRejectionReason?.universalCode}" type="String" label="${message(code:'trainingRejectionReason.universalCode.label',default:'universalCode')}" />
    <lay:showElement value="${trainingRejectionReason?.description}" type="String" label="${message(code:'trainingRejectionReason.description.label',default:'description')}" />

</lay:showWidget>
<el:row />
<el:row />
<br/>

<div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'trainingRejectionReason', action: 'edit', params: [encodedId: trainingRejectionReason?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>