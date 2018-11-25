<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationItem.entity', default: 'EvaluationItem List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EvaluationItem List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationItem',action:'list')}'"/>
    </div></div>
</div>
<el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationItem?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'evaluationItem.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${evaluationItem?.evaluationSection}" type="EvaluationSection" label="${message(code:'evaluationItem.evaluationSection.label',default:'evaluationSection')}" />
    <lay:showElement value="${evaluationItem?.index}" type="Integer" label="${message(code:'evaluationItem.index.label',default:'index')}" />
    <lay:showElement value="${evaluationItem?.maxMark}" type="Double" label="${message(code:'evaluationItem.maxMark.label',default:'maxMark')}" />
    <lay:showElement value="${evaluationItem?.universalCode}" type="String" label="${message(code:'evaluationItem.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'evaluationItem', action: 'edit', params: [encodedId: evaluationItem?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true"/>
</div>
</body>
</html>