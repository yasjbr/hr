
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationItem?.descriptionInfo, size:8]" />

<g:if test="${!hideSection}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="evaluationSection" action="autocomplete" name="evaluationSection.id" label="${message(code:'evaluationItem.evaluationSection.label',default:'evaluationSection')}" values="${[[evaluationItem?.evaluationSection?.id,evaluationItem?.evaluationSection?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:if>

<el:formGroup>
    <el:integerField name="index" size="8"  class=" isRequired isNumber" label="${message(code:'evaluationItem.index.label',default:'index')}" value="${evaluationItem?.index}" />
</el:formGroup>

<el:formGroup>
    <el:decimalField name="maxMark" size="8"  class=" isRequired isDecimal" label="${message(code:'evaluationItem.maxMark.label',default:'maxMark')}"  value="${evaluationItem?.maxMark}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8"  class=" isRequired" label="${message(code:'evaluationItem.universalCode.label',default:'universalCode')}" value="${evaluationItem?.universalCode}"/>
</el:formGroup>