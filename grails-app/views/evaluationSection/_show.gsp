<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationSection?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'evaluationSection.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${evaluationSection?.evaluationTemplate}" type="EvaluationTemplate" label="${message(code:'evaluationSection.evaluationTemplate.label',default:'evaluationTemplate')}" />
    <lay:showElement value="${evaluationSection?.index}" type="Integer" label="${message(code:'evaluationSection.index.label',default:'index')}" />
    <lay:showElement value="${evaluationSection?.hint}" type="String" label="${message(code:'evaluationSection.hint.label',default:'hint')}" />
</lay:showWidget>

<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'evaluationSection', action: 'edit', params: [encodedId: evaluationSection?.encodedId])}'"/>
    <btn:backButton goToPreviousLink="true"/>
</div>