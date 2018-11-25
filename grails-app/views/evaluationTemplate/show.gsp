<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationTemplate.entity', default: 'EvaluationTemplate List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationTemplate List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationTemplate',action:'list')}'"/>
    </div></div>
</div>
<el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationTemplate?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'evaluationTemplate.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${evaluationTemplate?.templateType}" type="enum" label="${message(code:'evaluationTemplate.templateType.label',default:'templateType')}" messagePrefix="EnumEvaluationTemplateType" />
    <lay:showElement value="${evaluationTemplate?.evaluationCriteria}" type="Set" label="${message(code:'evaluationTemplate.evaluationCriteria.label',default:'evaluationCriteria')}" />
    <lay:showElement value="${evaluationTemplate?.evaluationSections}" type="Set" label="${message(code:'evaluationTemplate.evaluationSections.label',default:'evaluationSections')}" />

    <lay:showElement value="${evaluationTemplate?.transientData?.militaryRanks?.descriptionInfo?.localName}" type="Set" label="${message(code:'evaluationTemplate.militaryRank.label',default:'militaryRank')}" />
    <lay:showElement value="${evaluationTemplate?.transientData?.jobCategories?.descriptionInfo?.localName}" type="Set" label="${message(code:'evaluationTemplate.jobCategory.label',default:'jobCategory')}" />

    <lay:showElement value="${evaluationTemplate?.universalCode}" type="String" label="${message(code:'evaluationTemplate.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'evaluationTemplate', action: 'edit', params: [encodedId: evaluationTemplate?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true"/>
</div>
</body>
</html>