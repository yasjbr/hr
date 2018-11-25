
<el:row/><br/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${evaluationItem?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'evaluationItem.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${evaluationItem?.evaluationSection}" type="EvaluationSection" label="${message(code:'evaluationItem.evaluationSection.label',default:'evaluationSection')}" />
    <lay:showElement value="${evaluationItem?.index}" type="Integer" label="${message(code:'evaluationItem.index.label',default:'index')}" />
    <lay:showElement value="${evaluationItem?.maxMark}" type="Double" label="${message(code:'evaluationItem.maxMark.label',default:'maxMark')}" />
    <lay:showElement value="${evaluationItem?.universalCode}" type="String" label="${message(code:'evaluationItem.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
