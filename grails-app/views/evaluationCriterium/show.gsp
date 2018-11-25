<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationCriterium.entity', default: 'EvaluationCriterium List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationCriterium List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationCriterium',action:'list')}'"/>
    </div></div>
</div>
<el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationCriterium?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'evaluationCriterium.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${evaluationCriterium?.evaluationTemplate}" type="EvaluationTemplate" label="${message(code:'evaluationCriterium.evaluationTemplate.label',default:'evaluationTemplate')}" />
    <lay:showElement value="${evaluationCriterium?.fromMark}" type="Double" label="${message(code:'evaluationCriterium.fromMark.label',default:'fromMark')}" />
    <lay:showElement value="${evaluationCriterium?.toMark}" type="Double" label="${message(code:'evaluationCriterium.toMark.label',default:'toMark')}" />
    <lay:showElement value="${evaluationCriterium?.universalCode}" type="String" label="${message(code:'evaluationCriterium.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'evaluationCriterium', action: 'edit', params: [encodedId: evaluationCriterium?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true"/>
</div>
</body>
</html>