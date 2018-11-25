<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationCriterium?.descriptionInfo,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="evaluationTemplate" action="autocomplete" name="evaluationTemplate.id" label="${message(code:'evaluationCriterium.evaluationTemplate.label',default:'evaluationTemplate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'evaluationCriterium.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    <el:decimalField name="fromMark" size="8"  class=" isDecimal" label="${message(code:'evaluationCriterium.fromMark.label',default:'fromMark')}"  />
</el:formGroup>
<el:formGroup>
    <el:decimalField name="toMark" size="8"  class=" isDecimal" label="${message(code:'evaluationCriterium.toMark.label',default:'toMark')}"  />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'evaluationCriterium.universalCode.label',default:'universalCode')}" />
</el:formGroup>
