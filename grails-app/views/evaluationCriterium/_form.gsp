
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationCriterium?.descriptionInfo]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="evaluationTemplate" action="autocomplete" name="evaluationTemplate.id" label="${message(code:'evaluationCriterium.evaluationTemplate.label',default:'evaluationTemplate')}" values="${[[evaluationCriterium?.evaluationTemplate?.id,evaluationCriterium?.evaluationTemplate?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:decimalField name="fromMark" size="8"  class=" isRequired isDecimal" label="${message(code:'evaluationCriterium.fromMark.label',default:'fromMark')}"  value="${evaluationCriterium?.fromMark?evaluationCriterium?.fromMark:'0'}" />
</el:formGroup>
<el:formGroup>
    <el:decimalField name="toMark" size="8"  class=" isRequired isDecimal" label="${message(code:'evaluationCriterium.toMark.label',default:'toMark')}"  value="${evaluationCriterium?.toMark}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class=" isRequired" label="${message(code:'evaluationCriterium.universalCode.label',default:'universalCode')}" value="${evaluationCriterium?.universalCode}"/>
</el:formGroup>